package lk.nibm.kandy.hdse252ft.smartrestaurantpos.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.ui.admin.AdminDashboardScreen
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.ui.admin.AdminMenuFormScreen
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.ui.admin.AdminMenuScreen
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.ui.admin.AdminOrderScreen
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.ui.admin.AdminQRScreen
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.ui.ar.ARViewScreen
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.ui.auth.LoginScreen
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.ui.auth.SignUpScreen
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.ui.cart.CartScreen
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.ui.detail.ItemDetailScreen
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.ui.home.HomeScreen
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.ui.menu.MenuScreen
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.ui.order.OrderConfirmationScreen
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.ui.order.OrderHistoryScreen
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.ui.qr.QRScannerScreen
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.ui.splash.SplashScreen
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.viewmodel.CartViewModel

@Composable
fun NavGraph(deepLinkTableNumber: Int? = null) {
    val navController = rememberNavController()
    val cartViewModel: CartViewModel = hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                navController = navController,
                deepLinkTableNumber = deepLinkTableNumber
            )
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
        composable(
            route = Screen.Menu.route,
            arguments = listOf(
                navArgument(TABLE_NUMBER_ARG) {
                    type = NavType.IntType
                    defaultValue = 0
                }
            ),
            deepLinks = listOf(
                navDeepLink { uriPattern = "thegoldenoak://menu?table={$TABLE_NUMBER_ARG}" }
            )
        ) { backStackEntry ->
            val tableNumber = backStackEntry.arguments?.getInt(TABLE_NUMBER_ARG) ?: 0
            LaunchedEffect(tableNumber) {
                if (tableNumber > 0) {
                    cartViewModel.setTableFromQr(tableNumber)
                }
            }
            MenuScreen(
                navController = navController,
                tableNumber = tableNumber,
                cartViewModel = cartViewModel
            )
        }
        composable(
            route = Screen.ItemDetail.route,
            arguments = listOf(navArgument(ITEM_ID_ARG) { type = NavType.StringType })
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString(ITEM_ID_ARG)
            ItemDetailScreen(navController, itemId, cartViewModel = cartViewModel)
        }
        composable(
            route = Screen.ARView.route,
            arguments = listOf(navArgument(ITEM_ID_ARG) { type = NavType.StringType })
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString(ITEM_ID_ARG)
            ARViewScreen(navController, itemId)
        }
        composable(
            route = Screen.Cart.route,
            arguments = listOf(
                navArgument(TABLE_NUMBER_ARG) {
                    type = NavType.IntType
                    defaultValue = 0
                }
            )
        ) { backStackEntry ->
            val tableNumber = backStackEntry.arguments?.getInt(TABLE_NUMBER_ARG) ?: 0
            LaunchedEffect(tableNumber) {
                if (tableNumber > 0) {
                    cartViewModel.setTableFromQr(tableNumber)
                }
            }
            CartScreen(
                navController = navController,
                tableNumber = tableNumber,
                viewModel = cartViewModel
            )
        }
        composable(Screen.QRScanner.route) {
            QRScannerScreen(navController, cartViewModel = cartViewModel)
        }
        composable(Screen.OrderHistory.route) {
            OrderHistoryScreen(navController)
        }
        composable(
            route = Screen.OrderConfirmation.route,
            arguments = listOf(navArgument(ORDER_ID_ARG) { type = NavType.StringType })
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString(ORDER_ID_ARG) ?: ""
            OrderConfirmationScreen(
                navController = navController,
                orderId = orderId
            )
        }
        composable(Screen.AdminDashboard.route) {
            AdminDashboardScreen(navController)
        }
        composable(Screen.AdminMenu.route) {
            AdminMenuScreen(navController)
        }
        composable(
            route = Screen.AdminMenuForm.route,
            arguments = listOf(
                navArgument(ITEM_ID_ARG) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString(ITEM_ID_ARG)
            AdminMenuFormScreen(navController, itemId)
        }
        composable(Screen.AdminOrders.route) {
            AdminOrderScreen(navController)
        }
        composable(Screen.AdminQR.route) {
            AdminQRScreen(navController)
        }
    }
}
