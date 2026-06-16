package lk.nibm.kandy.hdse252ft.smartrestaurantpos.ui.admin

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.data.model.Order
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.data.model.OrderStatus
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.ui.theme.GoldPrimary
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.viewmodel.AdminOrderViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.ui.theme.CreamMuted
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.ui.theme.CreamWhite
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.ui.theme.SurfaceDark
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.ui.theme.VegGreen
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.ui.theme.NonVegRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminOrderScreen(
    navController: NavController,
    viewModel: AdminOrderViewModel = hiltViewModel()
) {
    val orders by viewModel.orders.collectAsState()
    val statusFilter by viewModel.statusFilter.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val updatingId by viewModel.updatingOrderId.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    val filterOptions = listOf("Active", "Pending", "Confirmed", "Ready", "Delivered", "Cancelled", "All")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Order Queue",
                        color = GoldPrimary,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif,
                        fontSize = 20.sp,
                        letterSpacing = 1.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = GoldPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
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
                // Filters & Search Header Block
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = viewModel::onSearchQueryChange,
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Search by table, order ID, or dish...", color = CreamMuted) },
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

                    ScrollableTabRow(
                        selectedTabIndex = filterOptions.indexOf(statusFilter).coerceAtLeast(0),
                        containerColor = Color.Transparent,
                        contentColor = GoldPrimary,
                        edgePadding = 0.dp,
                        divider = {},
                        indicator = {}
                    ) {
                        filterOptions.forEach { filter ->
                            val selected = statusFilter == filter
                            Tab(
                                selected = selected,
                                onClick = { viewModel.onStatusFilterChange(filter) },
                                selectedContentColor = GoldPrimary,
                                unselectedContentColor = CreamMuted,
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (selected) GoldPrimary.copy(alpha = 0.12f) else Color.Transparent)
                            ) {
                                Text(
                                    text = filter.uppercase(),
                                    color = if (selected) GoldPrimary else CreamMuted,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (errorMessage != null) {
                        item {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = NonVegRed.copy(alpha = 0.1f),
                                modifier = Modifier.fillMaxWidth().border(1.dp, NonVegRed.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                            ) {
                                Text(
                                    text = errorMessage!!,
                                    color = NonVegRed,
                                    modifier = Modifier.padding(12.dp),
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }

                    if (orders.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 48.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No orders found in this queue",
                                    color = CreamMuted,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    } else {
                        items(orders, key = { it.id }) { order ->
                            AdminOrderCard(
                                order = order,
                                isUpdating = updatingId == order.id,
                                onConfirm = { viewModel.confirmOrder(order.id) },
                                onReady = { viewModel.markReady(order.id) },
                                onComplete = { viewModel.completeOrder(order.id) },
                                onCancel = { viewModel.cancelOrder(order.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminOrderCard(
    order: Order,
    isUpdating: Boolean,
    onConfirm: () -> Unit,
    onReady: () -> Unit,
    onComplete: () -> Unit,
    onCancel: () -> Unit
) {
    val dateFormat = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
    val timeAgo = remember(order.timestamp) {
        val diffMs = System.currentTimeMillis() - order.timestamp
        val diffMins = diffMs / (1000 * 60)
        when {
            diffMins < 1 -> "just now"
            diffMins < 60 -> "${diffMins}m ago"
            else -> {
                val diffHours = diffMins / 60
                if (diffHours < 24) "${diffHours}h ago" else "${diffHours / 24}d ago"
            }
        }
    }

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF14110F)),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, GoldPrimary.copy(alpha = 0.15f), RoundedCornerShape(18.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Receipt Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "ORDER #${order.id.take(8).uppercase()}",
                        color = CreamWhite,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = dateFormat.format(Date(order.timestamp)),
                            color = CreamMuted,
                            fontSize = 11.sp
                        )
                        Text(
                            text = "•",
                            color = GoldPrimary.copy(alpha = 0.4f),
                            fontSize = 11.sp
                        )
                        Text(
                            text = timeAgo,
                            color = if (order.status == OrderStatus.PENDING && (System.currentTimeMillis() - order.timestamp) > 10 * 60 * 1000) NonVegRed else GoldPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                StatusBadge(status = order.status)
            }

            // Table number assignment details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = GoldPrimary.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = if (order.tableNumber.isBlank()) "UNASSIGNED TABLE" else "TABLE ${order.tableNumber}",
                        color = GoldPrimary,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }

            // Receipt items list divider
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color(0xFF2E2722))
            )

            // Invoice detailed items
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                order.items.forEach { cartItem ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${cartItem.quantity}x",
                            color = GoldPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.width(32.dp)
                        )
                        Text(
                            text = cartItem.menuItem.name,
                            color = CreamWhite,
                            fontSize = 13.sp,
                            modifier = Modifier.weight(1f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = String.format(Locale.getDefault(), "Rs. %,.2f", cartItem.menuItem.price * cartItem.quantity),
                            color = CreamWhite,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color(0xFF2E2722))
            )

            // Pricing breakdowns
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                val subtotal = order.items.sumOf { it.menuItem.price * it.quantity }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Subtotal", color = CreamMuted, fontSize = 12.sp)
                    Text(String.format(Locale.getDefault(), "Rs. %,.2f", subtotal), color = CreamMuted, fontSize = 12.sp)
                }
                if ((order.discount ?: 0.0) > 0.0) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Discount", color = VegGreen, fontSize = 12.sp)
                        Text(String.format(Locale.getDefault(), "-Rs. %,.2f", order.discount), color = VegGreen, fontSize = 12.sp)
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Total Net Amount", color = CreamWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(
                        text = String.format(Locale.getDefault(), "Rs. %,.2f", order.totalAmount - (order.discount ?: 0.0)),
                        color = GoldPrimary,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif,
                        fontSize = 16.sp
                    )
                }
            }

            if (isUpdating) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp,
                        color = GoldPrimary
                    )
                }
            } else {
                OrderActionButtons(
                    status = order.status,
                    onConfirm = onConfirm,
                    onReady = onReady,
                    onComplete = onComplete,
                    onCancel = onCancel
                )
            }
        }
    }
}

@Composable
private fun StatusBadge(status: OrderStatus) {
    val (label, color) = when (status) {
        OrderStatus.PENDING -> "PENDING" to Color(0xFFFFA726)
        OrderStatus.CONFIRMED -> "COOKING" to Color(0xFF42A5F5)
        OrderStatus.READY -> "READY" to VegGreen
        OrderStatus.DELIVERED -> "DELIVERED" to Color(0xFF9E9E9E)
        OrderStatus.CANCELLED -> "CANCELLED" to NonVegRed
    }
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.15f),
        modifier = Modifier.border(0.5.dp, color.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            color = color,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
private fun OrderActionButtons(
    status: OrderStatus,
    onConfirm: () -> Unit,
    onReady: () -> Unit,
    onComplete: () -> Unit,
    onCancel: () -> Unit
) {
    Spacer(modifier = Modifier.height(2.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        when (status) {
            OrderStatus.PENDING -> {
                Button(
                    onClick = onConfirm,
                    modifier = Modifier.weight(1.2f),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Confirm Cooking", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF1E1B18))
                }
                Button(
                    onClick = onCancel,
                    modifier = Modifier.weight(0.8f),
                    colors = ButtonDefaults.buttonColors(containerColor = NonVegRed.copy(alpha = 0.15f), contentColor = NonVegRed),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Cancel", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
            OrderStatus.CONFIRMED -> {
                Button(
                    onClick = onReady,
                    modifier = Modifier.weight(1.2f),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Mark as Ready", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF1E1B18))
                }
                Button(
                    onClick = onCancel,
                    modifier = Modifier.weight(0.8f),
                    colors = ButtonDefaults.buttonColors(containerColor = NonVegRed.copy(alpha = 0.15f), contentColor = NonVegRed),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Cancel", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
            OrderStatus.READY -> {
                Button(
                    onClick = onComplete,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = VegGreen),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Delivered & Checkout", fontWeight = FontWeight.ExtraBold, fontSize = 13.sp, color = Color(0xFF1E1B18))
                }
            }
            else -> {}
        }
    }
}
