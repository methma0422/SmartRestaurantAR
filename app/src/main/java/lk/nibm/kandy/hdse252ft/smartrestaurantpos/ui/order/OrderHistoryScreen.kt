package lk.nibm.kandy.hdse252ft.smartrestaurantpos.ui.order

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.data.model.Order
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.data.model.OrderStatus
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.ui.theme.GoldPrimary
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.viewmodel.OrderHistoryViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderHistoryScreen(
    navController: NavController,
    viewModel: OrderHistoryViewModel = hiltViewModel()
) {
    val orders by viewModel.orders.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Order History") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { padding ->
        if (orders.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No past orders found", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(MaterialTheme.colorScheme.background),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(orders) { order ->
                    OrderCard(order = order)
                }
            }
        }
    }
}

@Composable
fun OrderCard(order: Order) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Order #${order.id.takeLast(6)}",
                    fontWeight = FontWeight.Bold,
                    color = GoldPrimary
                )
                StatusBadge(status = order.status)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text("Table: ${order.tableNumber}", style = MaterialTheme.typography.bodyMedium)
            
            if (order.latitude != null && order.longitude != null) {
                Text(
                    text = "Location: ${String.format(Locale.getDefault(), "%.4f, %.4f", order.latitude, order.longitude)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.surface)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date(order.timestamp)),
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "Rs. ${order.totalAmount}",
                    fontWeight = FontWeight.ExtraBold,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

@Composable
fun StatusBadge(status: OrderStatus) {
    val containerColor = when (status) {
        OrderStatus.PENDING -> MaterialTheme.colorScheme.secondaryContainer
        OrderStatus.CONFIRMED -> GoldPrimary.copy(alpha = 0.2f)
        OrderStatus.READY -> MaterialTheme.colorScheme.primaryContainer
        OrderStatus.DELIVERED -> MaterialTheme.colorScheme.tertiaryContainer
        OrderStatus.CANCELLED -> MaterialTheme.colorScheme.errorContainer
    }
    
    val contentColor = when (status) {
        OrderStatus.PENDING -> MaterialTheme.colorScheme.onSecondaryContainer
        OrderStatus.CONFIRMED -> GoldPrimary
        OrderStatus.READY -> MaterialTheme.colorScheme.onPrimaryContainer
        OrderStatus.DELIVERED -> MaterialTheme.colorScheme.onTertiaryContainer
        OrderStatus.CANCELLED -> MaterialTheme.colorScheme.onErrorContainer
    }

    Surface(
        color = containerColor,
        contentColor = contentColor,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = status.name,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold
        )
    }
}
