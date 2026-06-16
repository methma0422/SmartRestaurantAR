package lk.nibm.kandy.hdse252ft.smartrestaurantpos.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.data.local.entity.MenuItemEntity

@Dao
interface MenuItemDao {
    @Query("SELECT * FROM menu_items")
    fun getAllItems(): Flow<List<MenuItemEntity>>

    @Query("SELECT * FROM menu_items WHERE category = :category")
    fun getItemsByCategory(category: String): Flow<List<MenuItemEntity>>

    @Query("SELECT * FROM menu_items WHERE name LIKE '%' || :query || '%'")
    fun searchItems(query: String): Flow<List<MenuItemEntity>>

    @Query("SELECT DISTINCT category FROM menu_items")
    fun getCategories(): Flow<List<String>>

    @Query("SELECT * FROM menu_items WHERE id = :itemId LIMIT 1")
    suspend fun getItemById(itemId: String): MenuItemEntity?

    @Insert
    suspend fun insertItem(item: MenuItemEntity)

    @Update
    suspend fun updateItem(item: MenuItemEntity)

    @Delete
    suspend fun deleteItem(item: MenuItemEntity)

    @Upsert
    suspend fun upsertAll(items: List<MenuItemEntity>)
}
