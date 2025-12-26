package com.voxcina.shop.presentation.onboarding

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

/**
 * Represents a single onboarding page content.
 *
 * Each page displays an image with overlay text, followed by a title and description.
 * All text content is in Persian to match the app's RTL design.
 *
 * @property imageResId Drawable resource ID for the page image/placeholder
 * @property overlayTitleResId String resource ID for text displayed on the image overlay (e.g., "کالکشن جدید")
 * @property overlaySubtitleResId String resource ID for subtitle on the image overlay (e.g., "تخفیف‌های ویژه فصل")
 * @property titleLine1ResId String resource ID for first line of title (full opacity)
 * @property titleLine2ResId String resource ID for second line of title (70% opacity)
 * @property descriptionResId String resource ID for description text displayed below the title
 */
data class OnboardingPage(
    @DrawableRes val imageResId: Int,
    @StringRes val overlayTitleResId: Int,
    @StringRes val overlaySubtitleResId: Int,
    @StringRes val titleLine1ResId: Int,
    @StringRes val titleLine2ResId: Int,
    @StringRes val descriptionResId: Int
)
