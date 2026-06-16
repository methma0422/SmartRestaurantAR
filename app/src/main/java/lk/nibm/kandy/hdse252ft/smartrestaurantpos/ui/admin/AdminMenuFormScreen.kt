package lk.nibm.kandy.hdse252ft.smartrestaurantpos.ui.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.ui.theme.CreamMuted
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.ui.theme.CreamWhite
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.ui.theme.GoldPrimary
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.ui.theme.NonVegRed
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.ui.theme.SurfaceDark
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.viewmodel.AdminMenuFormViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminMenuFormScreen(
    navController: NavController,
    itemId: String?,
    viewModel: AdminMenuFormViewModel = hiltViewModel()
) {
    val menuItem by viewModel.menuItem.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isSaving by viewModel.isSaving.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val isEditing = !itemId.isNullOrBlank()

    var showCustomCategoryDialog by remember { mutableStateOf(false) }
    var customCategoryText by remember { mutableStateOf("") }
    var dropdownExpanded by remember { mutableStateOf(false) }

    val defaultCategories = listOf("Starters", "Main Course", "Burgers", "Salads", "Desserts")
    val allCategories = remember(menuItem.category) {
        if (menuItem.category.isNotBlank() && !defaultCategories.contains(menuItem.category)) {
            defaultCategories + menuItem.category
        } else {
            defaultCategories
        }
    }

    LaunchedEffect(itemId) {
        viewModel.loadItem(itemId)
    }

    // Custom Category Creation Dialog
    if (showCustomCategoryDialog) {
        AlertDialog(
            onDismissRequest = { showCustomCategoryDialog = false },
            title = { Text("New Category", color = GoldPrimary, fontFamily = FontFamily.Serif) },
            text = {
                OutlinedTextField(
                    value = customCategoryText,
                    onValueChange = { customCategoryText = it },
                    label = { Text("Category Name") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (customCategoryText.isNotBlank()) {
                            viewModel.updateCategory(customCategoryText.trim())
                            showCustomCategoryDialog = false
                            customCategoryText = ""
                        }
                    }
                ) {
                    Text("ADD", color = GoldPrimary, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCustomCategoryDialog = false }) {
                    Text("CANCEL", color = CreamMuted)
                }
            },
            containerColor = SurfaceDark
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isEditing) "Edit Menu Item" else "Add Menu Item",
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
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFF0C0B0A), Color(0xFF171311), SurfaceDark)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = GoldPrimary)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFF0C0B0A), Color(0xFF171311), SurfaceDark)
                        )
                    )
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Card 1: General Information
                FormSectionCard(title = "General Information") {
                    FormField("Name", menuItem.name, viewModel::updateName)
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Category Dropdown Input Box
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = menuItem.category,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Category") },
                            modifier = Modifier.fillMaxWidth(),
                            trailingIcon = {
                                IconButton(onClick = { dropdownExpanded = !dropdownExpanded }) {
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = GoldPrimary)
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFF1B1715),
                                unfocusedContainerColor = Color(0xFF1B1715),
                                focusedBorderColor = GoldPrimary,
                                unfocusedBorderColor = Color(0xFF2E2722),
                                focusedTextColor = CreamWhite,
                                unfocusedTextColor = CreamWhite,
                                focusedLabelColor = GoldPrimary,
                                unfocusedLabelColor = CreamMuted
                            )
                        )
                        
                        DropdownMenu(
                            expanded = dropdownExpanded,
                            onDismissRequest = { dropdownExpanded = false },
                            modifier = Modifier.fillMaxWidth(0.9f).background(Color(0xFF1C1816))
                        ) {
                            allCategories.forEach { category ->
                                DropdownMenuItem(
                                    text = { Text(category, color = CreamWhite) },
                                    onClick = {
                                        viewModel.updateCategory(category)
                                        dropdownExpanded = false
                                    }
                                )
                            }
                            DropdownMenuItem(
                                text = { Text("+ Custom...", color = GoldPrimary, fontWeight = FontWeight.Bold) },
                                onClick = {
                                    dropdownExpanded = false
                                    showCustomCategoryDialog = true
                                }
                            )
                        }
                    }
                }

                // Card 2: Pricing & Product Details
                FormSectionCard(title = "Pricing & Product Details") {
                    FormField("Price (Rs.)", if (menuItem.price == 0.0) "" else menuItem.price.toString(), onValueChange = viewModel::updatePrice)
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    FormField(
                        "Discounted Price (Rs. optional)",
                        menuItem.discountedPrice?.toString() ?: "",
                        onValueChange = viewModel::updateDiscountedPrice
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    FormField("Calories", if (menuItem.calories == 0) "" else menuItem.calories.toString(), onValueChange = viewModel::updateCalories)
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    FormField(
                        "Description",
                        menuItem.description,
                        onValueChange = viewModel::updateDescription,
                        singleLine = false
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Vegetarian Option", color = CreamWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Switch(checked = menuItem.isVegetarian, onCheckedChange = viewModel::updateIsVegetarian)
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Mark as New/Featured", color = CreamWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Switch(checked = menuItem.isNew, onCheckedChange = viewModel::updateIsNew)
                    }
                }

                // Card 3: Visual & Media
                FormSectionCard(title = "Visual & Media") {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .border(0.5.dp, GoldPrimary.copy(alpha = 0.15f), RoundedCornerShape(12.dp)),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F0C0B))
                    ) {
                        if (menuItem.imageUrl.isNotBlank()) {
                            AsyncImage(
                                model = menuItem.imageUrl,
                                contentDescription = "Preview",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Photo,
                                    contentDescription = null,
                                    tint = CreamMuted,
                                    modifier = Modifier.size(36.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Image Preview",
                                    color = CreamMuted,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    FormField("Image URL", menuItem.imageUrl, viewModel::updateImageUrl)
                }

                // Card 4: Ingredients & Benefits
                FormSectionCard(title = "Ingredients & Health Benefits") {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        menuItem.ingredients.forEachIndexed { index, ingredient ->
                            IngredientFields(
                                name = ingredient.name,
                                benefits = ingredient.benefits.joinToString(", "),
                                onNameChange = { viewModel.updateIngredient(index, it, ingredient.benefits.joinToString(", ")) },
                                onBenefitsChange = { viewModel.updateIngredient(index, ingredient.name, it) },
                                onRemove = { viewModel.removeIngredient(index) }
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(6.dp))
                    
                    TextButton(
                        onClick = viewModel::addIngredient,
                        colors = ButtonDefaults.textButtonColors(contentColor = GoldPrimary)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Text(" Add Ingredient", fontWeight = FontWeight.Bold)
                    }
                }

                if (errorMessage != null) {
                    Text(errorMessage!!, color = NonVegRed, fontSize = 13.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Validation rule
                val isFormValid = menuItem.name.isNotBlank() && menuItem.category.isNotBlank() && menuItem.price > 0.0

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.weight(0.8f).height(48.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = CreamWhite),
                        border = BorderStroke(1.dp, Color(0xFF2E2722)),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Text("CANCEL", fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp, fontSize = 13.sp)
                    }

                    Button(
                        onClick = {
                            viewModel.save { navController.popBackStack() }
                        },
                        modifier = Modifier.weight(1.2f).height(48.dp),
                        enabled = isFormValid && !isSaving,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GoldPrimary,
                            disabledContainerColor = GoldPrimary.copy(alpha = 0.3f)
                        ),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        if (isSaving) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp, color = Color(0xFF1E1B18))
                        } else {
                            Text("SAVE DISH", fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp, color = if (isFormValid) Color(0xFF1E1B18) else CreamMuted, fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FormSectionCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, GoldPrimary.copy(alpha = 0.15f), RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF14110F))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title.uppercase(),
                color = GoldPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(14.dp))
            content()
        }
    }
}

@Composable
private fun FormField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    singleLine: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = singleLine,
        minLines = if (singleLine) 1 else 3,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color(0xFF1B1715),
            unfocusedContainerColor = Color(0xFF1B1715),
            focusedBorderColor = GoldPrimary,
            unfocusedBorderColor = Color(0xFF2E2722),
            focusedTextColor = CreamWhite,
            unfocusedTextColor = CreamWhite,
            focusedLabelColor = GoldPrimary,
            unfocusedLabelColor = CreamMuted
        )
    )
}

@Composable
private fun IngredientFields(
    name: String,
    benefits: String,
    onNameChange: (String) -> Unit,
    onBenefitsChange: (String) -> Unit,
    onRemove: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFF2E2722), RoundedCornerShape(16.dp))
            .background(Color(0xFF151210))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                label = { Text("Ingredient Name") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF1B1715),
                    unfocusedContainerColor = Color(0xFF1B1715),
                    focusedBorderColor = GoldPrimary,
                    unfocusedBorderColor = Color(0xFF2E2722),
                    focusedTextColor = CreamWhite,
                    unfocusedTextColor = CreamWhite,
                    focusedLabelColor = GoldPrimary,
                    unfocusedLabelColor = CreamMuted
                )
            )
            IconButton(onClick = onRemove) {
                Icon(Icons.Default.Delete, contentDescription = "Remove", tint = NonVegRed)
            }
        }
        OutlinedTextField(
            value = benefits,
            onValueChange = onBenefitsChange,
            label = { Text("Health Benefits (comma-separated)") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF1B1715),
                unfocusedContainerColor = Color(0xFF1B1715),
                focusedBorderColor = GoldPrimary,
                unfocusedBorderColor = Color(0xFF2E2722),
                focusedTextColor = CreamWhite,
                unfocusedTextColor = CreamWhite,
                focusedLabelColor = GoldPrimary,
                unfocusedLabelColor = CreamMuted
            )
        )
    }
}
