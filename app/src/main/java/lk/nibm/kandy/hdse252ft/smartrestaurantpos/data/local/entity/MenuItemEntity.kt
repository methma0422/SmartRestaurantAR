package lk.nibm.kandy.hdse252ft.smartrestaurantpos.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.data.model.Ingredient
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.data.model.MenuItem

@Entity(tableName = "menu_items")
data class MenuItemEntity(
    @PrimaryKey val id: String,
    val name: String,
    val category: String,
    val price: Double,
    val discountedPrice: Double?,
    val calories: Int,
    val isVegetarian: Boolean,
    val isNew: Boolean,
    val imageUrl: String,
    val ingredients: List<Ingredient>,
    val description: String
)

fun MenuItemEntity.toDomainModel() = MenuItem(
    id = id,
    name = name,
    category = category,
    price = price,
    discountedPrice = discountedPrice,
    calories = calories,
    isVegetarian = isVegetarian,
    isNew = isNew,
    imageUrl = imageUrl,
    ingredients = ingredients,
    description = description
)

fun MenuItem.toEntity() = MenuItemEntity(
    id = id,
    name = name,
    category = category,
    price = price,
    discountedPrice = discountedPrice,
    calories = calories,
    isVegetarian = isVegetarian,
    isNew = isNew,
    imageUrl = imageUrl,
    ingredients = ingredients,
    description = description
)
