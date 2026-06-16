package lk.nibm.kandy.hdse252ft.smartrestaurantpos.ui.admin

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.ui.navigation.Screen
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.ui.theme.GoldPrimary
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.ui.theme.CreamMuted
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.ui.theme.CreamWhite
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.ui.theme.SurfaceDark
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.viewmodel.AdminDashboardViewModel
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.viewmodel.AuthViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    navController: NavController,
    viewModel: AdminDashboardViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val counts by viewModel.orderCounts.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "ADMIN PORTAL",
                            color = GoldPrimary,
                            fontWeight = FontWeight.Bold,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Serif,
                            fontSize = 20.sp,
                            letterSpacing = 1.2.sp
                        )
                        Text(
                            text = "The Golden Oak",
                            color = CreamMuted,
                            fontSize = 11.sp,
                            letterSpacing = 0.5.sp
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        authViewModel.logout()
                        navController.navigate(Screen.Menu.createRoute(0)) {
                            popUpTo(Screen.AdminDashboard.route) { inclusive = true }
                        }
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Logout",
                            tint = GoldPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { padding ->
        Column(
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
                .padding(horizontal = 20.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Welcome Section
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Welcome Back, Admin",
                    color = CreamWhite,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Serif,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Manage operations, menus, and incoming orders.",
                    color = CreamMuted,
                    fontSize = 13.sp
                )
            }

            // High-end financial and performance overview
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, GoldPrimary.copy(alpha = 0.25f), RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1B1714))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1.3f)) {
                        Text(
                            text = "TOTAL REVENUE",
                            color = GoldPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = String.format(Locale.getDefault(), "Rs. %,.2f", counts.totalRevenue),
                            color = CreamWhite,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Serif,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Box(
                        modifier = Modifier
                            .height(40.dp)
                            .width(1.dp)
                            .background(Color(0xFF2E2722))
                    )
                    Column(
                        modifier = Modifier
                            .weight(0.7f)
                            .padding(start = 16.dp)
                    ) {
                        Text(
                            text = "TOTAL ORDERS",
                            color = CreamMuted,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = counts.totalOrders.toString(),
                            color = CreamWhite,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Serif,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "Order Queue Status",
                color = CreamWhite,
                fontFamily = androidx.compose.ui.text.font.FontFamily.Serif,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SummaryCard(
                    label = "Pending",
                    count = counts.pending,
                    color = Color(0xFFFFA726),
                    modifier = Modifier.weight(1f)
                )
                SummaryCard(
                    label = "Cooking",
                    count = counts.confirmed,
                    color = Color(0xFF42A5F5),
                    modifier = Modifier.weight(1f)
                )
                SummaryCard(
                    label = "Ready",
                    count = counts.ready,
                    color = Color(0xFF66BB6A),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Management Portals",
                color = CreamWhite,
                fontFamily = androidx.compose.ui.text.font.FontFamily.Serif,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                AdminNavCard(
                    title = "Menu Management",
                    subtitle = "Add, edit, or delete items and ingredients",
                    icon = Icons.Default.MenuBook,
                    onClick = { navController.navigate(Screen.AdminMenu.route) }
                )

                AdminNavCard(
                    title = "Order Queue Management",
                    subtitle = "Track active orders, confirm, and checkout",
                    icon = Icons.Default.Receipt,
                    onClick = { navController.navigate(Screen.AdminOrders.route) }
                )

                AdminNavCard(
                    title = "Table QR Codes",
                    subtitle = "Generate and export table deep-link QR codes",
                    icon = Icons.Default.QrCode,
                    onClick = { navController.navigate(Screen.AdminQR.route) }
                )
            }
        }
    }
}

@Composable
private fun SummaryCard(
    label: String,
    count: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .border(1.dp, color.copy(alpha = 0.25f), RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1B18))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = count.toString(),
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = androidx.compose.ui.text.font.FontFamily.Serif,
                color = color
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                color = CreamMuted,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun AdminNavCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, GoldPrimary.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF171412))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(GoldPrimary.copy(alpha = 0.1f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = GoldPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = CreamWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Serif
                )
                Text(
                    text = subtitle,
                    color = CreamMuted,
                    fontSize = 12.sp
                )
            }
        }
    }
}
