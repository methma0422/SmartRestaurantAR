package lk.nibm.kandy.hdse252ft.smartrestaurantpos.data.model

data class Order(
    val id: String = "",
    val userId: String = "",
    val items: List<CartItem> = emptyList(),
    val tableNumber: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val totalAmount: Double = 0.0,
    val discount: Double = 0.0,
    val status: OrderStatus = OrderStatus.PENDING,
    val timestamp: Long = System.currentTimeMillis()
)
