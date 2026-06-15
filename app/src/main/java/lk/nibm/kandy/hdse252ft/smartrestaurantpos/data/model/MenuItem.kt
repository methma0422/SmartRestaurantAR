package lk.nibm.kandy.hdse252ft.smartrestaurantpos.data.model

data class MenuItem(
    val id: String = "",
    val name: String = "",
    val category: String = "",
    val price: Double = 0.0,
    val discountedPrice: Double? = null,
    val calories: Int = 0,
    val isVegetarian: Boolean = false,
    val isNew: Boolean = false,
    val imageUrl: String = "",
    val ingredients: List<Ingredient> = emptyList(),
    val description: String = ""
)
