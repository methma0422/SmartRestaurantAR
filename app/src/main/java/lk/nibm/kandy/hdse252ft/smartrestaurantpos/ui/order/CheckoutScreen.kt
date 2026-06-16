package lk.nibm.kandy.hdse252ft.smartrestaurantpos.ui.order

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.ui.navigation.Screen
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.ui.theme.*
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.viewmodel.CheckoutViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    navController: NavController,
    orderId: String,
    viewModel: CheckoutViewModel = hiltViewModel()
) {
    val order by viewModel.order.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isAdmin by viewModel.isAdmin.collectAsState()

    val paymentMethod by viewModel.paymentMethod.collectAsState()

    // Card details
    val cardholderName by viewModel.cardholderName.collectAsState()
    val cardNumber by viewModel.cardNumber.collectAsState()
    val cardExpiry by viewModel.cardExpiry.collectAsState()
    val cardCvv by viewModel.cardCvv.collectAsState()

    // Errors
    val cardholderNameError by viewModel.cardholderNameError.collectAsState()
    val cardNumberError by viewModel.cardNumberError.collectAsState()
    val cardExpiryError by viewModel.cardExpiryError.collectAsState()
    val cardCvvError by viewModel.cardCvvError.collectAsState()

    // Cash
    val cashReceived by viewModel.cashReceived.collectAsState()
    val changeToGive by viewModel.changeToGive.collectAsState()
    val cashReceivedError by viewModel.cashReceivedError.collectAsState()

    val isCheckingOut by viewModel.isCheckingOut.collectAsState()
    val checkoutError by viewModel.checkoutError.collectAsState()
    val billingSummary by viewModel.billingSummary.collectAsState()

    var showSuccessDialog by remember { mutableStateOf(false) }

    LaunchedEffect(orderId) {
        viewModel.loadOrder(orderId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Checkout Order", color = GoldPrimary, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = GoldPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF0C0B0A), Color(0xFF171311), SurfaceDark)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = GoldPrimary)
            } else if (order == null) {
                Text("Order not found.", color = CreamMuted)
            } else {
                val currentOrder = order!!
                val summary = billingSummary
                val scrollState = rememberScrollState()

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Order Summary Header
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF14110F)),
                        border = BorderStroke(1.dp, Color(0xFF2E2722)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "ORDER ID: #${currentOrder.id.take(8).uppercase()}",
                                    color = CreamWhite,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "Table ${currentOrder.tableNumber}",
                                    color = GoldPrimary,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 13.sp
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "${currentOrder.items.sumOf { it.quantity }} Items",
                                    color = CreamMuted,
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = "Rs. ${currentOrder.totalAmount}",
                                    color = CreamWhite,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }

                    // Payment Method Tab Selectors
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(Color(0xFF1B1715))
                            .border(1.dp, Color(0xFF2E2722), RoundedCornerShape(24.dp)),
                        horizontalArrangement = Arrangement.spacedBy(0.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(24.dp))
                                .background(if (paymentMethod == "Card") GoldPrimary else Color.Transparent)
                                .clickable { viewModel.setPaymentMethod("Card") },
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(
                                    imageVector = Icons.Default.CreditCard,
                                    contentDescription = null,
                                    tint = if (paymentMethod == "Card") Color(0xFF1E1B18) else CreamMuted
                                )
                                Text(
                                    text = "CREDIT CARD",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = if (paymentMethod == "Card") Color(0xFF1E1B18) else CreamMuted
                                )
                            }
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(24.dp))
                                .background(if (paymentMethod == "Cash") GoldPrimary else Color.Transparent)
                                .clickable { viewModel.setPaymentMethod("Cash") },
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(
                                    imageVector = Icons.Default.Payments,
                                    contentDescription = null,
                                    tint = if (paymentMethod == "Cash") Color(0xFF1E1B18) else CreamMuted
                                )
                                Text(
                                    text = "CASH / BAR",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = if (paymentMethod == "Cash") Color(0xFF1E1B18) else CreamMuted
                                )
                            }
                        }
                    }

                    // Card Form Details
                    if (paymentMethod == "Card") {
                        // Virtual Card visualizer
                        VirtualCreditCard(
                            cardNumber = cardNumber,
                            cardholderName = cardholderName,
                            cardExpiry = cardExpiry,
                            cardCvv = cardCvv
                        )

                        // Input Form
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = cardholderName,
                                onValueChange = viewModel::onCardholderNameChange,
                                modifier = Modifier.fillMaxWidth(),
                                isError = cardholderNameError != null,
                                label = { Text("Cardholder Name", color = CreamMuted) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GoldPrimary,
                                    unfocusedBorderColor = Color(0xFF2E2722),
                                    focusedTextColor = CreamWhite,
                                    unfocusedTextColor = CreamWhite,
                                    errorBorderColor = NonVegRed
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                            if (cardholderNameError != null) {
                                Text(cardholderNameError!!, color = NonVegRed, fontSize = 11.sp, modifier = Modifier.padding(start = 6.dp))
                            }

                            OutlinedTextField(
                                value = cardNumber,
                                onValueChange = viewModel::onCardNumberChange,
                                modifier = Modifier.fillMaxWidth(),
                                isError = cardNumberError != null,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                label = { Text("Card Number", color = CreamMuted) },
                                placeholder = { Text("0000 0000 0000 0000", color = CreamMuted.copy(alpha = 0.5f)) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GoldPrimary,
                                    unfocusedBorderColor = Color(0xFF2E2722),
                                    focusedTextColor = CreamWhite,
                                    unfocusedTextColor = CreamWhite,
                                    errorBorderColor = NonVegRed
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                            if (cardNumberError != null) {
                                Text(cardNumberError!!, color = NonVegRed, fontSize = 11.sp, modifier = Modifier.padding(start = 6.dp))
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Column(modifier = Modifier.weight(1.2f)) {
                                    OutlinedTextField(
                                        value = cardExpiry,
                                        onValueChange = viewModel::onCardExpiryChange,
                                        modifier = Modifier.fillMaxWidth(),
                                        isError = cardExpiryError != null,
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        label = { Text("Expiry Date", color = CreamMuted) },
                                        placeholder = { Text("MM/YY", color = CreamMuted.copy(alpha = 0.5f)) },
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = GoldPrimary,
                                            unfocusedBorderColor = Color(0xFF2E2722),
                                            focusedTextColor = CreamWhite,
                                            unfocusedTextColor = CreamWhite,
                                            errorBorderColor = NonVegRed
                                        ),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    if (cardExpiryError != null) {
                                        Text(cardExpiryError!!, color = NonVegRed, fontSize = 11.sp, modifier = Modifier.padding(start = 6.dp))
                                    }
                                }

                                Column(modifier = Modifier.weight(0.8f)) {
                                    OutlinedTextField(
                                        value = cardCvv,
                                        onValueChange = viewModel::onCardCvvChange,
                                        modifier = Modifier.fillMaxWidth(),
                                        isError = cardCvvError != null,
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        label = { Text("CVV", color = CreamMuted) },
                                        placeholder = { Text("•••", color = CreamMuted.copy(alpha = 0.5f)) },
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = GoldPrimary,
                                            unfocusedBorderColor = Color(0xFF2E2722),
                                            focusedTextColor = CreamWhite,
                                            unfocusedTextColor = CreamWhite,
                                            errorBorderColor = NonVegRed
                                        ),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    if (cardCvvError != null) {
                                        Text(cardCvvError!!, color = NonVegRed, fontSize = 11.sp, modifier = Modifier.padding(start = 6.dp))
                                    }
                                }
                            }
                        }
                    } else {
                        // Cash payment details
                        if (isAdmin) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "CASH DRAWER INPUTS",
                                    color = GoldPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    letterSpacing = 1.sp
                                )

                                OutlinedTextField(
                                    value = cashReceived,
                                    onValueChange = viewModel::onCashReceivedChange,
                                    modifier = Modifier.fillMaxWidth(),
                                    isError = cashReceivedError != null,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    label = { Text("Cash Received (Rs.)", color = CreamMuted) },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = GoldPrimary,
                                        unfocusedBorderColor = Color(0xFF2E2722),
                                        focusedTextColor = CreamWhite,
                                        unfocusedTextColor = CreamWhite,
                                        errorBorderColor = NonVegRed
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                if (cashReceivedError != null) {
                                    Text(cashReceivedError!!, color = NonVegRed, fontSize = 11.sp, modifier = Modifier.padding(start = 6.dp))
                                }

                                if (summary != null && cashReceived.isNotBlank() && cashReceivedError == null) {
                                    val cashAmount = cashReceived.toDoubleOrNull() ?: 0.0
                                    if (cashAmount >= summary.finalTotal) {
                                        Surface(
                                            shape = RoundedCornerShape(12.dp),
                                            color = VegGreen.copy(alpha = 0.12f),
                                            border = BorderStroke(1.dp, VegGreen.copy(alpha = 0.4f)),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(16.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = "CHANGE TO RETURN",
                                                    fontWeight = FontWeight.Bold,
                                                    color = VegGreen,
                                                    fontSize = 13.sp
                                                )
                                                Text(
                                                    text = String.format(Locale.getDefault(), "Rs. %,.2f", changeToGive),
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = VegGreen,
                                                    fontSize = 16.sp
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            // Customer Cash flow - friendly info box
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = Color(0xFF1A1715),
                                border = BorderStroke(1.dp, Color(0xFF2E2722)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(20.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = null,
                                        tint = GoldPrimary,
                                        modifier = Modifier.size(36.dp)
                                    )
                                    Text(
                                        text = "Cash Payment at Counter",
                                        fontWeight = FontWeight.Bold,
                                        color = CreamWhite,
                                        fontSize = 16.sp
                                    )
                                    Text(
                                        text = "Your request will notify the service counter for Table ${currentOrder.tableNumber}. You can proceed to the billing counter to make a cash payment, or wait for our waiter to assist you with the cash drawer checkout.",
                                        color = CreamMuted,
                                        fontSize = 12.sp,
                                        textAlign = TextAlign.Center,
                                        lineHeight = 18.sp
                                    )
                                }
                            }
                        }
                    }

                    // Cost Breakdowns
                    if (summary != null) {
                        HorizontalDivider(color = Color(0xFF2E2722))
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Subtotal", color = CreamMuted, fontSize = 13.sp)
                                Text(String.format(Locale.getDefault(), "Rs. %,.2f", summary.subtotal), color = CreamWhite, fontSize = 13.sp)
                            }
                            if (summary.discount > 0.0) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Discount Applied", color = VegGreen, fontSize = 13.sp)
                                    Text(String.format(Locale.getDefault(), "- Rs. %,.2f", summary.discount), color = VegGreen, fontSize = 13.sp)
                                }
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("VAT Tax (10%)", color = CreamMuted, fontSize = 13.sp)
                                Text(String.format(Locale.getDefault(), "Rs. %,.2f", summary.tax), color = CreamWhite, fontSize = 13.sp)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Service Charge (5%)", color = CreamMuted, fontSize = 13.sp)
                                Text(String.format(Locale.getDefault(), "Rs. %,.2f", summary.serviceCharge), color = CreamWhite, fontSize = 13.sp)
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Grand Total", fontWeight = FontWeight.Bold, color = GoldPrimary, fontSize = 16.sp)
                                Text(
                                    text = String.format(Locale.getDefault(), "Rs. %,.2f", summary.finalTotal),
                                    fontWeight = FontWeight.ExtraBold,
                                    color = GoldPrimary,
                                    fontSize = 20.sp,
                                    fontFamily = FontFamily.Serif
                                )
                            }
                        }
                    }

                    if (checkoutError != null) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = NonVegRed.copy(alpha = 0.1f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = checkoutError!!,
                                color = NonVegRed,
                                modifier = Modifier.padding(10.dp),
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Final Action Button
                    Button(
                        onClick = {
                            viewModel.checkout {
                                showSuccessDialog = true
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isCheckingOut,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
                    ) {
                        if (isCheckingOut) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = Color(0xFF1E1B18)
                            )
                        } else {
                            val buttonText = if (paymentMethod == "Card") {
                                "CONFIRM & PAY BILL"
                            } else {
                                if (isAdmin) "COMPLETE CASH CHECKOUT" else "REQUEST WAITER & LOG CASH"
                            }
                            Text(
                                text = buttonText,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E1B18),
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }

    if (showSuccessDialog) {
        Dialog(onDismissRequest = { /* Prevent dismiss */ }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFF1E1B18),
                border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.5f)),
                modifier = Modifier.padding(24.dp)
            ) {
                val checkScale by animateFloatAsState(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = 500),
                    label = "Checkmark animation"
                )

                LaunchedEffect(Unit) {
                    delay(2000)
                    showSuccessDialog = false
                    // Navigate back to Menu / Home screen
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }

                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Success",
                        tint = VegGreen,
                        modifier = Modifier
                            .size(72.dp)
                            .scale(checkScale)
                    )
                    Text(
                        text = "Transaction Complete",
                        color = GoldPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        fontFamily = FontFamily.Serif
                    )
                    Text(
                        text = "Thank you! The payment has been processed successfully. The table lock has been cleared.",
                        color = CreamMuted,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun VirtualCreditCard(
    cardNumber: String,
    cardholderName: String,
    cardExpiry: String,
    cardCvv: String
) {
    // Brand detection
    val detectedBrand = remember(cardNumber) {
        val clean = cardNumber.replace(" ", "")
        when {
            clean.startsWith("4") -> "VISA"
            clean.startsWith("5") -> "MASTERCARD"
            else -> "CREDIT CARD"
        }
    }

    Surface(
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.4f)),
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF2E2A24),
                            Color(0xFF14120E),
                            Color(0xFF070605)
                        )
                    )
                )
                .padding(20.dp)
        ) {
            // Gold Chip
            Box(
                modifier = Modifier
                    .size(42.dp, 32.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Brush.verticalGradient(colors = listOf(GoldLight, GoldPrimary)))
                    .border(1.dp, GoldLight, RoundedCornerShape(6.dp))
                    .align(Alignment.TopStart)
            )

            // Brand
            Text(
                text = detectedBrand,
                color = GoldPrimary,
                fontWeight = FontWeight.Black,
                fontSize = 14.sp,
                fontFamily = FontFamily.Serif,
                modifier = Modifier.align(Alignment.TopEnd)
            )

            // Card Number
            val displayCardNo = if (cardNumber.isBlank()) {
                "•••• •••• •••• ••••"
            } else {
                cardNumber + "•••• •••• •••• ••••".substring(cardNumber.length.coerceAtMost(19))
            }
            Text(
                text = displayCardNo,
                color = CreamWhite,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 2.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterStart)
                    .padding(top = 24.dp)
            )

            // Name and Expiry
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomStart),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column(modifier = Modifier.weight(1.5f)) {
                    Text(
                        text = "CARDHOLDER",
                        color = CreamMuted,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (cardholderName.isBlank()) "CARDHOLDER NAME" else cardholderName.uppercase(Locale.getDefault()),
                        color = CreamWhite,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Column(modifier = Modifier.weight(0.7f), horizontalAlignment = Alignment.End) {
                    Text(
                        text = "EXPIRES",
                        color = CreamMuted,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (cardExpiry.isBlank()) "MM/YY" else cardExpiry,
                        color = CreamWhite,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (cardCvv.isNotBlank()) {
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "CVV",
                            color = CreamMuted,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = cardCvv,
                            color = CreamWhite,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
