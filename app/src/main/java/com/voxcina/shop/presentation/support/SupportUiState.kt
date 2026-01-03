package com.voxcina.shop.presentation.support

import com.voxcina.shop.domain.model.Faq

sealed interface SupportUiState {
    data object Loading : SupportUiState
    
    data class Success(
        val faqs: List<Faq>,
        val searchQuery: String = "",
        val expandedFaqId: String? = null
    ) : SupportUiState {
        val filteredFaqs: List<Faq>
            get() = if (searchQuery.isBlank()) {
                faqs
            } else {
                faqs.filter {
                    it.question.contains(searchQuery, ignoreCase = true) ||
                    it.answer.contains(searchQuery, ignoreCase = true)
                }
            }
    }
    
    data class Error(val message: String) : SupportUiState
}

sealed interface SupportEvent {
    data class SearchQueryChanged(val query: String) : SupportEvent
    data class FaqClicked(val faqId: String) : SupportEvent
    data object Retry : SupportEvent
}
