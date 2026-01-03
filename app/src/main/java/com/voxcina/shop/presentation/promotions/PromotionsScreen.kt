package com.voxcina.shop.presentation.promotions

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.voxcina.shop.domain.model.Promotion
import com.voxcina.shop.ui.components.EmptyState
import com.voxcina.shop.ui.components.ScreenHeader
import com.voxcina.shop.ui.components.SoftShadowCard
import com.voxcina.shop.ui.components.VoxcinaLoading
import com.voxcina.shop.ui.components.VoxcinaPrimaryButton
import com.voxcina.shop.ui.theme.Destructive
import com.voxcina.shop.ui.theme.Primary
import com.voxcina.shop.ui.theme.SecondaryLight
import com.voxcina.shop.ui.theme.Success

@Composable
fun PromotionsScreen(
    onNavigateBack: () -> Unit,
    viewModel: PromotionsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            topBar = {
                ScreenHeader(
                    title = "کدهای تخفیف من",
                    onBackClick = onNavigateBack
                )
            },
            containerColor = SecondaryLight
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                when {
                    uiState.isLoading -> {
                        VoxcinaLoading(modifier = Modifier.align(Alignment.Center))
                    }
                    uiState.error != null -> {
                        ErrorContent(
                            message = uiState.error!!,
                            onRetry = { viewModel.loadPromotions() },
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    uiState.promotions.isEmpty() -> {
                        EmptyState(
                            icon = Icons.Outlined.LocalOffer,
                            title = "کد تخفیفی ندارید",
                            subtitle = "در حال حاضر کد تخفیف فعالی برای شما وجود ندارد"
                        )
                    }
                    else -> {
                        PromotionsList(
                            promotions = uiState.promotions,
                            onCopyCode = { code -> copyToClipboard(context, code) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PromotionsList(
    promotions: List<Promotion>,
    onCopyCode: (String) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(promotions, key = { it.id }) { promotion ->
            PromotionCard(
                promotion = promotion,
                onCopyCode = { onCopyCode(promotion.code) }
            )
        }
    }
}

@Composable
private fun PromotionCard(
    promotion: Promotion,
    onCopyCode: () -> Unit
) {
    SoftShadowCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 12.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(
                                if (promotion.isPercentage) Success.copy(alpha = 0.1f)
                                else Primary.copy(alpha = 0.1f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = promotion.getDisplayValue(),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (promotion.isPercentage) Success else Primary
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (promotion.isPercentage) "تخفیف درصدی" else "تخفیف مبلغی",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Primary
                        )
                        if (promotion.minOrderAmount > 0) {
                            Text(
                                text = "حداقل سفارش: ${promotion.minOrderAmount.toLong()} تومان",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Code display with copy button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(SecondaryLight)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = promotion.code,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Primary,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = onCopyCode,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.ContentCopy,
                        contentDescription = "کپی کد",
                        tint = Primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Usage info
            promotion.maxUses?.let { maxUses ->
                if (maxUses > 0) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "استفاده شده: ${promotion.usedCount} از $maxUses",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = Destructive,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        VoxcinaPrimaryButton(
            text = "تلاش مجدد",
            onClick = onRetry,
            modifier = Modifier.width(160.dp)
        )
    }
}

private fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("کد تخفیف", text)
    clipboard.setPrimaryClip(clip)
    Toast.makeText(context, "کد تخفیف کپی شد", Toast.LENGTH_SHORT).show()
}
