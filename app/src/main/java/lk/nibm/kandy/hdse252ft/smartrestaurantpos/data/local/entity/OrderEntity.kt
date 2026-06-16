package lk.nibm.kandy.hdse252ft.smartrestaurantpos.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.data.model.CartItem
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.data.model.Order
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.data.model.OrderStatus

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val items: List<CartItem>,
    val tableNumber: String,
    val latitude: Double?,
    val longitude: Double?,
    val totalAmount: Double,
    val discount: Double = 0.0,
    val status: OrderStatus,
    val timestamp: Long,
    val paymentMethod: String? = null,
    val isPaid: Boolean = false,
    val serviceCharge: Double = 0.0,
    val taxAmount: Double = 0.0,
    val finalTotal: Double = 0.0
)

fun OrderEntity.toDomainModel() = Order(
    id = id,
    userId = userId,
    items = items,
    tableNumber = tableNumber,
    latitude = latitude,
    longitude = longitude,
    totalAmount = totalAmount,
    discount = discount,
    status = status,
    timestamp = timestamp,
    paymentMethod = paymentMethod,
    isPaid = isPaid,
    serviceCharge = serviceCharge,
    taxAmount = taxAmount,
    finalTotal = finalTotal
)
