package lk.nibm.kandy.hdse252ft.smartrestaurantpos.data.model

data class User(
    val uid: String = "",
    val email: String = "",
    val role: UserRole = UserRole.ADMIN
)
