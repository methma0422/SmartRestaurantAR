package lk.nibm.kandy.hdse252ft.smartrestaurantpos.data.model

data class CartItem(
    val menuItem: MenuItem = MenuItem(),
    val quantity: Int = 0
)
