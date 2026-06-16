package lk.nibm.kandy.hdse252ft.smartrestaurantpos.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.data.model.CartItem
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.data.model.MenuItem
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.data.repository.OrderRepository
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    private val _tableNumber = MutableStateFlow("")
    val tableNumber: StateFlow<String> = _tableNumber.asStateFlow()

    private val _isTableLocked = MutableStateFlow(false)
    val isTableLocked: StateFlow<Boolean> = _isTableLocked.asStateFlow()

    private val _isPlacingOrder = MutableStateFlow(false)
    val isPlacingOrder: StateFlow<Boolean> = _isPlacingOrder.asStateFlow()

    private val _placeOrderError = MutableStateFlow<String?>(null)
    val placeOrderError: StateFlow<String?> = _placeOrderError.asStateFlow()

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
        if (!_isTableLocked.value) {
            _tableNumber.value = number
        }
    }

    fun setTableFromQr(tableNumber: Int) {
        if (tableNumber > 0) {
            _tableNumber.value = tableNumber.toString()
            _isTableLocked.value = true
        }
    }

    fun placeOrder(onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            if (_cartItems.value.isEmpty()) {
                _placeOrderError.value = "Your cart is empty"
                return@launch
            }
            if (_tableNumber.value.isBlank()) {
                _placeOrderError.value = "Enter a table number before placing the order"
                return@launch
            }

            _isPlacingOrder.value = true
            _placeOrderError.value = null

            try {
                val orderId = orderRepository.placeOrder(
                    items = _cartItems.value,
                    tableNumber = _tableNumber.value,
                    totalAmount = totalAmount
                )
                _cartItems.value = emptyList()
                onSuccess(orderId)
            } catch (e: Exception) {
                _placeOrderError.value = e.localizedMessage ?: "Unable to place order"
            } finally {
                _isPlacingOrder.value = false
            }
        }
    }
}
