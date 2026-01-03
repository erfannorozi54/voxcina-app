package com.voxcina.shop.presentation.support

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.voxcina.shop.domain.repository.FaqRepository
import com.voxcina.shop.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SupportViewModel @Inject constructor(
    private val faqRepository: FaqRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<SupportUiState>(SupportUiState.Loading)
    val uiState: StateFlow<SupportUiState> = _uiState.asStateFlow()
    
    init {
        loadFaqs()
    }
    
    fun onEvent(event: SupportEvent) {
        when (event) {
            is SupportEvent.SearchQueryChanged -> updateSearchQuery(event.query)
            is SupportEvent.FaqClicked -> toggleFaqExpansion(event.faqId)
            is SupportEvent.Retry -> loadFaqs()
        }
    }
    
    private fun loadFaqs() {
        viewModelScope.launch {
            _uiState.value = SupportUiState.Loading
            when (val result = faqRepository.getFaqs()) {
                is Result.Success -> {
                    _uiState.value = SupportUiState.Success(faqs = result.data)
                }
                is Result.Error -> {
                    _uiState.value = SupportUiState.Error(result.error.message)
                }
            }
        }
    }
    
    private fun updateSearchQuery(query: String) {
        val currentState = _uiState.value
        if (currentState is SupportUiState.Success) {
            _uiState.update {
                currentState.copy(searchQuery = query, expandedFaqId = null)
            }
        }
    }
    
    private fun toggleFaqExpansion(faqId: String) {
        val currentState = _uiState.value
        if (currentState is SupportUiState.Success) {
            _uiState.update {
                currentState.copy(
                    expandedFaqId = if (currentState.expandedFaqId == faqId) null else faqId
                )
            }
        }
    }
}
