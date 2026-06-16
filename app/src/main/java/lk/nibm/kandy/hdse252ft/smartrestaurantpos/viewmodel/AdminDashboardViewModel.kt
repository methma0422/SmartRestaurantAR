package lk.nibm.kandy.hdse252ft.smartrestaurantpos.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.data.model.OrderStatus
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.data.repository.OrderRepository
import javax.inject.Inject

data class OrderSummaryCounts(
    val pending: Int = 0,
    val confirmed: Int = 0,
    val ready: Int = 0
)

@HiltViewModel
class AdminDashboardViewModel @Inject constructor(
    orderRepository: OrderRepository
) : ViewModel() {

    init {
        orderRepository.startListeningToAllOrders()
    }

    val orderCounts: StateFlow<OrderSummaryCounts> = orderRepository.getAllOrders()
        .map { orders ->
            OrderSummaryCounts(
                pending = orders.count { it.status == OrderStatus.PENDING },
                confirmed = orders.count { it.status == OrderStatus.CONFIRMED },
                ready = orders.count { it.status == OrderStatus.READY }
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = OrderSummaryCounts()
        )
}
