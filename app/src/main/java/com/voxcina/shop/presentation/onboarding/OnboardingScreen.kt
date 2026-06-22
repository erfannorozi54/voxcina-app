package com.voxcina.shop.presentation.onboarding

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.voxcina.shop.R
import com.voxcina.shop.presentation.onboarding.components.OnboardingPageContent
import com.voxcina.shop.presentation.onboarding.components.PageIndicator
import com.voxcina.shop.ui.theme.Primary
import com.voxcina.shop.ui.theme.SecondaryLight
import com.voxcina.shop.ui.theme.VoxcinaTheme
import com.voxcina.shop.util.PersianDigitConverter
import kotlinx.coroutines.launch

/**
 * Main onboarding screen composable.
 * Displays a series of swipeable pages introducing the app's key features.
 *
 * @param onOnboardingComplete Callback invoked when user completes or skips onboarding
 * @param appVersion App version string to display at the bottom
 * @param modifier Modifier for the screen container
 */
@Composable
fun OnboardingScreen(
    onOnboardingComplete: () -> Unit,
    appVersion: String,
    modifier: Modifier = Modifier
) {
    val pages = defaultOnboardingPages
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { pages.size }
    )
    val coroutineScope = rememberCoroutineScope()
    
    // Check if on last page
    val isLastPage = pagerState.currentPage == pages.size - 1
    
    // Animated button colors
    val buttonBackgroundColor by animateColorAsState(
        targetValue = if (isLastPage) Primary else Color(0xFFE5E7EB), // gray-200
        animationSpec = tween(durationMillis = 300),
        label = "buttonBackground"
    )
    
    val buttonTextColor by animateColorAsState(
        targetValue = if (isLastPage) Color.White else Color(0xFF374151), // gray-700
        animationSpec = tween(durationMillis = 300),
        label = "buttonText"
    )

    // RTL layout with secondary light background
    androidx.compose.runtime.CompositionLocalProvider(
        androidx.compose.ui.platform.LocalLayoutDirection provides LayoutDirection.Rtl
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(SecondaryLight),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top bar with skip button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                // Skip button - positioned at top-end (visually top-left in RTL)
                TextButton(
                    onClick = onOnboardingComplete
                ) {
                    Text(
                        text = stringResource(R.string.onboarding_skip),
                        color = Primary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // HorizontalPager for pages
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                beyondViewportPageCount = 1
            ) { pageIndex ->
                OnboardingPageContent(
                    page = pages[pageIndex],
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Page indicator
            PageIndicator(
                pageCount = pages.size,
                currentPage = pagerState.currentPage,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Action button - changes based on current page
            Button(
                onClick = {
                    if (isLastPage) {
                        onOnboardingComplete()
                    } else {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(
                                page = pagerState.currentPage + 1,
                                animationSpec = tween(durationMillis = 400)
                            )
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp), // Full rounded (pill shape)
                colors = ButtonDefaults.buttonColors(
                    containerColor = buttonBackgroundColor,
                    contentColor = buttonTextColor
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(
                            if (isLastPage) R.string.onboarding_enter_store
                            else R.string.onboarding_next_page
                        ),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    // Left arrow icon (points left in RTL for "forward" direction)
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Version text
            Text(
                text = PersianDigitConverter.formatVersionPersian(appVersion),
                fontSize = 12.sp,
                color = Color(0xFF9CA3AF),
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
    }
}
