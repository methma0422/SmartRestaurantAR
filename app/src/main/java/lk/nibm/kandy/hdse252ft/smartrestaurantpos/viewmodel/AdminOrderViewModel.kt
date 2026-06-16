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

@HiltViewModel
class AdminOrderViewModel @Inject constructor(
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _updatingOrderId = MutableStateFlow<String?>(null)
    val updatingOrderId = _updatingOrderId.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    init {
        orderRepository.startListeningToAllOrders()
    }

    val orders: StateFlow<List<Order>> = orderRepository.getAllOrders()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

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
