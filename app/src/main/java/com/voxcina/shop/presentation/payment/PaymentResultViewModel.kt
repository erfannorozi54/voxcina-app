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

    fun verifyPayment(trackId: Long, orderId: String) {
        viewModelScope.launch {
            _uiState.value = PaymentResultUiState.Loading
            
            when (val result = paymentRepository.verifyPayment(trackId)) {
                is Result.Success -> {
                    val response = result.data
                    when {
                        response.isSuccess -> {
                            _uiState.value = PaymentResultUiState.Success(
                                orderNumber = response.orderNumber,
                                refNumber = response.refNumber
                            )
                        }
                        response.isAbandoned -> {
                            _uiState.value = PaymentResultUiState.Abandoned(
                                orderNumber = response.orderNumber,
                                orderId = response.orderId,
                                message = response.statusText
                            )
                        }
                        else -> {
                            _uiState.value = PaymentResultUiState.Failed(
                                orderNumber = response.orderNumber,
                                orderId = response.orderId,
                                message = response.statusText.ifEmpty { "پرداخت تایید نشد" },
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

    fun retryPayment(orderId: String) {
        viewModelScope.launch {
            _isRetrying.value = true
            
            when (val result = paymentRepository.retryPayment(orderId)) {
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
