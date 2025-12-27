package com.voxcina.shop.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.voxcina.shop.domain.usecase.CheckPhoneExistsUseCase
import com.voxcina.shop.domain.usecase.LoginWithOtpUseCase
import com.voxcina.shop.domain.usecase.LoginWithPasswordUseCase
import com.voxcina.shop.domain.usecase.OtpFlow
import com.voxcina.shop.domain.usecase.ResetPasswordUseCase
import com.voxcina.shop.domain.usecase.SendOtpUseCase
import com.voxcina.shop.domain.usecase.SignupWithOtpUseCase
import com.voxcina.shop.domain.validator.PasswordValidator
import com.voxcina.shop.domain.validator.PhoneValidator
import com.voxcina.shop.util.AppError
import com.voxcina.shop.util.AuthError
import com.voxcina.shop.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the authentication screen.
 * Manages UI state and handles all authentication-related events.
 *
 * Requirements: 1.4, 2.2, 2.3, 2.4, 3.4, 3.5, 4.3, 4.7, 4.8, 5.3, 5.9, 6.5, 6.6, 6.7, 6.9, 7.2, 7.3, 7.5, 10.1-10.6
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val checkPhoneExistsUseCase: CheckPhoneExistsUseCase,
    private val loginWithPasswordUseCase: LoginWithPasswordUseCase,
    private val loginWithOtpUseCase: LoginWithOtpUseCase,
    private val signupWithOtpUseCase: SignupWithOtpUseCase,
    private val sendOtpUseCase: SendOtpUseCase,
    private val resetPasswordUseCase: ResetPasswordUseCase,
    private val phoneValidator: PhoneValidator,
    private val passwordValidator: PasswordValidator
) : ViewModel() {

    companion object {
        private const val OTP_RESEND_COUNTDOWN_SECONDS = 120
    }

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.PhoneEntry())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private var countdownJob: Job? = null

    /**
     * Handles all authentication events from the UI.
     */
    fun onEvent(event: AuthEvent) {
        when (event) {
            // Phone Entry Events
            is AuthEvent.PhoneChanged -> handlePhoneChanged(event.phone)
            is AuthEvent.SubmitPhone -> handleSubmitPhone()
            
            // Login Events
            is AuthEvent.PasswordChanged -> handlePasswordChanged(event.password)
            is AuthEvent.TogglePasswordVisibility -> handleTogglePasswordVisibility()
            is AuthEvent.SubmitLogin -> handleSubmitLogin()
            is AuthEvent.RequestOtpLogin -> handleRequestOtpLogin()
            is AuthEvent.ForgotPassword -> handleForgotPassword()
            
            // Signup Events
            is AuthEvent.FirstNameChanged -> handleFirstNameChanged(event.firstName)
            is AuthEvent.LastNameChanged -> handleLastNameChanged(event.lastName)
            is AuthEvent.SubmitSignup -> handleSubmitSignup()
            
            // OTP Events
            is AuthEvent.OtpChanged -> handleOtpChanged(event.otp)
            is AuthEvent.SubmitOtp -> handleSubmitOtp()
            is AuthEvent.ResendOtp -> handleResendOtp()
            is AuthEvent.OtpAutoFilled -> handleOtpAutoFilled(event.otp)
            
            // Password Creation Events
            is AuthEvent.NewPasswordChanged -> handleNewPasswordChanged(event.password)
            is AuthEvent.ConfirmPasswordChanged -> handleConfirmPasswordChanged(event.password)
            is AuthEvent.ToggleNewPasswordVisibility -> handleToggleNewPasswordVisibility()
            is AuthEvent.SubmitNewPassword -> handleSubmitNewPassword()
            
            // Navigation Events
            is AuthEvent.NavigateBack -> handleNavigateBack()
            is AuthEvent.ClearError -> handleClearError()
        }
    }


    // ==================== Phone Entry Handlers ====================

    private fun handlePhoneChanged(phone: String) {
        val currentState = _uiState.value
        if (currentState is AuthUiState.PhoneEntry) {
            val normalizedPhone = phoneValidator.normalizePhone(phone)
            _uiState.update {
                currentState.copy(
                    phone = normalizedPhone,
                    phoneError = null // Clear error when user starts typing (Requirement 10.6)
                )
            }
        }
    }

    private fun handleSubmitPhone() {
        val currentState = _uiState.value
        if (currentState !is AuthUiState.PhoneEntry) return

        val normalizedPhone = phoneValidator.normalizePhone(currentState.phone)
        val validationResult = phoneValidator.validate(normalizedPhone)

        if (!validationResult.isValid) {
            _uiState.update {
                currentState.copy(phoneError = validationResult.errorMessage)
            }
            return
        }

        _uiState.update { currentState.copy(isLoading = true, phoneError = null) }

        viewModelScope.launch {
            when (val result = checkPhoneExistsUseCase(normalizedPhone)) {
                is Result.Success -> {
                    if (result.data) {
                        // Phone exists - go to login (Requirement 2.2)
                        _uiState.value = AuthUiState.Login(phone = normalizedPhone)
                    } else {
                        // Phone doesn't exist - go to signup (Requirement 2.3)
                        _uiState.value = AuthUiState.Signup(phone = normalizedPhone)
                    }
                }
                is Result.Error -> {
                    _uiState.update {
                        currentState.copy(
                            isLoading = false,
                            phoneError = mapErrorToMessage(result.error)
                        )
                    }
                }
            }
        }
    }

    // ==================== Login Handlers ====================

    private fun handlePasswordChanged(password: String) {
        val currentState = _uiState.value
        if (currentState is AuthUiState.Login) {
            _uiState.update {
                currentState.copy(
                    password = password,
                    error = null // Clear error when user starts typing
                )
            }
        }
    }

    private fun handleTogglePasswordVisibility() {
        val currentState = _uiState.value
        if (currentState is AuthUiState.Login) {
            _uiState.update {
                currentState.copy(passwordVisible = !currentState.passwordVisible)
            }
        }
    }

    private fun handleSubmitLogin() {
        val currentState = _uiState.value
        if (currentState !is AuthUiState.Login) return

        if (currentState.password.isEmpty()) {
            _uiState.update { currentState.copy(error = "رمز عبور را وارد کنید") }
            return
        }

        _uiState.update { currentState.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            when (val result = loginWithPasswordUseCase(currentState.phone, currentState.password)) {
                is Result.Success -> {
                    // Login successful (Requirement 3.4)
                    _uiState.value = AuthUiState.Success(message = "ورود موفقیت‌آمیز")
                }
                is Result.Error -> {
                    _uiState.update {
                        currentState.copy(
                            isLoading = false,
                            error = mapErrorToMessage(result.error)
                        )
                    }
                }
            }
        }
    }

    private fun handleRequestOtpLogin() {
        val currentState = _uiState.value
        if (currentState !is AuthUiState.Login) return

        _uiState.update { currentState.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            when (val result = sendOtpUseCase(currentState.phone, OtpFlow.LOGIN)) {
                is Result.Success -> {
                    // Navigate to OTP verification (Requirement 4.3)
                    _uiState.value = AuthUiState.OtpVerification(
                        phone = currentState.phone,
                        flow = OtpFlow.LOGIN,
                        resendCountdown = OTP_RESEND_COUNTDOWN_SECONDS
                    )
                    startResendCountdown()
                }
                is Result.Error -> {
                    _uiState.update {
                        currentState.copy(
                            isLoading = false,
                            error = mapErrorToMessage(result.error)
                        )
                    }
                }
            }
        }
    }

    private fun handleForgotPassword() {
        val currentState = _uiState.value
        if (currentState !is AuthUiState.Login) return

        _uiState.update { currentState.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            when (val result = sendOtpUseCase(currentState.phone, OtpFlow.FORGOT_PASSWORD)) {
                is Result.Success -> {
                    // Navigate to OTP verification for password reset (Requirement 7.2)
                    _uiState.value = AuthUiState.OtpVerification(
                        phone = currentState.phone,
                        flow = OtpFlow.FORGOT_PASSWORD,
                        resendCountdown = OTP_RESEND_COUNTDOWN_SECONDS
                    )
                    startResendCountdown()
                }
                is Result.Error -> {
                    _uiState.update {
                        currentState.copy(
                            isLoading = false,
                            error = mapErrorToMessage(result.error)
                        )
                    }
                }
            }
        }
    }


    // ==================== Signup Handlers ====================

    private fun handleFirstNameChanged(firstName: String) {
        val currentState = _uiState.value
        if (currentState is AuthUiState.Signup) {
            _uiState.update {
                currentState.copy(
                    firstName = firstName,
                    error = null
                )
            }
        }
    }

    private fun handleLastNameChanged(lastName: String) {
        val currentState = _uiState.value
        if (currentState is AuthUiState.Signup) {
            _uiState.update {
                currentState.copy(
                    lastName = lastName,
                    error = null
                )
            }
        }
    }

    private fun handleSubmitSignup() {
        val currentState = _uiState.value
        if (currentState !is AuthUiState.Signup) return

        // Validate name fields
        if (currentState.firstName.isBlank()) {
            _uiState.update { currentState.copy(error = "نام را وارد کنید") }
            return
        }
        if (currentState.lastName.isBlank()) {
            _uiState.update { currentState.copy(error = "نام خانوادگی را وارد کنید") }
            return
        }

        _uiState.update { currentState.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            when (val result = sendOtpUseCase(
                phone = currentState.phone,
                flow = OtpFlow.SIGNUP,
                firstName = currentState.firstName,
                lastName = currentState.lastName
            )) {
                is Result.Success -> {
                    // Navigate to OTP verification (Requirement 5.3)
                    _uiState.value = AuthUiState.OtpVerification(
                        phone = currentState.phone,
                        flow = OtpFlow.SIGNUP,
                        firstName = currentState.firstName,
                        lastName = currentState.lastName,
                        resendCountdown = OTP_RESEND_COUNTDOWN_SECONDS
                    )
                    startResendCountdown()
                }
                is Result.Error -> {
                    _uiState.update {
                        currentState.copy(
                            isLoading = false,
                            error = mapErrorToMessage(result.error)
                        )
                    }
                }
            }
        }
    }

    // ==================== OTP Handlers ====================

    private fun handleOtpChanged(otp: String) {
        val currentState = _uiState.value
        if (currentState is AuthUiState.OtpVerification) {
            // Only allow digits and max 5 characters
            val filteredOtp = otp.filter { it.isDigit() }.take(5)
            _uiState.update {
                currentState.copy(
                    otp = filteredOtp,
                    error = null
                )
            }
        }
    }

    private fun handleOtpAutoFilled(otp: String) {
        val currentState = _uiState.value
        if (currentState is AuthUiState.OtpVerification) {
            val filteredOtp = otp.filter { it.isDigit() }.take(5)
            _uiState.update {
                currentState.copy(otp = filteredOtp, error = null)
            }
            // Auto-submit if OTP is complete (Requirement 6.3)
            if (filteredOtp.length == 5) {
                handleSubmitOtp()
            }
        }
    }

    private fun handleSubmitOtp() {
        val currentState = _uiState.value
        if (currentState !is AuthUiState.OtpVerification) return

        if (currentState.otp.length != 5) {
            _uiState.update { currentState.copy(error = "کد تأیید باید ۵ رقم باشد") }
            return
        }

        _uiState.update { currentState.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            when (currentState.flow) {
                OtpFlow.LOGIN -> verifyOtpForLogin(currentState)
                OtpFlow.SIGNUP -> verifyOtpForSignup(currentState)
                OtpFlow.FORGOT_PASSWORD -> verifyOtpForPasswordReset(currentState)
            }
        }
    }

    private suspend fun verifyOtpForLogin(state: AuthUiState.OtpVerification) {
        when (val result = loginWithOtpUseCase(state.phone, state.otp)) {
            is Result.Success -> {
                cancelCountdown()
                _uiState.value = AuthUiState.Success(message = "ورود موفقیت‌آمیز")
            }
            is Result.Error -> {
                _uiState.update {
                    state.copy(
                        isLoading = false,
                        error = mapErrorToMessage(result.error)
                    )
                }
            }
        }
    }

    private suspend fun verifyOtpForSignup(state: AuthUiState.OtpVerification) {
        // For signup, OTP verification leads to password creation (Requirement 5.4)
        cancelCountdown()
        _uiState.value = AuthUiState.PasswordCreation(
            phone = state.phone,
            otp = state.otp,
            flow = PasswordFlow.SIGNUP,
            firstName = state.firstName,
            lastName = state.lastName
        )
    }

    private suspend fun verifyOtpForPasswordReset(state: AuthUiState.OtpVerification) {
        // For password reset, OTP verification leads to password creation (Requirement 7.3)
        cancelCountdown()
        _uiState.value = AuthUiState.PasswordCreation(
            phone = state.phone,
            otp = state.otp,
            flow = PasswordFlow.RESET
        )
    }

    private fun handleResendOtp() {
        val currentState = _uiState.value
        if (currentState !is AuthUiState.OtpVerification) return
        if (currentState.resendCountdown > 0) return // Still in cooldown

        _uiState.update { currentState.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            val result = when (currentState.flow) {
                OtpFlow.LOGIN -> sendOtpUseCase(currentState.phone, OtpFlow.LOGIN)
                OtpFlow.SIGNUP -> sendOtpUseCase(
                    phone = currentState.phone,
                    flow = OtpFlow.SIGNUP,
                    firstName = currentState.firstName,
                    lastName = currentState.lastName
                )
                OtpFlow.FORGOT_PASSWORD -> sendOtpUseCase(currentState.phone, OtpFlow.FORGOT_PASSWORD)
            }

            when (result) {
                is Result.Success -> {
                    _uiState.update {
                        currentState.copy(
                            isLoading = false,
                            otp = "",
                            resendCountdown = OTP_RESEND_COUNTDOWN_SECONDS
                        )
                    }
                    startResendCountdown()
                }
                is Result.Error -> {
                    _uiState.update {
                        currentState.copy(
                            isLoading = false,
                            error = mapErrorToMessage(result.error)
                        )
                    }
                }
            }
        }
    }


    // ==================== Password Creation Handlers ====================

    private fun handleNewPasswordChanged(password: String) {
        val currentState = _uiState.value
        if (currentState is AuthUiState.PasswordCreation) {
            val validationResult = passwordValidator.validate(password)
            _uiState.update {
                currentState.copy(
                    password = password,
                    passwordError = if (password.isNotEmpty() && !validationResult.isValid) {
                        validationResult.errorMessage
                    } else null,
                    error = null
                )
            }
        }
    }

    private fun handleConfirmPasswordChanged(password: String) {
        val currentState = _uiState.value
        if (currentState is AuthUiState.PasswordCreation) {
            _uiState.update {
                currentState.copy(
                    confirmPassword = password,
                    error = null
                )
            }
        }
    }

    private fun handleToggleNewPasswordVisibility() {
        val currentState = _uiState.value
        if (currentState is AuthUiState.PasswordCreation) {
            _uiState.update {
                currentState.copy(passwordVisible = !currentState.passwordVisible)
            }
        }
    }

    private fun handleSubmitNewPassword() {
        val currentState = _uiState.value
        if (currentState !is AuthUiState.PasswordCreation) return

        // Validate password strength (Requirement 5.5)
        val passwordValidation = passwordValidator.validate(currentState.password)
        if (!passwordValidation.isValid) {
            _uiState.update {
                currentState.copy(passwordError = passwordValidation.errorMessage)
            }
            return
        }

        // Validate password match (Requirement 5.6)
        val matchValidation = passwordValidator.validateMatch(
            currentState.password,
            currentState.confirmPassword
        )
        if (!matchValidation.isValid) {
            _uiState.update {
                currentState.copy(error = matchValidation.errorMessage)
            }
            return
        }

        _uiState.update { currentState.copy(isLoading = true, error = null, passwordError = null) }

        viewModelScope.launch {
            when (currentState.flow) {
                PasswordFlow.SIGNUP -> completeSignup(currentState)
                PasswordFlow.RESET -> completePasswordReset(currentState)
            }
        }
    }

    private suspend fun completeSignup(state: AuthUiState.PasswordCreation) {
        when (val result = signupWithOtpUseCase(
            phone = state.phone,
            otp = state.otp,
            password = state.password,
            confirmPassword = state.confirmPassword
        )) {
            is Result.Success -> {
                _uiState.value = AuthUiState.Success(message = "ثبت‌نام موفقیت‌آمیز")
            }
            is Result.Error -> {
                _uiState.update {
                    state.copy(
                        isLoading = false,
                        error = mapErrorToMessage(result.error)
                    )
                }
            }
        }
    }

    private suspend fun completePasswordReset(state: AuthUiState.PasswordCreation) {
        when (val result = resetPasswordUseCase(
            phone = state.phone,
            otp = state.otp,
            password = state.password,
            confirmPassword = state.confirmPassword
        )) {
            is Result.Success -> {
                // Return to login after successful password reset (Requirement 7.5)
                _uiState.value = AuthUiState.Login(
                    phone = state.phone,
                    error = null
                )
            }
            is Result.Error -> {
                _uiState.update {
                    state.copy(
                        isLoading = false,
                        error = mapErrorToMessage(result.error)
                    )
                }
            }
        }
    }

    // ==================== Navigation Handlers ====================

    private fun handleNavigateBack() {
        cancelCountdown()
        when (val currentState = _uiState.value) {
            is AuthUiState.Login -> {
                _uiState.value = AuthUiState.PhoneEntry(phone = currentState.phone)
            }
            is AuthUiState.Signup -> {
                _uiState.value = AuthUiState.PhoneEntry(phone = currentState.phone)
            }
            is AuthUiState.OtpVerification -> {
                when (currentState.flow) {
                    OtpFlow.LOGIN -> _uiState.value = AuthUiState.Login(phone = currentState.phone)
                    OtpFlow.SIGNUP -> _uiState.value = AuthUiState.Signup(
                        phone = currentState.phone,
                        firstName = currentState.firstName ?: "",
                        lastName = currentState.lastName ?: ""
                    )
                    OtpFlow.FORGOT_PASSWORD -> _uiState.value = AuthUiState.Login(phone = currentState.phone)
                }
            }
            is AuthUiState.PasswordCreation -> {
                when (currentState.flow) {
                    PasswordFlow.SIGNUP -> _uiState.value = AuthUiState.OtpVerification(
                        phone = currentState.phone,
                        flow = OtpFlow.SIGNUP,
                        firstName = currentState.firstName,
                        lastName = currentState.lastName
                    )
                    PasswordFlow.RESET -> _uiState.value = AuthUiState.OtpVerification(
                        phone = currentState.phone,
                        flow = OtpFlow.FORGOT_PASSWORD
                    )
                }
            }
            is AuthUiState.PhoneEntry, is AuthUiState.Success -> {
                // No back navigation from these states
            }
        }
    }

    private fun handleClearError() {
        when (val currentState = _uiState.value) {
            is AuthUiState.PhoneEntry -> _uiState.update { currentState.copy(phoneError = null) }
            is AuthUiState.Login -> _uiState.update { currentState.copy(error = null) }
            is AuthUiState.Signup -> _uiState.update { currentState.copy(error = null) }
            is AuthUiState.OtpVerification -> _uiState.update { currentState.copy(error = null) }
            is AuthUiState.PasswordCreation -> _uiState.update { 
                currentState.copy(error = null, passwordError = null) 
            }
            is AuthUiState.Success -> { /* No error to clear */ }
        }
    }

    // ==================== Countdown Timer ====================

    private fun startResendCountdown() {
        cancelCountdown()
        countdownJob = viewModelScope.launch {
            var countdown = OTP_RESEND_COUNTDOWN_SECONDS
            while (countdown > 0) {
                delay(1000)
                countdown--
                val currentState = _uiState.value
                if (currentState is AuthUiState.OtpVerification) {
                    _uiState.update { currentState.copy(resendCountdown = countdown) }
                } else {
                    break
                }
            }
        }
    }

    private fun cancelCountdown() {
        countdownJob?.cancel()
        countdownJob = null
    }

    // ==================== Error Mapping ====================

    /**
     * Maps application errors to Persian user-friendly messages.
     * Requirements: 10.1-10.6
     */
    private fun mapErrorToMessage(error: AppError): String {
        return when (error) {
            is AppError.NetworkError -> "خطا در اتصال به سرور"
            is AuthError.InvalidCredentials -> "شماره تلفن یا رمز عبور اشتباه است"
            is AuthError.InvalidOtp -> "کد تأیید اشتباه است"
            is AuthError.OtpExpired -> "کد تأیید منقضی شده است"
            is AuthError.PhoneAlreadyExists -> "این شماره قبلاً ثبت شده است"
            is AuthError.PhoneNotFound -> "کاربری با این شماره یافت نشد"
            is AuthError.RateLimited -> "لطفاً ${error.retryAfterSeconds / 60} دقیقه دیگر تلاش کنید"
            is AuthError.WeakPassword -> "رمز عبور باید حداقل ۸ کاراکتر با حروف بزرگ، کوچک و عدد باشد"
            is AuthError.PasswordMismatch -> "رمز عبور و تکرار آن یکسان نیستند"
            is AuthError.InvalidPhone -> "شماره تلفن نامعتبر است"
            is AppError.ServerError -> error.message
            is AppError.UnknownError -> "خطای ناشناخته"
            else -> error.message
        }
    }

    override fun onCleared() {
        super.onCleared()
        cancelCountdown()
    }
}
