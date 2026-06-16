package lk.nibm.kandy.hdse252ft.smartrestaurantpos.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.data.model.Order
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.data.repository.OrderRepository
import javax.inject.Inject

import lk.nibm.kandy.hdse252ft.smartrestaurantpos.data.model.OrderStatus

@HiltViewModel
class OrderConfirmationViewModel @Inject constructor(
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _order = MutableStateFlow<Order?>(null)
    val order = _order.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    fun loadOrder(orderId: String) {
        orderRepository.startListeningToOrders()
        viewModelScope.launch {
            _isLoading.value = true
            orderRepository.getOrderByIdFlow(orderId).collect { updatedOrder ->
                _order.value = updatedOrder
                _isLoading.value = false
            }
        }
    }

    fun cancelOrder(orderId: String) {
        viewModelScope.launch {
            try {
                orderRepository.updateOrderStatus(orderId, OrderStatus.CANCELLED)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateOrderItemQuantity(orderId: String, menuItemId: String, delta: Int) {
        viewModelScope.launch {
            val currentOrder = _order.value ?: return@launch
            val updatedItems = currentOrder.items.mapNotNull { item ->
                if (item.menuItem.id == menuItemId) {
                    val newQty = item.quantity + delta
                    if (newQty > 0) item.copy(quantity = newQty) else null
                } else {
                    item
                }
            }
            if (updatedItems.isEmpty()) {
                orderRepository.updateOrderStatus(orderId, OrderStatus.CANCELLED)
            } else {
                val newTotal = updatedItems.sumOf { it.menuItem.price * it.quantity }
                orderRepository.updateOrderItems(orderId, updatedItems, newTotal, resetTimestamp = false)
            }
        }
    }

    fun removeOrderItem(orderId: String, menuItemId: String) {
        viewModelScope.launch {
            val currentOrder = _order.value ?: return@launch
            val updatedItems = currentOrder.items.filter { it.menuItem.id != menuItemId }
            if (updatedItems.isEmpty()) {
                orderRepository.updateOrderStatus(orderId, OrderStatus.CANCELLED)
            } else {
                val newTotal = updatedItems.sumOf { it.menuItem.price * it.quantity }
                orderRepository.updateOrderItems(orderId, updatedItems, newTotal, resetTimestamp = false)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        orderRepository.stopListeningToOrders()
    }
}
