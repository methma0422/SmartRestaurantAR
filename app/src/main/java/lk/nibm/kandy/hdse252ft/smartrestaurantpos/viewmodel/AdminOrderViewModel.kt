package lk.nibm.kandy.hdse252ft.smartrestaurantpos.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.data.model.Order
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.data.model.OrderStatus
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.data.repository.OrderRepository
import javax.inject.Inject

import kotlinx.coroutines.flow.combine

@HiltViewModel
class AdminOrderViewModel @Inject constructor(
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _updatingOrderId = MutableStateFlow<String?>(null)
    val updatingOrderId = _updatingOrderId.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    private val _statusFilter = MutableStateFlow("Active")
    val statusFilter = _statusFilter.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    init {
        orderRepository.startListeningToAllOrders()
    }

    val orders: StateFlow<List<Order>> = combine(
        orderRepository.getAllOrders(),
        _statusFilter,
        _searchQuery
    ) { rawOrders, filter, query ->
        rawOrders.filter { order ->
            val matchesFilter = when (filter) {
                "Active" -> order.status == OrderStatus.PENDING || order.status == OrderStatus.CONFIRMED || order.status == OrderStatus.READY
                "Pending" -> order.status == OrderStatus.PENDING
                "Confirmed" -> order.status == OrderStatus.CONFIRMED
                "Ready" -> order.status == OrderStatus.READY
                "Delivered" -> order.status == OrderStatus.DELIVERED
                "Cancelled" -> order.status == OrderStatus.CANCELLED
                "All" -> true
                else -> true
            }

            val matchesQuery = if (query.isBlank()) {
                true
            } else {
                order.tableNumber.contains(query, ignoreCase = true) ||
                order.id.contains(query, ignoreCase = true) ||
                order.items.any { it.menuItem.name.contains(query, ignoreCase = true) }
            }

            matchesFilter && matchesQuery
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun onStatusFilterChange(filter: String) {
        _statusFilter.value = filter
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun confirmOrder(orderId: String) = updateStatus(orderId, OrderStatus.CONFIRMED)

    fun markReady(orderId: String) = updateStatus(orderId, OrderStatus.READY)

    fun completeOrder(orderId: String) = updateStatus(orderId, OrderStatus.DELIVERED)

    fun cancelOrder(orderId: String) = updateStatus(orderId, OrderStatus.CANCELLED)

    private fun updateStatus(orderId: String, status: OrderStatus) {
        viewModelScope.launch {
            _updatingOrderId.value = orderId
            _errorMessage.value = null
            try {
                orderRepository.updateOrderStatus(orderId, status)
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "Failed to update order"
            } finally {
                _updatingOrderId.value = null
            }
        }
    }
}
