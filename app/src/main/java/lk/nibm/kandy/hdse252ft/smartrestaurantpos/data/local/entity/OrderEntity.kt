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
    val status: OrderStatus,
    val timestamp: Long
)

fun OrderEntity.toDomainModel() = Order(
    id = id,
    userId = userId,
    items = items,
    tableNumber = tableNumber,
    latitude = latitude,
    longitude = longitude,
    totalAmount = totalAmount,
    status = status,
    timestamp = timestamp
)
