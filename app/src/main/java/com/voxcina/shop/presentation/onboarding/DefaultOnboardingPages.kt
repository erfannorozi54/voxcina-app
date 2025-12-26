package com.voxcina.shop.presentation.onboarding

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.voxcina.shop.R

/**
 * Default onboarding pages content for the Voxcina app.
 *
 * Contains 3 pages introducing the app's key features:
 * 1. Product discovery and brand collection
 * 2. Smart AI-powered search
 * 3. Secure payment and fast delivery
 *
 * All text is in Persian to match the app's RTL design.
 */
val defaultOnboardingPages = listOf(
    OnboardingPage(
        imageResId = R.raw.onboarding_1,
        overlayTitleResId = R.string.onboarding_page1_overlay_title,
        overlaySubtitleResId = R.string.onboarding_page1_overlay_subtitle,
        titleLine1ResId = R.string.onboarding_page1_title_line1,
        titleLine2ResId = R.string.onboarding_page1_title_line2,
        descriptionResId = R.string.onboarding_page1_description
    ),
    OnboardingPage(
        imageResId = R.raw.onboarding_2,
        overlayTitleResId = R.string.onboarding_page2_overlay_title,
        overlaySubtitleResId = R.string.onboarding_page2_overlay_subtitle,
        titleLine1ResId = R.string.onboarding_page2_title_line1,
        titleLine2ResId = R.string.onboarding_page2_title_line2,
        descriptionResId = R.string.onboarding_page2_description
    ),
    OnboardingPage(
        imageResId = R.raw.onboarding_3,
        overlayTitleResId = R.string.onboarding_page3_overlay_title,
        overlaySubtitleResId = R.string.onboarding_page3_overlay_subtitle,
        titleLine1ResId = R.string.onboarding_page3_title_line1,
        titleLine2ResId = R.string.onboarding_page3_title_line2,
        descriptionResId = R.string.onboarding_page3_description
    )
)
