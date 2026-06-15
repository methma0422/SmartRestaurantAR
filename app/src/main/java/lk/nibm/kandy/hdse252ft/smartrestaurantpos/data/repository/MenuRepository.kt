package lk.nibm.kandy.hdse252ft.smartrestaurantpos.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.data.local.SeedData
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.data.local.dao.MenuItemDao
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.data.local.entity.toDomainModel
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.data.local.entity.toEntity
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.data.model.MenuItem
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MenuRepository @Inject constructor(
    private val menuItemDao: MenuItemDao,
    private val firestore: FirebaseFirestore
) {
    fun getAllMenuItems(): Flow<List<MenuItem>> = 
        menuItemDao.getAllItems().map { entities -> entities.map { it.toDomainModel() } }

    fun getMenuItemsByCategory(category: String): Flow<List<MenuItem>> =
        menuItemDao.getItemsByCategory(category).map { entities -> entities.map { it.toDomainModel() } }

    fun searchMenuItems(query: String): Flow<List<MenuItem>> =
        menuItemDao.searchItems(query).map { entities -> entities.map { it.toDomainModel() } }

    fun getCategories(): Flow<List<String>> = menuItemDao.getCategories()

    suspend fun syncMenuWithRemote() {
        try {
            val snapshot = firestore.collection("menu").get().await()
            val remoteItems = snapshot.toObjects(MenuItem::class.java)
            menuItemDao.upsertAll(remoteItems.map { it.toEntity() })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun seedFirestoreIfEmpty() {
        try {
            val snapshot = firestore.collection("menu").get().await()
            
            if (snapshot.isEmpty) {
                val seedItems = SeedData.getSampleMenuItems()
                
                seedItems.forEach { menuItem ->
                    firestore.collection("menu").document(menuItem.id).set(menuItem).await()
                }
                
                println("Successfully seeded ${seedItems.size} menu items to Firestore")
            } else {
                println("Firestore 'menu' collection already contains ${snapshot.size()} items. Skipping seed.")
            }
        } catch (e: Exception) {
            println("Error seeding Firestore: ${e.message}")
            e.printStackTrace()
        }
    }
}
