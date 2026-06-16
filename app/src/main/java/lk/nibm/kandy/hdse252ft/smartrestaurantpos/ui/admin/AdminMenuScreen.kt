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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.data.model.MenuItem
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.ui.navigation.Screen
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.ui.theme.GoldPrimary
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.ui.theme.VegGreen
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.viewmodel.AdminMenuViewModel

import androidx.compose.animation.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.ui.theme.CreamMuted
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.ui.theme.CreamWhite
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.ui.theme.SurfaceDark
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.ui.theme.NonVegRed
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminMenuScreen(
    navController: NavController,
    viewModel: AdminMenuViewModel = hiltViewModel()
) {
    val menuItems by viewModel.menuItems.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val deletingId by viewModel.isDeleting.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Menu Management",
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
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.AdminMenuForm.createRoute()) },
                containerColor = GoldPrimary,
                contentColor = Color(0xFF1E1B18),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(bottom = 8.dp, end = 8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Item", modifier = Modifier.size(28.dp))
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
                // Search and Filters Header Block
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
                        placeholder = { Text("Search menu items...", color = CreamMuted) },
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
                        selectedTabIndex = categories.indexOf(selectedCategory).coerceAtLeast(0),
                        containerColor = Color.Transparent,
                        contentColor = GoldPrimary,
                        edgePadding = 0.dp,
                        divider = {},
                        indicator = {}
                    ) {
                        categories.forEach { category ->
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
                                Text(
                                    text = category.uppercase(),
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
                    verticalArrangement = Arrangement.spacedBy(12.dp)
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

                    if (menuItems.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 48.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No menu items match your search",
                                    color = CreamMuted,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    } else {
                        items(menuItems, key = { it.id }) { item ->
                            AdminMenuItemCard(
                                item = item,
                                isDeleting = deletingId == item.id,
                                onEdit = { navController.navigate(Screen.AdminMenuForm.createRoute(item.id)) },
                                onDelete = { viewModel.deleteMenuItem(item.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminMenuItemCard(
    item: MenuItem,
    isDeleting: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF14110F)),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, GoldPrimary.copy(alpha = 0.15f), RoundedCornerShape(18.dp))
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
                modifier = Modifier
                    .size(70.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(0.5.dp, GoldPrimary.copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = item.name,
                    color = CreamWhite,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = item.category.uppercase(),
                        color = CreamMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (item.isVegetarian) {
                        Surface(
                            shape = RoundedCornerShape(999.dp),
                            color = VegGreen.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "VEG",
                                color = VegGreen,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                Text(
                    text = String.format(Locale.getDefault(), "Rs. %,.2f", item.price),
                    color = GoldPrimary,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif,
                    fontSize = 15.sp
                )
            }
            if (isDeleting) {
                Box(modifier = Modifier.size(48.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp, color = GoldPrimary)
                }
            } else {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = GoldPrimary)
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = NonVegRed)
                    }
                }
            }
        }
    }
}
