package lk.nibm.kandy.hdse252ft.smartrestaurantpos.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination

private data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val routeMatcher: (String) -> Boolean
)

@Composable
fun RestaurantBottomBar(
    currentRoute: String,
    tableNumber: Int = 0,
    onNavigate: (String) -> Unit
) {
    val bottomNavItems = listOf(
        BottomNavItem(
            route = Screen.Menu.createRoute(tableNumber),
            label = "Menu",
            icon = Icons.Default.RestaurantMenu,
            routeMatcher = { it.startsWith("menu") }
        ),
        BottomNavItem(
            route = Screen.Cart.createRoute(tableNumber),
            label = "Cart",
            icon = Icons.Default.ShoppingCart,
            routeMatcher = { it.startsWith("cart") }
        ),
        BottomNavItem(
            route = Screen.OrderHistory.route,
            label = "Orders",
            icon = Icons.Default.History,
            routeMatcher = { it == Screen.OrderHistory.route }
        )
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        bottomNavItems.forEach { item ->
            NavigationBarItem(
                selected = item.routeMatcher(currentRoute),
                onClick = { if (!item.routeMatcher(currentRoute)) onNavigate(item.route) },
                icon = { Icon(imageVector = item.icon, contentDescription = item.label) },
                label = { Text(item.label) }
            )
        }
    }
}

fun NavController.navigateToTopLevel(route: String) {
    navigate(route) {
        launchSingleTop = true
        restoreState = true
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
    }
}
