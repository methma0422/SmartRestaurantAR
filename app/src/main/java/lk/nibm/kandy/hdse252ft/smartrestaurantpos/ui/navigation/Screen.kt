package lk.nibm.kandy.hdse252ft.smartrestaurantpos.ui.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object SignUp : Screen("signup")
    object Home : Screen("home")
    object Menu : Screen("menu?table={table}") {
        fun createRoute(tableNumber: Int = 0) = "menu?table=$tableNumber"
    }
    object ItemDetail : Screen("itemDetail/{itemId}") {
        fun createRoute(itemId: String) = "itemDetail/$itemId"
    }
    object ARView : Screen("arView/{itemId}") {
        fun createRoute(itemId: String) = "arView/$itemId"
    }
    object Cart : Screen("cart?table={table}") {
        fun createRoute(tableNumber: Int = 0) = "cart?table=$tableNumber"
    }
    object QRScanner : Screen("qrScanner")
    object OrderHistory : Screen("orderHistory")
    object OrderConfirmation : Screen("orderConfirmation/{orderId}") {
        fun createRoute(orderId: String) = "orderConfirmation/$orderId"
    }
    object AdminDashboard : Screen("adminDashboard")
    object AdminMenu : Screen("adminMenu")
    object AdminMenuForm : Screen("adminMenuForm?itemId={itemId}") {
        fun createRoute(itemId: String? = null) =
            if (itemId != null) "adminMenuForm?itemId=$itemId" else "adminMenuForm"
    }
    object AdminOrders : Screen("adminOrders")
    object AdminQR : Screen("adminQR")
}

const val TABLE_NUMBER_ARG = "table"
const val ORDER_ID_ARG = "orderId"
const val ITEM_ID_ARG = "itemId"
