package lk.nibm.kandy.hdse252ft.smartrestaurantpos.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collect
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.data.model.CartItem
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.data.model.MenuItem
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.data.model.Order
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.data.model.OrderStatus
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.data.repository.OrderRepository
import javax.inject.Inject
import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext

@HiltViewModel
class CartViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val sharedPrefs = context.getSharedPreferences("restaurant_prefs", Context.MODE_PRIVATE)

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    private val _tableNumber = MutableStateFlow("")
    val tableNumber: StateFlow<String> = _tableNumber.asStateFlow()

    private val _isTableLocked = MutableStateFlow(false)
    val isTableLocked: StateFlow<Boolean> = _isTableLocked.asStateFlow()

    private val prefListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        if (key == "locked_table_number") {
            val savedTable = sharedPrefs.getString("locked_table_number", "") ?: ""
            _tableNumber.value = savedTable
            if (savedTable.isNotBlank()) {
                _isTableLocked.value = true
            }
        }
    }

    init {
        sharedPrefs.registerOnSharedPreferenceChangeListener(prefListener)
        val savedTable = sharedPrefs.getString("locked_table_number", "") ?: ""
        if (savedTable.isNotBlank()) {
            _tableNumber.value = savedTable
            _isTableLocked.value = true
        }

        viewModelScope.launch {
            orderRepository.getAllOrders().collect { orders ->
                val lastOrderId = sharedPrefs.getString("last_placed_order_id", "") ?: ""
                if (lastOrderId.isNotBlank()) {
                    val order = orders.find { it.id == lastOrderId }
                    if (order != null && order.status == OrderStatus.CANCELLED) {
                        val timeElapsedMs = System.currentTimeMillis() - order.timestamp
                        val fiveMinutesMs = 5 * 60 * 1000
                        if (timeElapsedMs < fiveMinutesMs) {
                            clearTableNumber()
                        }
                        sharedPrefs.edit().remove("last_placed_order_id").apply()
                    }
                }
            }
        }
    }

    fun clearTableNumber() {
        _tableNumber.value = ""
        _isTableLocked.value = false
        sharedPrefs.edit().remove("locked_table_number").apply()
    }

    private val _isPlacingOrder = MutableStateFlow(false)
    val isPlacingOrder: StateFlow<Boolean> = _isPlacingOrder.asStateFlow()

    private val _placeOrderError = MutableStateFlow<String?>(null)
    val placeOrderError: StateFlow<String?> = _placeOrderError.asStateFlow()

    val activeOrder: StateFlow<Order?> = combine(
        orderRepository.getAllOrders(),
        _tableNumber
    ) { orders, table ->
        orders.find { order ->
            (order.status == OrderStatus.PENDING || 
             order.status == OrderStatus.CONFIRMED || 
             order.status == OrderStatus.READY) &&
            order.tableNumber == table
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val totalAmount: Double
        get() = _cartItems.value.sumOf { it.menuItem.price * it.quantity }

    fun addToCart(menuItem: MenuItem) {
        _cartItems.update { currentItems ->
            val existingItem = currentItems.find { it.menuItem.id == menuItem.id }
            if (existingItem != null) {
                currentItems.map {
                    if (it.menuItem.id == menuItem.id) it.copy(quantity = it.quantity + 1) else it
                }
            } else {
                currentItems + CartItem(menuItem, 1)
            }
        }
    }

    fun removeFromCart(menuItemId: String) {
        _cartItems.update { currentItems ->
            currentItems.filter { it.menuItem.id != menuItemId }
        }
    }

    fun updateQuantity(menuItemId: String, delta: Int) {
        _cartItems.update { currentItems ->
            currentItems.mapNotNull {
                if (it.menuItem.id == menuItemId) {
                    val newQty = it.quantity + delta
                    if (newQty > 0) it.copy(quantity = newQty) else null
                } else it
            }
        }
    }

    fun setTableNumber(number: String) {
        val savedTable = sharedPrefs.getString("locked_table_number", "") ?: ""
        if (savedTable.isBlank()) {
            _tableNumber.value = number
            if (number.isNotBlank()) {
                _isTableLocked.value = true
                sharedPrefs.edit().putString("locked_table_number", number).apply()
                viewModelScope.launch {
                    orderRepository.assignUnassignedOrdersToTable(number)
                }
            }
        } else {
            _tableNumber.value = savedTable
            _isTableLocked.value = true
            if (savedTable != number && number.isNotBlank()) {
                android.widget.Toast.makeText(
                    context,
                    "This device is already locked to Table $savedTable.",
                    android.widget.Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    fun setTableFromQr(tableNumber: Int) {
        if (tableNumber > 0) {
            val savedTable = sharedPrefs.getString("locked_table_number", "") ?: ""
            if (savedTable.isBlank()) {
                _tableNumber.value = tableNumber.toString()
                _isTableLocked.value = true
                sharedPrefs.edit().putString("locked_table_number", tableNumber.toString()).apply()
                viewModelScope.launch {
                    orderRepository.assignUnassignedOrdersToTable(tableNumber.toString())
                }
            } else {
                _tableNumber.value = savedTable
                _isTableLocked.value = true
                if (savedTable != tableNumber.toString()) {
                    android.widget.Toast.makeText(
                        context,
                        "This device is already locked to Table $savedTable.",
                        android.widget.Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    fun placeOrder(onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            if (_cartItems.value.isEmpty()) {
                _placeOrderError.value = "Your cart is empty"
                return@launch
            }

            _isPlacingOrder.value = true
            _placeOrderError.value = null

            try {
                val currentActiveOrder = activeOrder.value
                val orderId: String
                if (currentActiveOrder != null) {
                    val mergedItems = currentActiveOrder.items.toMutableList()
                    _cartItems.value.forEach { cartItem ->
                        val existing = mergedItems.find { it.menuItem.id == cartItem.menuItem.id }
                        if (existing != null) {
                            val index = mergedItems.indexOf(existing)
                            mergedItems[index] = existing.copy(quantity = existing.quantity + cartItem.quantity)
                        } else {
                            mergedItems.add(cartItem)
                        }
                    }
                    val newTotal = mergedItems.sumOf { it.menuItem.price * it.quantity }
                    orderRepository.updateOrderItems(
                        orderId = currentActiveOrder.id,
                        items = mergedItems,
                        totalAmount = newTotal,
                        resetTimestamp = true
                    )
                    orderId = currentActiveOrder.id
                } else {
                    orderId = orderRepository.placeOrder(
                        items = _cartItems.value,
                        tableNumber = _tableNumber.value,
                        totalAmount = totalAmount
                    )
                }
                _cartItems.value = emptyList()
                sharedPrefs.edit().putString("last_placed_order_id", orderId).apply()
                onSuccess(orderId)
            } catch (e: Exception) {
                _placeOrderError.value = e.localizedMessage ?: "Unable to place order"
            } finally {
                _isPlacingOrder.value = false
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        sharedPrefs.unregisterOnSharedPreferenceChangeListener(prefListener)
    }
}
