package lk.nibm.kandy.hdse252ft.smartrestaurantpos.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
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
    private val locationRepository: LocationRepository,
    private val authRepository: AuthRepository
) {
    private var orderListener: ListenerRegistration? = null

    fun getAllOrders(): Flow<List<Order>> = orderDao.getAllOrders().map { entities ->
        entities.map { it.toDomainModel() }
    }

    suspend fun placeOrder(items: List<CartItem>, tableNumber: String, totalAmount: Double) {
        val location = locationRepository.getCurrentLocation()
        val userId = authRepository.currentUserId
        val order = Order(
            id = UUID.randomUUID().toString(),
            userId = userId,
            items = items,
            tableNumber = tableNumber,
            latitude = location?.latitude,
            longitude = location?.longitude,
            totalAmount = totalAmount,
            status = OrderStatus.PENDING,
            timestamp = System.currentTimeMillis()
        )

        // Save to Local DB
        orderDao.insertOrder(
            OrderEntity(
                id = order.id,
                userId = order.userId,
                items = order.items,
                tableNumber = order.tableNumber,
                latitude = order.latitude,
                longitude = order.longitude,
                totalAmount = order.totalAmount,
                status = order.status,
                timestamp = order.timestamp
            )
        )

        // Sync to Firestore
        try {
            firestore.collection("orders").document(order.id).set(order).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun startListeningToOrders() {
        val userId = authRepository.currentUserId
        if (userId.isEmpty()) return

        // Stop existing listener if any
        orderListener?.remove()

        orderListener = firestore.collection("orders")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    error.printStackTrace()
                    return@addSnapshotListener
                }

                snapshot?.let { querySnapshot ->
                    val remoteOrders = querySnapshot.toObjects(Order::class.java)
                    CoroutineScope(Dispatchers.IO).launch {
                        remoteOrders.forEach { order ->
                            orderDao.insertOrder(
                                OrderEntity(
                                    id = order.id,
                                    userId = order.userId,
                                    items = order.items,
                                    tableNumber = order.tableNumber,
                                    latitude = order.latitude,
                                    longitude = order.longitude,
                                    totalAmount = order.totalAmount,
                                    status = order.status,
                                    timestamp = order.timestamp
                                )
                            )
                        }
                    }
                }
            }
    }

    fun stopListeningToOrders() {
        orderListener?.remove()
        orderListener = null
    }
}
