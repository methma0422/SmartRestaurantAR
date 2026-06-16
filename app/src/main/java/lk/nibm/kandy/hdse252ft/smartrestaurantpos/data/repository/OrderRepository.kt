package lk.nibm.kandy.hdse252ft.smartrestaurantpos.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.data.local.dao.OrderDao
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.data.local.entity.OrderEntity
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.data.local.entity.toDomainModel
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.data.model.CartItem
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.data.model.Order
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.data.model.OrderStatus
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderRepository @Inject constructor(
    private val orderDao: OrderDao,
    private val firestore: FirebaseFirestore,
    private val authRepository: AuthRepository
) {
    private var orderListener: ListenerRegistration? = null

    fun getAllOrders(): Flow<List<Order>> = orderDao.getAllOrders().map { entities ->
        entities.map { it.toDomainModel() }
    }

    suspend fun getOrderById(orderId: String): Order? {
        return orderDao.getOrderById(orderId)?.toDomainModel()
    }

    fun getOrderByIdFlow(orderId: String): Flow<Order?> {
        return orderDao.getAllOrders().map { list ->
            list.find { it.id == orderId }?.toDomainModel()
        }
    }

    suspend fun placeOrder(items: List<CartItem>, tableNumber: String, totalAmount: Double): String {
        val order = Order(
            id = UUID.randomUUID().toString(),
            userId = authRepository.currentUserId,
            items = items,
            tableNumber = tableNumber,
            latitude = null,
            longitude = null,
            totalAmount = totalAmount,
            discount = 0.0,
            status = OrderStatus.PENDING,
            timestamp = System.currentTimeMillis()
        )

        orderDao.insertOrder(order.toEntity())

        try {
            firestore.collection("orders").document(order.id).set(order).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return order.id
    }

    suspend fun updateOrderStatus(orderId: String, status: OrderStatus) {
        val existing = orderDao.getOrderById(orderId)
            ?: throw IllegalStateException("Order not found locally")

        val updated = existing.copy(status = status)
        orderDao.insertOrder(updated)

        try {
            firestore.collection("orders").document(orderId)
                .set(updated.toDomainModel()).await()
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    suspend fun updateOrderItems(orderId: String, items: List<CartItem>, totalAmount: Double, resetTimestamp: Boolean = false) {
        val existing = orderDao.getOrderById(orderId)
            ?: throw IllegalStateException("Order not found locally")

        val updated = existing.copy(
            items = items,
            totalAmount = totalAmount,
            timestamp = if (resetTimestamp) System.currentTimeMillis() else existing.timestamp,
            status = if (resetTimestamp) OrderStatus.PENDING else existing.status
        )

        orderDao.insertOrder(updated)

        try {
            firestore.collection("orders").document(orderId)
                .set(updated.toDomainModel()).await()
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    suspend fun assignUnassignedOrdersToTable(tableNumber: String) {
        try {
            val entities = orderDao.getAllOrders().firstOrNull() ?: emptyList()
            val unassignedOrders = entities.filter {
                (it.tableNumber.isBlank() || it.tableNumber == "0") &&
                (it.status == OrderStatus.PENDING || it.status == OrderStatus.CONFIRMED || it.status == OrderStatus.READY)
            }

            unassignedOrders.forEach { orderEntity ->
                val updatedEntity = orderEntity.copy(tableNumber = tableNumber)
                orderDao.insertOrder(updatedEntity)
                try {
                    firestore.collection("orders").document(orderEntity.id)
                        .set(updatedEntity.toDomainModel()).await()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun updateOrderDiscount(orderId: String, discount: Double) {
        val existing = orderDao.getOrderById(orderId)
            ?: throw IllegalStateException("Order not found locally")

        val updated = existing.copy(discount = discount)
        orderDao.insertOrder(updated)

        try {
            firestore.collection("orders").document(orderId)
                .set(updated.toDomainModel()).await()
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }


    fun startListeningToAllOrders() {
        orderListener?.remove()

        orderListener = firestore.collection("orders")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    error.printStackTrace()
                    return@addSnapshotListener
                }

                snapshot?.let { querySnapshot ->
                    val remoteOrders = querySnapshot.toObjects(Order::class.java)
                    CoroutineScope(Dispatchers.IO).launch {
                        remoteOrders.forEach { order ->
                            orderDao.insertOrder(order.toEntity())
                        }
                    }
                }
            }
    }

    fun startListeningToOrders() {
        startListeningToAllOrders()
    }

    fun stopListeningToOrders() {
        orderListener?.remove()
        orderListener = null
    }

    private fun Order.toEntity() = OrderEntity(
        id = id,
        userId = userId,
        items = items,
        tableNumber = tableNumber,
        latitude = latitude,
        longitude = longitude,
        totalAmount = totalAmount,
        discount = discount,
        status = status,
        timestamp = timestamp
    )
}
