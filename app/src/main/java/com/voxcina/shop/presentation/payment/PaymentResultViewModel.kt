package com.voxcina.shop.presentation.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.voxcina.shop.domain.repository.PaymentRepository
import com.voxcina.shop.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class PaymentResultUiState {
    data object Loading : PaymentResultUiState()
    data class Success(val orderNumber: String?) : PaymentResultUiState()
    data class Failed(val orderNumber: String?, val message: String?) : PaymentResultUiState()
    data class Error(val message: String) : PaymentResultUiState()
}

@HiltViewModel
class PaymentResultViewModel @Inject constructor(
    private val paymentRepository: PaymentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<PaymentResultUiState>(PaymentResultUiState.Loading)
    val uiState: StateFlow<PaymentResultUiState> = _uiState.asStateFlow()

    fun verifyPayment(trackId: Long, orderId: String) {
        viewModelScope.launch {
            _uiState.value = PaymentResultUiState.Loading
            
            when (val result = paymentRepository.verifyPayment(trackId)) {
                is Result.Success -> {
                    val response = result.data
                    if (response.paymentStatus == "paid") {
                        // Extract order number from description (e.g., "Order DGS-10032 - ...")
                        val orderNumber = response.description
                            ?.substringAfter("Order ")
                            ?.substringBefore(" -")
                            ?.takeIf { it.startsWith("DGS-") }
                        _uiState.value = PaymentResultUiState.Success(orderNumber = orderNumber)
                    } else {
                        _uiState.value = PaymentResultUiState.Failed(
                            orderNumber = null,
                            message = response.statusText.ifEmpty { "پرداخت تایید نشد" }
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.value = PaymentResultUiState.Error("خطا در بررسی وضعیت پرداخت")
                }
            }
        }
    }

    fun setSuccessState(orderNumber: String?) {
        _uiState.value = PaymentResultUiState.Success(orderNumber)
    }

    fun setFailedState(message: String?) {
        _uiState.value = PaymentResultUiState.Failed(null, message)
    }
}
