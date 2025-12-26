package com.voxcina.shop.presentation.onboarding.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.voxcina.shop.R
import com.voxcina.shop.presentation.onboarding.OnboardingPage
import com.voxcina.shop.ui.theme.Primary
import com.voxcina.shop.ui.theme.VoxcinaTheme

/**
 * Content composable for a single onboarding page.
 * Composes OnboardingImageCard with title and description text.
 *
 * @param page The OnboardingPage data to display
 * @param modifier Modifier for the content container
 */
@Composable
fun OnboardingPageContent(
    page: OnboardingPage,
    modifier: Modifier = Modifier
) {
    val titleLine1 = stringResource(page.titleLine1ResId)
    val titleLine2 = stringResource(page.titleLine2ResId)
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Image card with overlay
        OnboardingImageCard(
            imageResId = page.imageResId,
            overlayTitle = stringResource(page.overlayTitleResId),
            overlaySubtitle = stringResource(page.overlaySubtitleResId),
            modifier = Modifier
                .fillMaxWidth(0.85f)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Title text with gradient effect - first line full color, second line 70% opacity
        Text(
            text = buildAnnotatedString {
                // First line - full primary color
                withStyle(
                    style = SpanStyle(
                        color = Primary,
                        fontWeight = FontWeight.ExtraBold
                    )
                ) {
                    append(titleLine1)
                }
                append(" ")
                // Second line - 70% opacity primary color
                withStyle(
                    style = SpanStyle(
                        color = Primary.copy(alpha = 0.7f),
                        fontWeight = FontWeight.ExtraBold
                    )
                ) {
                    append(titleLine2)
                }
            },
            fontSize = 28.sp,
            textAlign = TextAlign.Center,
            lineHeight = 36.sp,
            letterSpacing = (-0.5).sp
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Description text - Body typography (14sp), slate gray color
        Text(
            text = stringResource(page.descriptionResId),
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            color = Color(0xFF64748B), // slate-500
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFCFAF8)
@Composable
private fun OnboardingPageContentPreview() {
    VoxcinaTheme {
        OnboardingPageContent(
            page = OnboardingPage(
                imageResId = R.raw.onboarding_1,
                overlayTitleResId = R.string.onboarding_page1_overlay_title,
                overlaySubtitleResId = R.string.onboarding_page1_overlay_subtitle,
                titleLine1ResId = R.string.onboarding_page1_title_line1,
                titleLine2ResId = R.string.onboarding_page1_title_line2,
                descriptionResId = R.string.onboarding_page1_description
            ),
            modifier = Modifier.padding(vertical = 16.dp)
        )
    }
}
