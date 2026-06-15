package lk.nibm.kandy.hdse252ft.smartrestaurantpos.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.ui.ar.ARViewScreen
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.ui.auth.LoginScreen
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.ui.auth.SignUpScreen
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.ui.cart.CartScreen
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.ui.detail.ItemDetailScreen
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.ui.home.HomeScreen
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.ui.menu.MenuScreen
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.ui.order.OrderHistoryScreen
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.ui.qr.QRScannerScreen
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.ui.splash.SplashScreen
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.viewmodel.CartViewModel

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val cartViewModel: CartViewModel = hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(navController)
        }
        composable(Screen.Login.route) {
            LoginScreen(navController)
        }
        composable(Screen.SignUp.route) {
            SignUpScreen(navController)
        }
        composable(Screen.Home.route) {
            HomeScreen(navController)
        }
        composable(Screen.Menu.route) {
            MenuScreen(navController)
        }
        composable(
            route = Screen.ItemDetail.route,
            arguments = listOf(navArgument("itemId") { type = NavType.StringType })
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString("itemId")
            ItemDetailScreen(navController, itemId, cartViewModel = cartViewModel)
        }
        composable(
            route = Screen.ARView.route,
            arguments = listOf(navArgument("itemId") { type = NavType.StringType })
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString("itemId")
            ARViewScreen(navController, itemId)
        }
        composable(Screen.Cart.route) {
            CartScreen(navController, viewModel = cartViewModel)
        }
        composable(Screen.QRScanner.route) {
            QRScannerScreen(navController, cartViewModel = cartViewModel)
        }
        composable(Screen.OrderHistory.route) {
            OrderHistoryScreen(navController)
        }
    }
}
