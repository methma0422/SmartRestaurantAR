package lk.nibm.kandy.hdse252ft.smartrestaurantpos.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.data.model.MenuItem
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.data.model.Order
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.data.model.OrderStatus
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.data.repository.MenuRepository
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.data.repository.OrderRepository
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.data.repository.AuthRepository
import javax.inject.Inject
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext

enum class DietaryFilter {
    All,
    Vegetarian,
    NonVegetarian
}

@HiltViewModel
class MenuViewModel @Inject constructor(
    private val repository: MenuRepository,
    private val orderRepository: OrderRepository,
    private val authRepository: AuthRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val sharedPrefs = context.getSharedPreferences("restaurant_prefs", Context.MODE_PRIVATE)

    private val _tableNumber = MutableStateFlow(0)
    val tableNumber = _tableNumber.asStateFlow()

    fun setTableNumber(table: Int) {
        val savedTable = sharedPrefs.getString("locked_table_number", "") ?: ""
        if (savedTable.isNotBlank()) {
            _tableNumber.value = savedTable.toIntOrNull() ?: 0
        } else if (table > 0) {
            _tableNumber.value = table
        }
    }

    val activeOrders: StateFlow<List<Order>> = combine(
        orderRepository.getAllOrders(),
        _tableNumber
    ) { orders, table ->
        orders.filter { order ->
            (order.status == OrderStatus.PENDING || 
             order.status == OrderStatus.CONFIRMED || 
             order.status == OrderStatus.READY) &&
            (order.tableNumber == table.toString() || 
             (order.userId.isNotBlank() && order.userId == authRepository.currentUserId))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory = _selectedCategory.asStateFlow()

    private val _dietaryFilter = MutableStateFlow(DietaryFilter.All)
    val dietaryFilter = _dietaryFilter.asStateFlow()

    private val _isSyncing = MutableStateFlow(true)
    val isSyncing = _isSyncing.asStateFlow()

    private val _syncError = MutableStateFlow<String?>(null)
    val syncError = _syncError.asStateFlow()

    val categories = repository.getCategories()
        .map { listOf("All") + it }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), listOf("All"))

    val menuItems = combine(
        repository.getAllMenuItems(),
        _searchQuery,
        _selectedCategory,
        _dietaryFilter
    ) { items, query, category, filter ->
        items.filter { item ->
            (category == "All" || item.category == category) &&
            (item.name.contains(query, ignoreCase = true)) &&
            when (filter) {
                DietaryFilter.All -> true
                DietaryFilter.Vegetarian -> item.isVegetarian
                DietaryFilter.NonVegetarian -> !item.isVegetarian
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        val savedTable = sharedPrefs.getString("locked_table_number", "") ?: ""
        if (savedTable.isNotBlank()) {
            _tableNumber.value = savedTable.toIntOrNull() ?: 0
        }
        syncMenu()
        orderRepository.startListeningToOrders()
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onCategorySelect(category: String) {
        _selectedCategory.value = category
    }

    fun onDietaryFilterSelect(filter: DietaryFilter) {
        _dietaryFilter.value = filter
    }

    private fun syncMenu() {
        viewModelScope.launch {
            _isSyncing.value = true
            _syncError.value = null
            try {
                repository.seedFirestoreIfEmpty()
                repository.syncMenuWithRemote()
            } catch (e: Exception) {
                _syncError.value = e.localizedMessage ?: "Unable to load menu right now"
            } finally {
                _isSyncing.value = false
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        orderRepository.stopListeningToOrders()
    }
}
