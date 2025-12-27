package com.voxcina.shop.presentation.onboarding.components

import android.graphics.BitmapFactory
import androidx.annotation.RawRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.voxcina.shop.R
import com.voxcina.shop.ui.theme.Primary
import com.voxcina.shop.ui.theme.VoxcinaTheme

/**
 * Image card composable for onboarding pages.
 * Displays an image with rounded corners and a glassmorphism overlay
 * at the bottom showing title, subtitle, and a shopping bag icon.
 *
 * @param imageResId Raw resource ID for the webp image
 * @param overlayTitle Title text displayed on the overlay (e.g., "کالکشن جدید")
 * @param overlaySubtitle Subtitle text displayed on the overlay
 * @param modifier Modifier for the card
 */
@Composable
fun OnboardingImageCard(
    @RawRes imageResId: Int,
    overlayTitle: String,
    overlaySubtitle: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val bitmap = remember(imageResId) {
        context.resources.openRawResource(imageResId).use { inputStream ->
            BitmapFactory.decodeStream(inputStream)
        }
    }
    
    Card(
        modifier = modifier
            .aspectRatio(4f / 5f),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Display the webp image from raw resources
            bitmap?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(24.dp))
                )
            }

            // Glassmorphism overlay card at bottom
            GlassOverlayCard(
                title = overlayTitle,
                subtitle = overlaySubtitle,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            )
        }
    }
}

/**
 * Glassmorphism style overlay card with frosted glass effect.
 * Contains a shopping bag icon, title, and subtitle.
 * Follows Android standard glassmorphism design with proper layering.
 */
@Composable
private fun GlassOverlayCard(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
    ) {
        // Blurred background layer for glass effect
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    color = Color.White.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(16.dp)
                )
                .border(
                    width = 1.dp,
                    color = Color.White.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(16.dp)
                )
                .blur(
                    radius = 16.dp,
                    edgeTreatment = BlurredEdgeTreatment.Unbounded
                )
        )
        
        // Content layer
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Color.White.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(16.dp)
                )
                .border(
                    width = 1.dp,
                    color = Color.White.copy(alpha = 0.25f),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Text content (right side in RTL)
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    color = Primary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    color = Primary.copy(alpha = 0.8f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Shopping bag icon in circular container (left side in RTL)
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = Color.White,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.ShoppingCart,
                    contentDescription = null,
                    tint = Primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun OnboardingImageCardPreview() {
    VoxcinaTheme {
        OnboardingImageCard(
            imageResId = R.raw.onboarding_1,
            overlayTitle = "کالکشن جدید",
            overlaySubtitle = "تخفیف‌های ویژه فصل",
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1A3C69)
@Composable
private fun GlassOverlayCardPreview() {
    VoxcinaTheme {
        GlassOverlayCard(
            title = "کالکشن جدید",
            subtitle = "تخفیف‌های ویژه فصل",
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        )
    }
}
