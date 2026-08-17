package com.voxcina.shop.presentation.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.voxcina.shop.domain.model.CardDetails
import com.voxcina.shop.domain.model.Cart
import com.voxcina.shop.domain.model.Order
import com.voxcina.shop.domain.model.PaymentMethod
import com.voxcina.shop.domain.model.ShippingMethod
import com.voxcina.shop.domain.model.UserAddress
import com.voxcina.shop.domain.repository.AddressRepository
import com.voxcina.shop.domain.repository.CartRepository
import com.voxcina.shop.domain.repository.CheckoutRepository
import com.voxcina.shop.domain.repository.PaymentRepository
import com.voxcina.shop.domain.repository.ShippingRepository
import com.voxcina.shop.presentation.cart.AppliedVoucherTransfer
import com.voxcina.shop.util.AppError
import com.voxcina.shop.util.CartError
import com.voxcina.shop.util.CheckoutError
import com.voxcina.shop.util.Result
import com.voxcina.shop.util.ShippingError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Checkout screen.
 * Manages checkout state, shipping method selection, payment method selection,
 * card details, discount application, and order creation.
 *
 * Requirements: 3.1, 4.2, 4.4, 5.3, 6.1, 7.3, 8.5, 8.6, 8.7, 9.1
 */
@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val addressRepository: AddressRepository,
    private val shippingRepository: ShippingRepository,
    private val checkoutRepository: CheckoutRepository,
    private val paymentRepository: PaymentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<CheckoutUiState>(CheckoutUiState.Loading)
    val uiState: StateFlow<CheckoutUiState> = _uiState.asStateFlow()

    private val _snackbarMessage = MutableSharedFlow<String>()
    val snackbarMessage: SharedFlow<String> = _snackbarMessage.asSharedFlow()

    private val _notificationEvent = MutableSharedFlow<String>()
    val notificationEvent: SharedFlow<String> = _notificationEvent.asSharedFlow()

    private val _navigationEvent = MutableSharedFlow<CheckoutNavigationEvent>()
    val navigationEvent: SharedFlow<CheckoutNavigationEvent> = _navigationEvent.asSharedFlow()

    // Store discount code input separately from applied discount
    private var currentDiscountCode: String = ""

    init {
        loadInitialData()
    }

    /**
     * Handles UI events from the checkout screen.
     */
    fun onEvent(event: CheckoutEvent) {
        when (event) {
            is CheckoutEvent.Refresh -> loadInitialData()
            is CheckoutEvent.Retry -> loadInitialData()
            is CheckoutEvent.NavigateBack -> navigateBack()
            is CheckoutEvent.NavigateToAddresses -> navigateToAddresses()
            is CheckoutEvent.BottomNavClick -> { /* Navigation handled by UI */ }
            is CheckoutEvent.SelectDeliveryMethod -> selectDeliveryMethod(event.method)
            is CheckoutEvent.SelectPaymentMethod -> selectPaymentMethod(event.method)
            is CheckoutEvent.UpdateCardDetails -> updateCardDetails(event.cardDetails)
            is CheckoutEvent.UpdateCardNumber -> updateCardNumber(event.cardNumber)
            is CheckoutEvent.UpdateExpiryDate -> updateExpiryDate(event.expiryDate)
            is CheckoutEvent.UpdateCvv -> updateCvv(event.cvv)
            is CheckoutEvent.UpdateSaveCard -> updateSaveCard(event.saveCard)
            is CheckoutEvent.UpdateDiscountCode -> updateDiscountCode(event.code)
            is CheckoutEvent.ApplyDiscount -> applyDiscount(event.code)
            is CheckoutEvent.RemoveDiscount -> removeDiscount()
            is CheckoutEvent.ProcessCheckout -> processCheckout()
            is CheckoutEvent.ProceedToPayment -> proceedToPayment()
            is CheckoutEvent.PaymentMethodComingSoon -> {
                viewModelScope.launch {
                    _notificationEvent.emit("این روش پرداخت به زودی فعال خواهد شد")
                }
            }
            is CheckoutEvent.ExpandOrderDetails -> { /* Handled by UI */ }
            is CheckoutEvent.CollapseOrderDetails -> { /* Handled by UI */ }
        }
    }


    // ============ Data Loading ============

    /**
     * Loads initial checkout data: cart, default address, and shipping quotes.
     *
     * Requirements: 3.1, 4.4, 9.1
     */
    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.value = CheckoutUiState.Loading

            // Load cart first
            val cartResult = cartRepository.getCart()
            if (cartResult is Result.Error) {
                _uiState.value = CheckoutUiState.Error(
                    message = mapErrorToMessage(cartResult.error),
                    canRetry = true
                )
                return@launch
            }

            val cart = (cartResult as Result.Success).data
            if (cart.items.isEmpty()) {
                _uiState.value = CheckoutUiState.Error(
                    message = CheckoutError.EmptyCart.message,
                    canRetry = false
                )
                return@launch
            }

            // Load addresses
            val addressResult = addressRepository.getAddresses()
            val addresses = when (addressResult) {
                is Result.Success -> addressResult.data
                is Result.Error -> emptyList()
            }

            // Get default address or first address
            val defaultAddress = addresses.find { it.isDefault } ?: addresses.firstOrNull()

            // Set initial success state. If the user applied a voucher on the
            // cart screen, carry it over so it is not lost (and not re-activated)
            // at checkout.
            val transferredVoucher = AppliedVoucherTransfer.discount
            _uiState.value = CheckoutUiState.Success(
                cart = cart,
                selectedAddress = defaultAddress,
                shippingMethods = emptyList(),
                selectedShippingMethod = null,
                isShippingLoading = defaultAddress != null,
                discountState = if (transferredVoucher != null) {
                    CheckoutDiscountState.Applied(transferredVoucher)
                } else {
                    CheckoutDiscountState.Idle
                }
            )

            // Load shipping quotes if we have an address
            if (defaultAddress != null) {
                loadShippingQuotes(defaultAddress, cart)
            }
        }
    }

    /**
     * Loads shipping quotes based on the selected address.
     *
     * Requirements: 4.4
     */
    private fun loadShippingQuotes(address: UserAddress, cart: Cart) {
        viewModelScope.launch {
            // Set shipping loading state
            _uiState.update { state ->
                if (state is CheckoutUiState.Success) {
                    state.copy(isShippingLoading = true)
                } else state
            }

            val itemCount = cart.items.sumOf { it.quantity }
            val totalValue = cart.summary.subtotal

            val result = shippingRepository.getShippingQuotes(
                cityCode = address.cityCode,
                itemCount = itemCount,
                totalValue = totalValue
            )

            when (result) {
                is Result.Success -> {
                    val methods = result.data
                    _uiState.update { state ->
                        if (state is CheckoutUiState.Success) {
                            state.copy(
                                shippingMethods = methods,
                                selectedShippingMethod = methods.firstOrNull(),
                                isShippingLoading = false
                            )
                        } else state
                    }
                }
                is Result.Error -> {
                    _uiState.update { state ->
                        if (state is CheckoutUiState.Success) {
                            state.copy(
                                shippingMethods = emptyList(),
                                selectedShippingMethod = null,
                                isShippingLoading = false
                            )
                        } else state
                    }
                    _snackbarMessage.emit(mapErrorToMessage(result.error))
                }
            }
        }
    }

    /**
     * Refreshes shipping quotes when address changes.
     */
    fun refreshShippingQuotes() {
        val currentState = _uiState.value
        if (currentState !is CheckoutUiState.Success) return

        val address = currentState.selectedAddress ?: return
        loadShippingQuotes(address, currentState.cart)
    }

    /**
     * Updates the selected address and refreshes shipping quotes.
     */
    fun updateSelectedAddress(address: UserAddress) {
        _uiState.update { state ->
            if (state is CheckoutUiState.Success) {
                state.copy(
                    selectedAddress = address,
                    shippingMethods = emptyList(),
                    selectedShippingMethod = null,
                    validationErrors = state.validationErrors - CheckoutValidationFields.ADDRESS
                )
            } else state
        }

        // Reload shipping quotes for new address
        val currentState = _uiState.value
        if (currentState is CheckoutUiState.Success) {
            loadShippingQuotes(address, currentState.cart)
        }
    }


    // ============ Selection Handlers ============

    /**
     * Handles delivery method selection.
     *
     * Requirements: 4.2
     */
    private fun selectDeliveryMethod(method: ShippingMethod) {
        _uiState.update { state ->
            if (state is CheckoutUiState.Success) {
                state.copy(
                    selectedShippingMethod = method,
                    validationErrors = state.validationErrors - CheckoutValidationFields.SHIPPING_METHOD
                )
            } else state
        }
    }

    /**
     * Handles payment method selection.
     *
     * Requirements: 5.3
     */
    private fun selectPaymentMethod(method: PaymentMethod) {
        _uiState.update { state ->
            if (state is CheckoutUiState.Success) {
                state.copy(
                    selectedPaymentMethod = method
                )
            } else state
        }
    }

    // ============ Card Details Handlers ============

    /**
     * Updates all card details at once.
     *
     * Requirements: 6.1
     */
    private fun updateCardDetails(cardDetails: CardDetails) {
        _uiState.update { state ->
            if (state is CheckoutUiState.Success) {
                state.copy(
                    cardDetails = cardDetails,
                    validationErrors = clearCardValidationErrors(state.validationErrors)
                )
            } else state
        }
    }

    /**
     * Updates the card number field.
     *
     * Requirements: 6.1, 6.2
     */
    private fun updateCardNumber(cardNumber: String) {
        _uiState.update { state ->
            if (state is CheckoutUiState.Success) {
                // Remove non-digit characters and limit to 16 digits
                val digitsOnly = cardNumber.filter { it.isDigit() }.take(16)
                state.copy(
                    cardDetails = state.cardDetails.copy(cardNumber = digitsOnly),
                    validationErrors = state.validationErrors - CheckoutValidationFields.CARD_NUMBER
                )
            } else state
        }
    }

    /**
     * Updates the expiry date field.
     *
     * Requirements: 6.1
     */
    private fun updateExpiryDate(expiryDate: String) {
        _uiState.update { state ->
            if (state is CheckoutUiState.Success) {
                // Remove non-digit characters and limit to 4 digits
                val digitsOnly = expiryDate.filter { it.isDigit() }.take(4)
                state.copy(
                    cardDetails = state.cardDetails.copy(expiryDate = digitsOnly),
                    validationErrors = state.validationErrors - CheckoutValidationFields.CARD_EXPIRY
                )
            } else state
        }
    }

    /**
     * Updates the CVV field.
     *
     * Requirements: 6.1, 6.3
     */
    private fun updateCvv(cvv: String) {
        _uiState.update { state ->
            if (state is CheckoutUiState.Success) {
                // Remove non-digit characters and limit to 4 digits
                val digitsOnly = cvv.filter { it.isDigit() }.take(4)
                state.copy(
                    cardDetails = state.cardDetails.copy(cvv = digitsOnly),
                    validationErrors = state.validationErrors - CheckoutValidationFields.CARD_CVV
                )
            } else state
        }
    }

    /**
     * Updates the save card checkbox.
     *
     * Requirements: 6.5
     */
    private fun updateSaveCard(saveCard: Boolean) {
        _uiState.update { state ->
            if (state is CheckoutUiState.Success) {
                state.copy(
                    cardDetails = state.cardDetails.copy(saveCard = saveCard)
                )
            } else state
        }
    }

    private fun clearCardValidationErrors(errors: Map<String, String>): Map<String, String> {
        return errors - setOf(
            CheckoutValidationFields.CARD_NUMBER,
            CheckoutValidationFields.CARD_EXPIRY,
            CheckoutValidationFields.CARD_CVV
        )
    }


    // ============ Discount Handlers ============

    /**
     * Updates the discount code input (without applying).
     */
    private fun updateDiscountCode(code: String) {
        currentDiscountCode = code
    }

    /**
     * Applies a discount code / voucher.
     * Validates against the backend (admin code or negotiated / cart-recovery
     * coupon) and checks minimum order requirements. After a successful apply,
     * the voucher is marked as used on the backend.
     *
     * Requirements: 7.3
     */
    private fun applyDiscount(code: String) {
        val currentState = _uiState.value
        if (currentState !is CheckoutUiState.Success) return

        val trimmedCode = code.trim()
        if (trimmedCode.isBlank()) return

        // Set discount loading state
        _uiState.update { state ->
            if (state is CheckoutUiState.Success) {
                state.copy(discountState = CheckoutDiscountState.Loading)
            } else state
        }

        viewModelScope.launch {
            val cart = (uiState.value as? CheckoutUiState.Success)?.cart ?: return@launch

            when (val result = cartRepository.applyVoucher(trimmedCode, cart)) {
                is Result.Success -> {
                    val discount = result.data
                    val updatedCart = (uiState.value as? CheckoutUiState.Success)?.cart ?: cart

                    // Check minimum order amount (admin codes only; negotiated
                    // and cart-recovery coupons are validated server-side)
                    if (updatedCart.summary.subtotal < discount.minOrderAmount) {
                        _uiState.update { state ->
                            if (state is CheckoutUiState.Success) {
                                state.copy(
                                    discountState = CheckoutDiscountState.Error(
                                        "حداقل مبلغ سفارش ${formatPrice(discount.minOrderAmount)} تومان است"
                                    )
                                )
                            } else state
                        }
                        return@launch
                    }

                    // Apply discount
                    _uiState.update { state ->
                        if (state is CheckoutUiState.Success) {
                            state.copy(discountState = CheckoutDiscountState.Applied(discount))
                        } else state
                    }
                    AppliedVoucherTransfer.set(discount)

                    // Mark the voucher as used on the backend (fire-and-forget,
                    // mirroring the web front-end /discounts/activate call).
                    // The backend guards the increment against the usage cap
                    // and treats `used` as "applied to cart", so activation is
                    // safe for every voucher type.
                    cartRepository.activateVoucher(discount.code)
                }
                is Result.Error -> {
                    _uiState.update { state ->
                        if (state is CheckoutUiState.Success) {
                            state.copy(
                                discountState = CheckoutDiscountState.Error(mapErrorToMessage(result.error))
                            )
                        } else state
                    }
                }
            }
        }
    }

    /**
     * Removes the applied discount and deactivates the voucher on the backend.
     */
    private fun removeDiscount() {
        val appliedDiscount = (uiState.value as? CheckoutUiState.Success)?.appliedDiscount

        _uiState.update { state ->
            if (state is CheckoutUiState.Success) {
                state.copy(discountState = CheckoutDiscountState.Idle)
            } else state
        }
        currentDiscountCode = ""
        AppliedVoucherTransfer.clear()

        if (appliedDiscount != null) {
            viewModelScope.launch {
                cartRepository.deactivateVoucher(appliedDiscount.code)
            }
        }
    }


    // ============ Checkout Processing ============

    /**
     * Validates all required fields and processes checkout.
     *
     * Requirements: 8.5, 8.6, 8.7
     */
    private fun processCheckout() {
        val currentState = _uiState.value
        if (currentState !is CheckoutUiState.Success) return

        // Validate all fields
        val validationErrors = validateCheckout(currentState)
        if (validationErrors.isNotEmpty()) {
            _uiState.update { state ->
                if (state is CheckoutUiState.Success) {
                    state.copy(validationErrors = validationErrors)
                } else state
            }
            viewModelScope.launch {
                _snackbarMessage.emit("لطفاً اطلاعات را تکمیل کنید")
            }
            return
        }

        // Set processing state
        _uiState.update { state ->
            if (state is CheckoutUiState.Success) {
                state.copy(isProcessing = true, validationErrors = emptyMap())
            } else state
        }

        viewModelScope.launch {
            val result = checkoutRepository.createOrder(
                items = currentState.cart.items,
                totalAmount = currentState.totalAmount,
                shippingAddress = currentState.selectedAddress!!,
                shippingCost = currentState.shippingCost,
                discountAmount = currentState.appliedDiscount?.let { currentState.calculateDiscountAmount(it) } ?: 0L,
                promoCode = currentState.appliedDiscount?.code
            )

            when (result) {
                is Result.Success -> {
                    val order = result.data
                    _uiState.update { state ->
                        if (state is CheckoutUiState.Success) {
                            state.copy(isProcessing = false)
                        } else state
                    }
                    // Navigate to payment or success screen
                    _navigationEvent.emit(CheckoutNavigationEvent.PaymentSuccess(order.id))
                }
                is Result.Error -> {
                    _uiState.update { state ->
                        if (state is CheckoutUiState.Success) {
                            state.copy(isProcessing = false)
                        } else state
                    }
                    _snackbarMessage.emit(mapErrorToMessage(result.error))
                }
            }
        }
    }

    /**
     * Validates checkout state and returns validation errors.
     *
     * Requirements: 8.5, 8.6
     */
    private fun validateCheckout(state: CheckoutUiState.Success): Map<String, String> {
        val errors = mutableMapOf<String, String>()

        // Validate address
        if (state.selectedAddress == null) {
            errors[CheckoutValidationFields.ADDRESS] = "لطفاً آدرس تحویل را انتخاب کنید"
        }

        // Validate shipping method
        if (state.selectedShippingMethod == null) {
            errors[CheckoutValidationFields.SHIPPING_METHOD] = "لطفاً روش ارسال را انتخاب کنید"
        }

        // Validate payment method
        if (state.selectedPaymentMethod != PaymentMethod.ZIBAL && 
            state.selectedPaymentMethod != PaymentMethod.DIGIPAY) {
            errors["payment_method"] = "لطفاً روش پرداخت را انتخاب کنید"
        }

        return errors
    }


    // ============ Navigation ============

    private fun navigateBack() {
        viewModelScope.launch {
            _navigationEvent.emit(CheckoutNavigationEvent.NavigateBack)
        }
    }

    private fun navigateToAddresses() {
        viewModelScope.launch {
            _navigationEvent.emit(CheckoutNavigationEvent.NavigateToAddresses)
        }
    }

    // ============ Payment Handlers ============

    /**
     * Proceed to payment: create order then request payment from Zibal.
     */
    private fun proceedToPayment() {
        viewModelScope.launch {
            val state = _uiState.value
            if (state !is CheckoutUiState.Success) return@launch
            
            val selectedAddress = state.selectedAddress ?: run {
                _snackbarMessage.emit("لطفا آدرس تحویل را انتخاب کنید")
                return@launch
            }

            _uiState.update { s ->
                if (s is CheckoutUiState.Success) s.copy(isPaymentLoading = true) else s
            }

            // Step 1: Create order
            val orderResult = checkoutRepository.createOrder(
                items = state.cart.items,
                totalAmount = state.totalAmount,
                shippingAddress = selectedAddress,
                shippingCost = state.shippingCost,
                discountAmount = state.appliedDiscount?.let { state.calculateDiscountAmount(it) } ?: 0L,
                promoCode = state.appliedDiscount?.code
            )

            when (orderResult) {
                is Result.Success -> {
                    val order = orderResult.data
                    val gateway = when (state.selectedPaymentMethod) {
                        PaymentMethod.ZIBAL -> "zibal"
                        PaymentMethod.DIGIPAY -> "digipay"
                        else -> "zibal"
                    }
                    // Step 2: Request payment
                    val paymentResult = paymentRepository.requestPayment(
                        orderId = order.id,
                        gateway = gateway
                    )
                    
                    when (paymentResult) {
                        is Result.Success -> {
                            _navigationEvent.emit(
                                CheckoutNavigationEvent.RedirectToPayment(
                                    payUrl = paymentResult.data.payUrl,
                                    orderId = order.id,
                                    trackId = paymentResult.data.trackId
                                )
                            )
                        }
                        is Result.Error -> {
                            _uiState.update { s ->
                                if (s is CheckoutUiState.Success) s.copy(isPaymentLoading = false) else s
                            }
                            _notificationEvent.emit(mapErrorToMessage(paymentResult.error))
                        }
                    }
                }
                is Result.Error -> {
                    _uiState.update { s ->
                        if (s is CheckoutUiState.Success) s.copy(isPaymentLoading = false) else s
                    }
                    _notificationEvent.emit(mapErrorToMessage(orderResult.error))
                }
            }
        }
    }

    /**
     * Request payment from a gateway.
     */
    fun requestPayment(orderId: String, gateway: String) {
        viewModelScope.launch {
            _uiState.update { state ->
                if (state is CheckoutUiState.Success) {
                    state.copy(isPaymentLoading = true)
                } else state
            }

            val result = paymentRepository.requestPayment(
                orderId = orderId,
                gateway = gateway
            )

            when (result) {
                is Result.Success -> {
                    _navigationEvent.emit(
                        CheckoutNavigationEvent.RedirectToPayment(
                            payUrl = result.data.payUrl,
                            orderId = orderId,
                            trackId = result.data.trackId
                        )
                    )
                }
                is Result.Error -> {
                    _uiState.update { state ->
                        if (state is CheckoutUiState.Success) {
                            state.copy(isPaymentLoading = false)
                        } else state
                    }
                    _snackbarMessage.emit(mapErrorToMessage(result.error))
                }
            }
        }
    }

    /**
     * Verify payment after callback.
     */
    fun verifyPayment(trackId: String, gateway: String) {
        viewModelScope.launch {
            _uiState.update { state ->
                if (state is CheckoutUiState.Success) {
                    state.copy(isPaymentLoading = true)
                } else state
            }

            val result = paymentRepository.verifyPayment(trackId, gateway)

            when (result) {
                is Result.Success -> {
                    if (result.data.isSuccess) {
                        _navigationEvent.emit(
                            CheckoutNavigationEvent.PaymentSuccess(result.data.orderId ?: "")
                        )
                    } else {
                        _snackbarMessage.emit("پرداخت ناموفق: ${result.data.statusText ?: "خطا"}")
                    }
                }
                is Result.Error -> {
                    _snackbarMessage.emit(mapErrorToMessage(result.error))
                }
            }

            _uiState.update { state ->
                if (state is CheckoutUiState.Success) {
                    state.copy(isPaymentLoading = false)
                } else state
            }
        }
    }

    // ============ Utility Functions ============

    /**
     * Maps AppError to user-friendly Persian error messages.
     */
    private fun mapErrorToMessage(error: AppError): String {
        return when (error) {
            is AppError.NetworkError -> "خطا در اتصال به سرور"
            is CartError.CartLoadFailed -> "خطا در بارگذاری سبد خرید"
            is CartError.DiscountInvalid -> "کد تخفیف نامعتبر است"
            is CartError.DiscountExpired -> "کد تخفیف منقضی شده است"
            is CartError.DiscountMinOrderNotMet -> 
                "حداقل مبلغ سفارش ${formatPrice(error.minAmount)} تومان است"
            is ShippingError.ShippingQuotesLoadFailed -> "خطا در دریافت روش‌های ارسال"
            is ShippingError.InvalidCityCode -> "کد شهر نامعتبر است"
            is ShippingError.NoShippingMethodsAvailable -> "روش ارسالی برای این مقصد موجود نیست"
            is CheckoutError.OrderCreationFailed -> "خطا در ثبت سفارش. لطفاً دوباره تلاش کنید"
            is CheckoutError.MissingAddress -> "لطفاً آدرس تحویل را انتخاب کنید"
            is CheckoutError.MissingShippingMethod -> "لطفاً روش ارسال را انتخاب کنید"
            is CheckoutError.InvalidCardDetails -> "اطلاعات کارت نامعتبر است"
            is CheckoutError.EmptyCart -> "سبد خرید خالی است"
            is AppError.ServerError -> error.message
            is AppError.UnknownError -> error.message
            else -> error.message
        }
    }

    /**
     * Formats price with thousand separators.
     */
    private fun formatPrice(price: Long): String {
        return String.format("%,d", price)
    }
}

/**
 * Sealed class for checkout navigation events.
 */
sealed class CheckoutNavigationEvent {
    data object NavigateBack : CheckoutNavigationEvent()
    data object NavigateToAddresses : CheckoutNavigationEvent()
    data class RedirectToPayment(val payUrl: String, val orderId: String, val trackId: String) : CheckoutNavigationEvent()
    data class PaymentSuccess(val orderId: String) : CheckoutNavigationEvent()
}
