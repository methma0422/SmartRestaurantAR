package lk.nibm.kandy.hdse252ft.smartrestaurantpos.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.data.model.Order
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.data.model.UserRole
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.data.repository.AuthRepository
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.data.repository.OrderRepository
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.data.repository.UserRepository
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val sharedPrefs = context.getSharedPreferences("restaurant_prefs", Context.MODE_PRIVATE)

    private val _order = MutableStateFlow<Order?>(null)
    val order: StateFlow<Order?> = _order.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isAdmin = MutableStateFlow(false)
    val isAdmin: StateFlow<Boolean> = _isAdmin.asStateFlow()

    // Payment States
    private val _paymentMethod = MutableStateFlow("Card") // "Card" or "Cash"
    val paymentMethod: StateFlow<String> = _paymentMethod.asStateFlow()

    // Card Input States
    private val _cardholderName = MutableStateFlow("")
    val cardholderName: StateFlow<String> = _cardholderName.asStateFlow()

    private val _cardNumber = MutableStateFlow("")
    val cardNumber: StateFlow<String> = _cardNumber.asStateFlow()

    private val _cardExpiry = MutableStateFlow("")
    val cardExpiry: StateFlow<String> = _cardExpiry.asStateFlow()

    private val _cardCvv = MutableStateFlow("")
    val cardCvv: StateFlow<String> = _cardCvv.asStateFlow()

    // Card Error States
    private val _cardholderNameError = MutableStateFlow<String?>(null)
    val cardholderNameError: StateFlow<String?> = _cardholderNameError.asStateFlow()

    private val _cardNumberError = MutableStateFlow<String?>(null)
    val cardNumberError: StateFlow<String?> = _cardNumberError.asStateFlow()

    private val _cardExpiryError = MutableStateFlow<String?>(null)
    val cardExpiryError: StateFlow<String?> = _cardExpiryError.asStateFlow()

    private val _cardCvvError = MutableStateFlow<String?>(null)
    val cardCvvError: StateFlow<String?> = _cardCvvError.asStateFlow()

    // Cash Input States
    private val _cashReceived = MutableStateFlow("")
    val cashReceived: StateFlow<String> = _cashReceived.asStateFlow()

    private val _cashReceivedError = MutableStateFlow<String?>(null)
    val cashReceivedError: StateFlow<String?> = _cashReceivedError.asStateFlow()

    private val _isCheckingOut = MutableStateFlow(false)
    val isCheckingOut: StateFlow<Boolean> = _isCheckingOut.asStateFlow()

    private val _checkoutError = MutableStateFlow<String?>(null)
    val checkoutError: StateFlow<String?> = _checkoutError.asStateFlow()

    init {
        viewModelScope.launch {
            if (authRepository.isLoggedIn) {
                val uid = authRepository.currentUserId
                val role = userRepository.getUserRole(uid)
                _isAdmin.value = role == UserRole.ADMIN
            }
        }
    }

    fun loadOrder(orderId: String) {
        orderRepository.startListeningToOrders()
        viewModelScope.launch {
            _isLoading.value = true
            orderRepository.getOrderByIdFlow(orderId).collect { updatedOrder ->
                _order.value = updatedOrder
                _isLoading.value = false
            }
        }
    }

    fun setPaymentMethod(method: String) {
        _paymentMethod.value = method
        clearValidationErrors()
    }

    // Dynamic Card Number Formatting (adds space every 4 digits, limits to 16 digits)
    fun onCardNumberChange(input: String) {
        val clean = input.replace(" ", "").filter { it.isDigit() }.take(16)
        val formatted = clean.chunked(4).joinToString(" ")
        _cardNumber.value = formatted
        _cardNumberError.value = null
    }

    // Dynamic Expiry Date Formatting (adds / after MM, limits to 5 chars MM/YY)
    fun onCardExpiryChange(input: String) {
        val clean = input.replace("/", "").filter { it.isDigit() }.take(4)
        val formatted = if (clean.length >= 2) {
            clean.substring(0, 2) + "/" + clean.substring(2)
        } else {
            clean
        }
        _cardExpiry.value = formatted
        _cardExpiryError.value = null
    }

    fun onCardholderNameChange(input: String) {
        _cardholderName.value = input
        _cardholderNameError.value = null
    }

    fun onCardCvvChange(input: String) {
        _cardCvv.value = input.filter { it.isDigit() }.take(3)
        _cardCvvError.value = null
    }

    fun onCashReceivedChange(input: String) {
        _cashReceived.value = input.filter { it == '.' || it.isDigit() }
        _cashReceivedError.value = null
    }

    // Computes billing breakdown
    val billingSummary: StateFlow<BillingBreakdown?> = _order.map { order ->
        if (order == null) return@map null

        val subtotal = order.totalAmount
        val discount = order.discount
        val netAmount = (subtotal - discount).coerceAtLeast(0.0)

        // Standard Taxes
        val tax = netAmount * 0.10 // 10% VAT
        val serviceCharge = netAmount * 0.05 // 5% Service Charge
        val finalTotal = netAmount + tax + serviceCharge

        BillingBreakdown(
            subtotal = subtotal,
            discount = discount,
            tax = tax,
            serviceCharge = serviceCharge,
            finalTotal = finalTotal
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Computes change dynamically for Cash payment (if admin)
    val changeToGive: StateFlow<Double> = combine(billingSummary, _cashReceived) { summary, cash ->
        if (summary == null) return@combine 0.0
        val cashAmount = cash.toDoubleOrNull() ?: 0.0
        (cashAmount - summary.finalTotal).coerceAtLeast(0.0)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    private fun clearValidationErrors() {
        _cardholderNameError.value = null
        _cardNumberError.value = null
        _cardExpiryError.value = null
        _cardCvvError.value = null
        _cashReceivedError.value = null
        _checkoutError.value = null
    }

    fun checkout(onSuccess: () -> Unit) {
        val currentOrder = _order.value ?: return
        val currentSummary = billingSummary.value ?: return

        if (_paymentMethod.value == "Card") {
            var isValid = true

            if (_cardholderName.value.isBlank()) {
                _cardholderNameError.value = "Cardholder name is required"
                isValid = false
            }

            val rawCardNo = _cardNumber.value.replace(" ", "")
            if (rawCardNo.length != 16) {
                _cardNumberError.value = "Card number must be 16 digits"
                isValid = false
            }

            val expiry = _cardExpiry.value
            if (expiry.length != 5 || !expiry.contains("/")) {
                _cardExpiryError.value = "Format must be MM/YY"
                isValid = false
            } else {
                val parts = expiry.split("/")
                val month = parts[0].toIntOrNull() ?: 0
                val year = parts[1].toIntOrNull() ?: 0
                if (month < 1 || month > 12) {
                    _cardExpiryError.value = "Invalid month (01-12)"
                    isValid = false
                } else {
                    val currentYearShort = Calendar.getInstance().get(Calendar.YEAR) % 100
                    val currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1
                    if (year < currentYearShort || (year == currentYearShort && month < currentMonth)) {
                        _cardExpiryError.value = "Card has expired"
                        isValid = false
                    }
                }
            }

            if (_cardCvv.value.length != 3) {
                _cardCvvError.value = "CVV must be 3 digits"
                isValid = false
            }

            if (!isValid) return
        } else {
            // Cash Checkout
            if (_isAdmin.value) {
                val cash = _cashReceived.value.toDoubleOrNull() ?: 0.0
                if (cash < currentSummary.finalTotal) {
                    _cashReceivedError.value = "Cash received is less than final total"
                    return
                }
            }
        }

        viewModelScope.launch {
            _isCheckingOut.value = true
            _checkoutError.value = null
            try {
                // Perform the checkout update in Repository (DB and Firestore)
                orderRepository.checkoutOrder(
                    orderId = currentOrder.id,
                    paymentMethod = _paymentMethod.value,
                    serviceCharge = currentSummary.serviceCharge,
                    taxAmount = currentSummary.tax,
                    finalTotal = currentSummary.finalTotal
                )

                // If table is locked on this device and matches this order, unlock it
                val lockedTable = sharedPrefs.getString("locked_table_number", "") ?: ""
                if (lockedTable == currentOrder.tableNumber && lockedTable.isNotBlank()) {
                    sharedPrefs.edit().remove("locked_table_number").apply()
                }

                // If order was saved locally as last placed order, remove it
                val lastOrderId = sharedPrefs.getString("last_placed_order_id", "") ?: ""
                if (lastOrderId == currentOrder.id) {
                    sharedPrefs.edit().remove("last_placed_order_id").apply()
                }

                onSuccess()
            } catch (e: Exception) {
                _checkoutError.value = e.localizedMessage ?: "Failed to process checkout"
            } finally {
                _isCheckingOut.value = false
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        orderRepository.stopListeningToOrders()
    }
}

data class BillingBreakdown(
    val subtotal: Double,
    val discount: Double,
    val tax: Double,
    val serviceCharge: Double,
    val finalTotal: Double
)
