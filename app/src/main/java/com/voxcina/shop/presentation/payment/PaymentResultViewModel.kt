package com.voxcina.shop.presentation.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.voxcina.shop.domain.repository.PaymentRepository
import com.voxcina.shop.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class PaymentResultUiState {
    data object Loading : PaymentResultUiState()
    data class Success(val orderNumber: String?, val refNumber: String? = null) : PaymentResultUiState()
    data class Abandoned(val orderNumber: String?, val orderId: String?, val message: String) : PaymentResultUiState()
    data class Failed(val orderNumber: String?, val orderId: String?, val message: String?, val canRetry: Boolean) : PaymentResultUiState()
    data class Error(val message: String) : PaymentResultUiState()
}

sealed class PaymentResultEvent {
    data class RetryPayment(val payUrl: String) : PaymentResultEvent()
}

@HiltViewModel
class PaymentResultViewModel @Inject constructor(
    private val paymentRepository: PaymentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<PaymentResultUiState>(PaymentResultUiState.Loading)
    val uiState: StateFlow<PaymentResultUiState> = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<PaymentResultEvent>()
    val event: SharedFlow<PaymentResultEvent> = _event.asSharedFlow()

    private val _isRetrying = MutableStateFlow(false)
    val isRetrying: StateFlow<Boolean> = _isRetrying.asStateFlow()

    /**
     * Set success state directly when we know payment succeeded
     */
    fun setSuccessState(orderNumber: String?) {
        _uiState.value = PaymentResultUiState.Success(orderNumber = orderNumber, refNumber = null)
    }

    /**
     * Set abandoned state
     */
    fun setAbandonedState(orderId: String) {
        _uiState.value = PaymentResultUiState.Abandoned(
            orderNumber = null,
            orderId = orderId,
            message = "پرداخت تکمیل نشد"
        )
    }

    fun verifyPayment(trackId: String, orderId: String, gateway: String) {
        viewModelScope.launch {
            _uiState.value = PaymentResultUiState.Loading
            
            when (val result = paymentRepository.verifyPayment(trackId, gateway)) {
                is Result.Success -> {
                    val response = result.data
                    when {
                        response.isSuccess -> {
                            _uiState.value = PaymentResultUiState.Success(
                                orderNumber = response.orderId, // Using orderId as number if needed
                                refNumber = response.refNumber
                            )
                        }
                        response.isAbandoned -> {
                            _uiState.value = PaymentResultUiState.Abandoned(
                                orderNumber = null,
                                orderId = response.orderId ?: orderId,
                                message = response.statusText ?: "پرداخت ناتمام"
                            )
                        }
                        else -> {
                            _uiState.value = PaymentResultUiState.Failed(
                                orderNumber = null,
                                orderId = response.orderId ?: orderId,
                                message = response.statusText ?: "پرداخت تایید نشد",
                                canRetry = response.canRetry
                            )
                        }
                    }
                }
                is Result.Error -> {
                    _uiState.value = PaymentResultUiState.Error("خطا در بررسی وضعیت پرداخت")
                }
            }
        }
    }

    fun retryPayment(orderId: String, gateway: String) {
        viewModelScope.launch {
            _isRetrying.value = true
            
            when (val result = paymentRepository.retryPayment(orderId, gateway)) {
                is Result.Success -> {
                    _event.emit(PaymentResultEvent.RetryPayment(result.data.payUrl))
                }
                is Result.Error -> {
                    _uiState.value = PaymentResultUiState.Error(
                        result.error.message.ifEmpty { "خطا در ایجاد درخواست پرداخت مجدد" }
                    )
                }
            }
            
            _isRetrying.value = false
        }
    }
}
