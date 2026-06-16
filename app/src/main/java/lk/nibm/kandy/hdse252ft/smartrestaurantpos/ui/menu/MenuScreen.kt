package lk.nibm.kandy.hdse252ft.smartrestaurantpos.ui.menu

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.data.model.MenuItem
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.data.model.Order
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.data.model.OrderStatus
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.ui.navigation.Screen
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.ui.navigation.RestaurantBottomBar
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.ui.navigation.navigateToTopLevel
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.ui.theme.CreamMuted
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.ui.theme.CreamWhite
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.ui.theme.GoldLight
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.ui.theme.GoldPrimary
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.ui.theme.NonVegRed
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.ui.theme.SurfaceDark
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.ui.theme.VegGreen
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.viewmodel.CartViewModel
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.viewmodel.DietaryFilter
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.viewmodel.MenuViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(
    navController: NavController,
    tableNumber: Int = 0,
    cartViewModel: CartViewModel = hiltViewModel(),
    viewModel: MenuViewModel = hiltViewModel()
) {
    val menuItems by viewModel.menuItems.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val dietaryFilter by viewModel.dietaryFilter.collectAsState()
    val isSyncing by viewModel.isSyncing.collectAsState()
    val syncError by viewModel.syncError.collectAsState()
    val activeOrders by viewModel.activeOrders.collectAsState()
    val currentTableNumber by viewModel.tableNumber.collectAsState()

    LaunchedEffect(tableNumber) {
        viewModel.setTableNumber(tableNumber)
    }

    val featuredItems = menuItems.take(3)
    val categoryPreview = categories

    Scaffold(
        bottomBar = {
            RestaurantBottomBar(
                currentRoute = Screen.Menu.createRoute(currentTableNumber),
                tableNumber = currentTableNumber
            ) { route ->
                navController.navigateToTopLevel(route)
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF0C0B0A),
                            Color(0xFF171311),
                            SurfaceDark
                        )
                    )
                )
        ) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Good Evening",
                            color = CreamMuted,
                            fontSize = 12.sp,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "THE GOLDEN OAK",
                            color = GoldPrimary,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif,
                            fontSize = 20.sp,
                            letterSpacing = 1.2.sp
                        )
                    }
                },
                actions = {
                    if (currentTableNumber > 0) {
                        Surface(
                            shape = RoundedCornerShape(999.dp),
                            color = GoldPrimary.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "Table $currentTableNumber",
                                color = GoldPrimary,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    IconButton(onClick = {
                        navController.navigate(Screen.Login.route)
                    }) {
                        Icon(Icons.Default.Person, contentDescription = "Admin", tint = GoldPrimary)
                    }
                    IconButton(onClick = {
                        navController.navigate(Screen.Cart.createRoute(currentTableNumber))
                    }) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = "Cart", tint = GoldPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Categories",
                                color = CreamWhite,
                                fontFamily = FontFamily.Serif,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Swipe list >>",
                                color = GoldPrimary,
                                fontSize = 12.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        ScrollableTabRow(
                            selectedTabIndex = categoryPreview.indexOf(selectedCategory).coerceAtLeast(0),
                            containerColor = Color.Transparent,
                            contentColor = GoldPrimary,
                            edgePadding = 0.dp,
                            divider = {},
                            indicator = {}
                        ) {
                            categoryPreview.forEach { category ->
                                val selected = selectedCategory == category
                                Tab(
                                    selected = selected,
                                    onClick = { viewModel.onCategorySelect(category) },
                                    selectedContentColor = GoldPrimary,
                                    unselectedContentColor = CreamMuted,
                                    modifier = Modifier
                                        .padding(end = 8.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (selected) GoldPrimary.copy(alpha = 0.12f) else Color.Transparent)
                                ) {
                                    CategoryChipText(
                                        text = category,
                                        selected = selected,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = viewModel::onSearchQueryChange,
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Search dishes...", color = CreamMuted) },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = CreamMuted) },
                            singleLine = true,
                            shape = RoundedCornerShape(24.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFF1B1715),
                                unfocusedContainerColor = Color(0xFF1B1715),
                                focusedBorderColor = GoldPrimary,
                                unfocusedBorderColor = Color(0xFF2E2722),
                                focusedTextColor = CreamWhite,
                                unfocusedTextColor = CreamWhite
                            )
                        )

                        if (isSyncing) {
                            Spacer(modifier = Modifier.height(10.dp))
                            LinearProgressIndicator(
                                modifier = Modifier.fillMaxWidth(),
                                color = GoldPrimary,
                                trackColor = Color(0x332A2520)
                            )
                        }

                        if (syncError != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = syncError!!,
                                color = Color(0xFFFFA69E),
                                fontSize = 12.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            FilterChip(
                                selected = dietaryFilter == DietaryFilter.All,
                                onClick = { viewModel.onDietaryFilterSelect(DietaryFilter.All) },
                                label = { Text("All") }
                            )
                            FilterChip(
                                selected = dietaryFilter == DietaryFilter.Vegetarian,
                                onClick = { viewModel.onDietaryFilterSelect(DietaryFilter.Vegetarian) },
                                label = { Text("Veg") }
                            )
                            FilterChip(
                                selected = dietaryFilter == DietaryFilter.NonVegetarian,
                                onClick = { viewModel.onDietaryFilterSelect(DietaryFilter.NonVegetarian) },
                                label = { Text("Non") }
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Surface(
                                shape = RoundedCornerShape(999.dp),
                                color = Color(0xFF201B16)
                            ) {
                                Text(
                                    text = "01",
                                    color = CreamMuted,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = selectedCategory,
                            color = CreamWhite,
                            fontFamily = FontFamily.Serif,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (featuredItems.isNotEmpty()) {
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            featuredItems.forEachIndexed { index, menuItem ->
                                FeaturedMenuCard(
                                    item = menuItem,
                                    onClick = { navController.navigate(Screen.ItemDetail.createRoute(menuItem.id)) }
                                )
                                if (index < featuredItems.lastIndex) {
                                    Spacer(modifier = Modifier.height(2.dp))
                                }
                            }
                        }
                    }
                }

                if (menuItems.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No dishes match your filters",
                                color = CreamMuted,
                                fontSize = 14.sp
                            )
                        }
                    }
                } else {
                    items(menuItems) { item ->
                        CompactMenuItemCard(
                            item = item,
                            onClick = { navController.navigate(Screen.ItemDetail.createRoute(item.id)) }
                        )
                    }
                }


            }
        }

        if (activeOrders.isNotEmpty()) {
            val activeOrder = activeOrders.first()
            ActiveOrderTrackerCard(
                order = activeOrder,
                onClick = {
                    navController.navigate(Screen.OrderConfirmation.createRoute(activeOrder.id))
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            )
        }
    }
}
}

@Composable
private fun CategoryChipText(
    text: String,
    selected: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(if (selected) 48.dp else 40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(if (selected) GoldPrimary else Color(0xFF211C19)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text.firstOrNull()?.uppercaseChar()?.toString().orEmpty().ifEmpty { "?" },
                color = if (selected) Color(0xFF211C19) else GoldPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = if (selected) 16.sp else 13.sp
            )
        }
        Text(
            text = text.ifBlank { "UNCATEGORIZED" }.uppercase(),
            color = if (selected) GoldPrimary else CreamMuted,
            fontSize = 9.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun FeaturedMenuCard(
    item: MenuItem,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF171311)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = item.imageUrl,
                contentDescription = item.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(74.dp)
                    .clip(CircleShape)
                    .border(1.dp, GoldPrimary.copy(alpha = 0.3f), CircleShape)
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = item.name,
                    color = CreamWhite,
                    fontSize = 18.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${item.calories} Calories",
                    color = CreamMuted,
                    fontSize = 12.sp
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (item.isVegetarian) {
                        FoodTag(text = "Veg", tint = VegGreen)
                    } else {
                        FoodTag(text = "Non", tint = NonVegRed)
                    }
                    if (item.isNew) {
                        FoodTag(text = "New", tint = GoldPrimary)
                    }
                }
            }

            Text(
                text = "Rs. ${item.price}",
                color = GoldPrimary,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif,
                fontSize = 18.sp
            )
        }
    }
}

@Composable
private fun CompactMenuItemCard(
    item: MenuItem,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF12100F)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = item.imageUrl,
                contentDescription = item.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(66.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = item.name,
                    color = CreamWhite,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${item.calories} Calories",
                    color = CreamMuted,
                    fontSize = 12.sp
                )
            }

            Text(
                text = "Rs. ${item.price}",
                color = GoldPrimary,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
private fun FoodTag(text: String, tint: Color) {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = tint.copy(alpha = 0.14f),
        contentColor = tint
    ) {
        Text(
            text = text.uppercase(),
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun ActiveOrderTrackerCard(
    order: Order,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progress = when (order.status) {
        OrderStatus.PENDING -> 0.3f
        OrderStatus.CONFIRMED -> 0.65f
        OrderStatus.READY -> 1.0f
        else -> 0f
    }

    val statusTitle = when (order.status) {
        OrderStatus.PENDING -> "Awaiting Confirmation"
        OrderStatus.CONFIRMED -> "Cooking your Order"
        OrderStatus.READY -> "Order is Ready!"
        else -> "Processing"
    }

    val statusSubtitle = when (order.status) {
        OrderStatus.PENDING -> "Sent to kitchen"
        OrderStatus.CONFIRMED -> "Chef is preparing the food"
        OrderStatus.READY -> "Pick up at the counter"
        else -> "Please wait"
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1B18)),
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, GoldPrimary.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(44.dp),
                contentAlignment = Alignment.Center
            ) {
                val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                val pulseScale by infiniteTransition.animateFloat(
                    initialValue = 1.0f,
                    targetValue = 1.3f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1200, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "pulseScale"
                )
                val pulseAlpha by infiniteTransition.animateFloat(
                    initialValue = 0.4f,
                    targetValue = 0.0f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1200, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "pulseAlpha"
                )

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(GoldPrimary.copy(alpha = pulseAlpha))
                        .align(Alignment.Center)
                )

                CircularProgressIndicator(
                    progress = progress,
                    modifier = Modifier.size(36.dp),
                    color = GoldPrimary,
                    trackColor = Color(0xFF2E2722),
                    strokeWidth = 3.dp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = statusTitle,
                    color = CreamWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Text(
                    text = statusSubtitle,
                    color = CreamMuted,
                    fontSize = 12.sp
                )
                Text(
                    text = "Order #${order.id.takeLast(6).uppercase()}",
                    color = GoldPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 10.sp,
                    letterSpacing = 0.5.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                modifier = Modifier.height(32.dp)
            ) {
                Text(
                    text = "TRACK",
                    color = Color(0xFF1E1B18),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 11.sp
                )
            }
        }
    }
}

