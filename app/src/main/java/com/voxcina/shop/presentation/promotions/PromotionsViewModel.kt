package com.voxcina.shop.presentation.promotions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.voxcina.shop.domain.model.Promotion
import com.voxcina.shop.domain.repository.PromotionRepository
import com.voxcina.shop.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PromotionsUiState(
    val isLoading: Boolean = true,
    val promotions: List<Promotion> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class PromotionsViewModel @Inject constructor(
    private val promotionRepository: PromotionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PromotionsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadPromotions()
    }

    fun loadPromotions() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = promotionRepository.getUserPromotions()) {
                is Result.Success -> _uiState.update {
                    it.copy(isLoading = false, promotions = result.data)
                }
                is Result.Error -> _uiState.update {
                    it.copy(isLoading = false, error = result.error.message)
                }
            }
        }
    }
}
