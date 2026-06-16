package lk.nibm.kandy.hdse252ft.smartrestaurantpos.ui.order

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.animation.core.*
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.data.model.OrderStatus
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.ui.navigation.Screen
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.ui.theme.CreamMuted
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.ui.theme.CreamWhite
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.ui.theme.GoldPrimary
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.ui.theme.VegGreen
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.viewmodel.OrderConfirmationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderConfirmationScreen(
    navController: NavController,
    orderId: String,
    tableNumber: Int = 0,
    viewModel: OrderConfirmationViewModel = hiltViewModel()
) {
    val order by viewModel.order.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(orderId) {
        viewModel.loadOrder(orderId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Order Details", color = GoldPrimary, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = GoldPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1E1B16))
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF1E1B16), Color(0xFF121212))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = GoldPrimary)
            } else {
                val currentOrder = order
                var timeRemainingMillis by remember { mutableStateOf(0L) }

                LaunchedEffect(currentOrder) {
                    currentOrder?.let { o ->
                        while (true) {
                            val diff = (o.timestamp + 5 * 60 * 1000) - System.currentTimeMillis()
                            timeRemainingMillis = diff.coerceAtLeast(0L)
                            if (diff <= 0) break
                            delay(1000)
                        }
                    }
                }

                val isEditable = timeRemainingMillis > 0 && currentOrder != null &&
                        (currentOrder.status == OrderStatus.PENDING || currentOrder.status == OrderStatus.CONFIRMED)

                val scrollState = rememberScrollState()
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Status-dependent mappings
                    val (titleText, descText, iconColor) = when (currentOrder?.status) {
                        OrderStatus.PENDING -> Triple("Order Placed!", "Your order has been sent to the kitchen.", VegGreen)
                        OrderStatus.CONFIRMED -> Triple("Confirmed", "Your food is being prepared by our chefs.", GoldPrimary)
                        OrderStatus.READY -> Triple("Ready to Serve!", "Your order is prepared and ready!", GoldPrimary)
                        OrderStatus.DELIVERED -> Triple("Delivered", "Hope you enjoyed your meal!", VegGreen)
                        OrderStatus.CANCELLED -> Triple("Cancelled", "This order was cancelled.", MaterialTheme.colorScheme.error)
                        null -> Triple("Order Details", "Processing...", GoldPrimary)
                    }

                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(64.dp)
                    )

                    Text(
                        text = titleText,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldPrimary
                    )

                    Text(
                        text = descText,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (isEditable) {
                        val minutes = timeRemainingMillis / 1000 / 60
                        val seconds = (timeRemainingMillis / 1000) % 60
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = GoldPrimary.copy(alpha = 0.12f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.3f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "You can edit or cancel items for another $minutes:${String.format("%02d", seconds)} mins",
                                color = GoldPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 16.dp)
                            )
                        }
                    } else if (currentOrder != null && (currentOrder.status == OrderStatus.PENDING || currentOrder.status == OrderStatus.CONFIRMED || currentOrder.status == OrderStatus.READY)) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color.White.copy(alpha = 0.05f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CreamMuted.copy(alpha = 0.2f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Editing window closed. You can only add new items to this order.",
                                color = CreamMuted,
                                fontWeight = FontWeight.Medium,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Receipt Container
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFF1E1B18),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E2722)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Restaurant Brand
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "THE GOLDEN OAK",
                                    color = GoldPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Serif,
                                    fontSize = 18.sp,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = "Digital Restaurant POS",
                                    color = CreamMuted,
                                    fontSize = 11.sp
                                )
                            }

                            HorizontalDivider(color = Color(0xFF2E2722))

                            // Order Info Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                DetailRow("Order ID", "#${currentOrder?.id?.take(8)?.uppercase() ?: orderId.take(8).uppercase()}")
                                DetailRow("Table", "Table ${currentOrder?.tableNumber ?: tableNumber.toString()}")
                            }

                            // Timestamp and Status Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                val dateStr = currentOrder?.timestamp?.let {
                                    SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date(it))
                                } ?: "Just now"
                                DetailRow("Placed On", dateStr)
                                DetailRow("Status", currentOrder?.status?.name ?: "PENDING")
                            }

                            HorizontalDivider(color = Color(0xFF2E2722))

                            // Items List Header
                            Text(
                                text = "ORDER ITEMS",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = GoldPrimary,
                                letterSpacing = 0.5.sp
                            )

                            // Items list
                            currentOrder?.items?.forEach { item ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Thumbnail
                                    AsyncImage(
                                        model = item.menuItem.imageUrl,
                                        contentDescription = item.menuItem.name,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                    )

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = item.menuItem.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = Color.White,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = "Rs. ${item.menuItem.price}",
                                            color = CreamMuted,
                                            fontSize = 12.sp
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    if (isEditable) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            IconButton(
                                                onClick = { viewModel.updateOrderItemQuantity(currentOrder.id, item.menuItem.id, -1) },
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Remove,
                                                    contentDescription = "Decrease",
                                                    tint = GoldPrimary,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                            Text(
                                                text = "${item.quantity}",
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp
                                            )
                                            IconButton(
                                                onClick = { viewModel.updateOrderItemQuantity(currentOrder.id, item.menuItem.id, 1) },
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Add,
                                                    contentDescription = "Increase",
                                                    tint = GoldPrimary,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                            IconButton(
                                                onClick = { viewModel.removeOrderItem(currentOrder.id, item.menuItem.id) },
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Delete,
                                                    contentDescription = "Delete",
                                                    tint = MaterialTheme.colorScheme.error,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                    } else {
                                        Column(horizontalAlignment = Alignment.End) {
                                            Text(
                                                text = "${item.quantity} x Rs. ${item.menuItem.price}",
                                                color = CreamMuted,
                                                fontSize = 12.sp
                                            )
                                            Text(
                                                text = "Rs. ${item.menuItem.price * item.quantity}",
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White,
                                                fontSize = 14.sp
                                            )
                                        }
                                    }
                                }
                            }

                            HorizontalDivider(color = Color(0xFF2E2722))

                            // Payment Summary
                            Column(
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Subtotal", color = CreamMuted, fontSize = 14.sp)
                                    Text("Rs. ${currentOrder?.totalAmount ?: 0.0}", color = CreamWhite, fontSize = 14.sp)
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Tax (VAT 0%)", color = CreamMuted, fontSize = 14.sp)
                                    Text("Rs. 0.00", color = CreamWhite, fontSize = 14.sp)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Total Amount", fontWeight = FontWeight.Bold, color = GoldPrimary, fontSize = 16.sp)
                                    Text(
                                        text = "Rs. ${currentOrder?.totalAmount ?: 0.0}",
                                        fontWeight = FontWeight.ExtraBold,
                                        color = GoldPrimary,
                                        fontSize = 18.sp,
                                        fontFamily = FontFamily.Serif
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (isEditable && currentOrder != null) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = {
                                    viewModel.cancelOrder(currentOrder.id)
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("CANCEL ORDER", fontWeight = FontWeight.Bold, color = Color.White)
                            }

                            Button(
                                onClick = {
                                    val table = currentOrder.tableNumber.toIntOrNull() ?: tableNumber
                                    navController.navigate(Screen.Menu.createRoute(table)) {
                                        popUpTo(Screen.OrderConfirmation.route) { inclusive = true }
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("ADD MORE ITEMS", fontWeight = FontWeight.Bold, color = Color(0xFF1E1B18))
                            }
                        }
                    } else {
                        val isActiveOrder = currentOrder != null && 
                                (currentOrder.status == OrderStatus.PENDING || 
                                 currentOrder.status == OrderStatus.CONFIRMED || 
                                 currentOrder.status == OrderStatus.READY)
                        
                        Button(
                            onClick = {
                                val table = currentOrder?.tableNumber?.toIntOrNull() ?: tableNumber
                                navController.navigate(Screen.Menu.createRoute(table)) {
                                    popUpTo(Screen.OrderConfirmation.route) { inclusive = true }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = if (isActiveOrder) "ADD MORE ITEMS" else "BACK TO MENU",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E1B18)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(text = value, fontWeight = FontWeight.Bold, fontSize = 16.sp)
    }
}
