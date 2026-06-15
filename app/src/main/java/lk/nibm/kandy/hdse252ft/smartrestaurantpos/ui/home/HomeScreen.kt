package lk.nibm.kandy.hdse252ft.smartrestaurantpos.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.data.model.MenuItem
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.ui.navigation.Screen
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.ui.theme.GoldPrimary
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.ui.theme.GoldLight
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.viewmodel.AuthViewModel
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.viewmodel.MenuViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    authViewModel: AuthViewModel = hiltViewModel(),
    menuViewModel: MenuViewModel = hiltViewModel()
) {
    val menuItems by menuViewModel.menuItems.collectAsState()
    val email by authViewModel.email.collectAsState()
    val displayEmail = email.ifEmpty() { "Valued Guest" }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("The Golden Oak", color = GoldPrimary, fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = {
                        authViewModel.logout()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Logout", tint = GoldPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
        ) {
            // Hero section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Welcome Back,",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = displayEmail,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Quick Actions Cards Grid
            Text(
                text = "Quick Navigation",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = CreamWhiteOrGoldPrimary(true),
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                NavigationCard(
                    title = "Menu",
                    subtitle = "Explore Dishes",
                    icon = Icons.Default.RestaurantMenu,
                    modifier = Modifier.weight(1f)
                ) {
                    navController.navigate(Screen.Menu.route)
                }

                NavigationCard(
                    title = "Scan Table",
                    subtitle = "Quick Check-in",
                    icon = Icons.Default.QrCodeScanner,
                    modifier = Modifier.weight(1f)
                ) {
                    navController.navigate(Screen.QRScanner.route)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                NavigationCard(
                    title = "My Cart",
                    subtitle = "Verify Order",
                    icon = Icons.Default.ShoppingCart,
                    modifier = Modifier.weight(1f)
                ) {
                    navController.navigate(Screen.Cart.route)
                }

                NavigationCard(
                    title = "History",
                    subtitle = "Past Orders",
                    icon = Icons.Default.History,
                    modifier = Modifier.weight(1f)
                ) {
                    navController.navigate(Screen.OrderHistory.route)
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Featured / New Items Carousel
            val featuredItems = menuItems.filter { it.isNew || it.discountedPrice != null }
            if (featuredItems.isNotEmpty()) {
                Text(
                    text = "Chef's Recommendations",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = CreamWhiteOrGoldPrimary(true),
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(featuredItems) { item ->
                        FeaturedItemCard(item = item) {
                            navController.navigate(Screen.ItemDetail.createRoute(item.id))
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun NavigationCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        modifier = modifier.height(110.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = GoldPrimary,
                modifier = Modifier.size(28.dp)
            )
            Column {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun FeaturedItemCard(
    item: MenuItem,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier
            .width(220.dp)
            .height(240.dp)
    ) {
        Column {
            AsyncImage(
                model = item.imageUrl,
                contentDescription = item.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = item.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = item.category,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Rs. ${item.price}",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp,
                        color = GoldPrimary
                    )
                    if (item.isNew) {
                        Surface(
                            color = GoldLight.copy(alpha = 0.2f),
                            contentColor = GoldLight,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "NEW",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CreamWhiteOrGoldPrimary(isPrimary: Boolean): Color {
    return if (isPrimary) GoldPrimary else MaterialTheme.colorScheme.onSurface
}
