package lk.nibm.kandy.hdse252ft.smartrestaurantpos.ui.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object SignUp : Screen("signup")
    object Home : Screen("home")
    object Menu : Screen("menu")
    object ItemDetail : Screen("itemDetail/{itemId}") {
        fun createRoute(itemId: String) = "itemDetail/$itemId"
    }
    object ARView : Screen("arView/{itemId}") {
        fun createRoute(itemId: String) = "arView/$itemId"
    }
    object Cart : Screen("cart")
    object QRScanner : Screen("qrScanner")
    object OrderHistory : Screen("orderHistory")
}
