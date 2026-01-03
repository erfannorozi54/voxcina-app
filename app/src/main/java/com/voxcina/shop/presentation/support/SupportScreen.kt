package com.voxcina.shop.presentation.support

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.voxcina.shop.domain.model.Faq
import com.voxcina.shop.presentation.support.components.ExpandableFaqItem
import com.voxcina.shop.ui.components.EmptyState
import com.voxcina.shop.ui.components.ScreenHeader
import com.voxcina.shop.ui.components.SearchTextField
import com.voxcina.shop.ui.components.VoxcinaLoading
import com.voxcina.shop.ui.theme.Destructive
import com.voxcina.shop.ui.theme.Primary
import com.voxcina.shop.ui.theme.Primary100
import com.voxcina.shop.ui.theme.SecondaryLight
import com.voxcina.shop.ui.theme.VoxcinaTheme

@Composable
fun SupportScreen(
    onNavigateBack: () -> Unit,
    viewModel: SupportViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    SupportScreenContent(
        uiState = uiState,
        onBackClick = onNavigateBack,
        onSearchQueryChanged = { viewModel.onEvent(SupportEvent.SearchQueryChanged(it)) },
        onFaqClicked = { viewModel.onEvent(SupportEvent.FaqClicked(it)) },
        onRetry = { viewModel.onEvent(SupportEvent.Retry) }
    )
}

@Composable
private fun SupportScreenContent(
    uiState: SupportUiState,
    onBackClick: () -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onFaqClicked: (String) -> Unit,
    onRetry: () -> Unit
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            topBar = {
                ScreenHeader(
                    title = "پشتیبانی و سوالات متداول",
                    onBackClick = onBackClick
                )
            },
            containerColor = SecondaryLight
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when (uiState) {
                    is SupportUiState.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            VoxcinaLoading(
                                size = 100.dp,
                                loadingText = "در حال بارگذاری..."
                            )
                        }
                    }
                    is SupportUiState.Success -> {
                        SupportSuccessContent(
                            state = uiState,
                            onSearchQueryChanged = onSearchQueryChanged,
                            onFaqClicked = onFaqClicked
                        )
                    }
                    is SupportUiState.Error -> {
                        EmptyState(
                            icon = Icons.Default.Error,
                            title = "خطا در بارگذاری",
                            subtitle = uiState.message,
                            actionButtonText = "تلاش مجدد",
                            onActionClick = onRetry,
                            iconTint = Destructive.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SupportSuccessContent(
    state: SupportUiState.Success,
    onSearchQueryChanged: (String) -> Unit,
    onFaqClicked: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header section
        item {
            SupportHeader()
        }
        
        // Search field
        item {
            SearchTextField(
                value = state.searchQuery,
                onValueChange = onSearchQueryChanged,
                placeholder = "جستجو در سوالات..."
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        
        // FAQ items or empty state
        if (state.filteredFaqs.isEmpty()) {
            item {
                NoResultsState(hasSearchQuery = state.searchQuery.isNotBlank())
            }
        } else {
            itemsIndexed(
                items = state.filteredFaqs,
                key = { _, faq -> faq.id }
            ) { _, faq ->
                ExpandableFaqItem(
                    faq = faq,
                    isExpanded = state.expandedFaqId == faq.id,
                    onClick = { onFaqClicked(faq.id) }
                )
            }
        }
        
        // Bottom spacing
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SupportHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(Primary100, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                contentDescription = null,
                tint = Primary,
                modifier = Modifier.size(28.dp)
            )
        }
        Column(
            modifier = Modifier.padding(start = 12.dp)
        ) {
            Text(
                text = "FAQ",
                style = MaterialTheme.typography.labelSmall,
                color = Primary.copy(alpha = 0.7f),
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "پرسشهای متداول",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Primary
            )
        }
    }
    Text(
        text = "در این بخش به رایجترین سوالات شما درباره روند ثبت سفارش، ارسال، پیگیری و خدمات پس از فروش پاسخ دادهایم.",
        style = MaterialTheme.typography.bodyMedium,
        color = Color.Gray,
        modifier = Modifier.padding(bottom = 16.dp)
    )
}

@Composable
private fun NoResultsState(hasSearchQuery: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Outlined.Search,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = Color.Gray.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = if (hasSearchQuery) "نتیجهای یافت نشد" else "سوالی ثبت نشده است",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Primary
        )
        Text(
            text = if (hasSearchQuery) "عبارت دیگری را جستجو کنید" else "فعلاً سوال متداولی ثبت نشده است",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
    }
}

// ============== Previews ==============

@Preview(showBackground = true)
@Composable
private fun SupportScreenLoadingPreview() {
    VoxcinaTheme {
        SupportScreenContent(
            uiState = SupportUiState.Loading,
            onBackClick = {},
            onSearchQueryChanged = {},
            onFaqClicked = {},
            onRetry = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SupportScreenSuccessPreview() {
    VoxcinaTheme {
        SupportScreenContent(
            uiState = SupportUiState.Success(
                faqs = listOf(
                    Faq("1", "چگونه سفارش خود را پیگیری کنم؟", "برای پیگیری سفارش خود میتوانید از بخش سفارشهای من در پروفایل کاربری استفاده کنید."),
                    Faq("2", "هزینه ارسال چقدر است؟", "هزینه ارسال بسته به شهر مقصد متفاوت است و در صفحه پرداخت نمایش داده میشود."),
                    Faq("3", "آیا امکان مرجوع کردن کالا وجود دارد؟", "بله، تا ۷ روز پس از دریافت کالا امکان مرجوعی وجود دارد.")
                ),
                expandedFaqId = "1"
            ),
            onBackClick = {},
            onSearchQueryChanged = {},
            onFaqClicked = {},
            onRetry = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SupportScreenEmptyPreview() {
    VoxcinaTheme {
        SupportScreenContent(
            uiState = SupportUiState.Success(faqs = emptyList()),
            onBackClick = {},
            onSearchQueryChanged = {},
            onFaqClicked = {},
            onRetry = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SupportScreenErrorPreview() {
    VoxcinaTheme {
        SupportScreenContent(
            uiState = SupportUiState.Error("خطا در اتصال به سرور"),
            onBackClick = {},
            onSearchQueryChanged = {},
            onFaqClicked = {},
            onRetry = {}
        )
    }
}
