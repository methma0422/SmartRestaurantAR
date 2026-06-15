package lk.nibm.kandy.hdse252ft.smartrestaurantpos.data.local

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.data.model.CartItem
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.data.model.Ingredient
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.data.model.OrderStatus

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromIngredientList(value: List<Ingredient>?): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toIngredientList(value: String): List<Ingredient> {
        val listType = object : TypeToken<List<Ingredient>>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun fromCartItemList(value: List<CartItem>?): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toCartItemList(value: String): List<CartItem> {
        val listType = object : TypeToken<List<CartItem>>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun fromOrderStatus(value: OrderStatus): String {
        return value.name
    }

    @TypeConverter
    fun toOrderStatus(value: String): OrderStatus {
        return OrderStatus.valueOf(value)
    }
}
