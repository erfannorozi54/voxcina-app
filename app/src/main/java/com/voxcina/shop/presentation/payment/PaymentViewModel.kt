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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val paymentRepository: PaymentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<PaymentUiState>(PaymentUiState.Idle)
    val uiState: StateFlow<PaymentUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<PaymentNavigationEvent>()
    val navigationEvent: SharedFlow<PaymentNavigationEvent> = _navigationEvent.asSharedFlow()

    fun requestPayment(orderId: String) {
        viewModelScope.launch {
            _uiState.update { PaymentUiState.Loading }

            val result = paymentRepository.requestPayment(
                orderId = orderId,
                gateway = "zibal"
            )

            when (result) {
                is Result.Success -> {
                    _uiState.update { PaymentUiState.Redirecting }
                    _navigationEvent.emit(
                        PaymentNavigationEvent.RedirectToPayment(result.data.payUrl)
                    )
                }
                is Result.Error -> {
                    _uiState.update { 
                        PaymentUiState.Error(result.error.message)
                    }
                }
            }
        }
    }

    fun verifyPayment(trackId: String) {
        viewModelScope.launch {
            _uiState.update { PaymentUiState.Verifying }

            val result = paymentRepository.verifyPayment(trackId, "zibal")

            when (result) {
                is Result.Success -> {
                    if (result.data.isSuccess) {
                        _uiState.update { 
                            PaymentUiState.Success(result.data.refNumber)
                        }
                        _navigationEvent.emit(PaymentNavigationEvent.PaymentSuccess)
                    } else if (result.data.isAbandoned) {
                        _uiState.update { 
                            PaymentUiState.Error(result.data.statusText ?: "پرداخت ناتمام ماند")
                        }
                        _navigationEvent.emit(PaymentNavigationEvent.PaymentFailed)
                    } else {
                        _uiState.update { 
                            PaymentUiState.Error(result.data.statusText ?: "پرداخت تایید نشد")
                        }
                        _navigationEvent.emit(PaymentNavigationEvent.PaymentFailed)
                    }
                }
                is Result.Error -> {
                    _uiState.update { 
                        PaymentUiState.Error(result.error.message)
                    }
                    _navigationEvent.emit(PaymentNavigationEvent.PaymentFailed)
                }
            }
        }
    }

    fun verifyPaymentByOrderId(orderId: String, isSuccess: Boolean) {
        viewModelScope.launch {
            if (isSuccess) {
                _uiState.update { PaymentUiState.Success(null) }
                _navigationEvent.emit(PaymentNavigationEvent.PaymentSuccess)
            } else {
                _uiState.update { 
                    PaymentUiState.Error("پرداخت ناموفق بود")
                }
                _navigationEvent.emit(PaymentNavigationEvent.PaymentFailed)
            }
        }
    }
}
