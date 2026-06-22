package com.voxcina.shop.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.voxcina.shop.R
import com.voxcina.shop.ui.theme.Primary
import com.voxcina.shop.ui.theme.Primary100
import com.voxcina.shop.ui.theme.PrimaryDark
import com.voxcina.shop.ui.theme.Secondary
import com.voxcina.shop.ui.theme.VoxcinaTheme
import kotlin.math.cos
import kotlin.math.sin

/**
 * Fashion-themed loading component with animated hanger and floating fabric elements.
 * Features the Voxcina logo with creative fashion-related animations.
 *
 * @param modifier Modifier for the loading container
 * @param size Size of the loading component
 * @param showText Whether to show loading text
 * @param loadingText Custom loading text (Persian)
 */
@Composable
fun VoxcinaLoading(
    modifier: Modifier = Modifier,
    size: Dp = 120.dp,
    showText: Boolean = true,
    loadingText: String = "در حال بارگذاری..."
) {
    // Animation states
    val infiniteTransition = rememberInfiniteTransition(label = "loading")
    
    // Logo pulse animation
    val logoScale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logoScale"
    )
    
    // Hanger swing animation
    val hangerRotation by infiniteTransition.animateFloat(
        initialValue = -15f,
        targetValue = 15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "hangerSwing"
    )
    
    // Fabric wave animation
    val fabricWave by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "fabricWave"
    )
    
    // Shimmer animation for sparkles
    val shimmerAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = EaseInOutQuad),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmer"
    )
    
    // Dots animation for text
    val dotsAlpha by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dots"
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier.size(size),
            contentAlignment = Alignment.Center
        ) {
            // Outer rotating fabric ring
            FabricRing(
                modifier = Modifier.size(size),
                rotation = fabricWave
            )
            
            // Animated hanger with swing
            AnimatedHanger(
                modifier = Modifier
                    .size(size * 0.7f)
                    .rotate(hangerRotation),
                shimmerAlpha = shimmerAlpha
            )
            
            // Center logo with pulse
            Box(
                modifier = Modifier
                    .size(size * 0.4f)
                    .scale(logoScale)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Primary,
                                PrimaryDark
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.raw.white_icon),
                    contentDescription = "Voxcina Logo",
                    modifier = Modifier
                        .size(size * 0.25f)
                        .padding(4.dp)
                )
            }
            
            // Floating sparkles
            FloatingSparkles(
                modifier = Modifier.size(size),
                alpha = shimmerAlpha
            )
        }
        
        if (showText) {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = loadingText,
                    color = Primary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

/**
 * Animated fabric ring that rotates around the loading indicator
 */
@Composable
private fun FabricRing(
    modifier: Modifier = Modifier,
    rotation: Float
) {
    Canvas(modifier = modifier) {
        val radius = size.minDimension / 2
        val strokeWidth = 3.dp.toPx()
        
        // Draw flowing fabric segments
        for (i in 0 until 4) {
            val startAngle = rotation + (i * 90f)
            val sweepAngle = 60f
            
            rotate(startAngle) {
                drawArc(
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            Primary.copy(alpha = 0.1f),
                            Primary.copy(alpha = 0.6f),
                            Primary.copy(alpha = 0.1f)
                        )
                    ),
                    startAngle = 0f,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    style = Stroke(
                        width = strokeWidth,
                        cap = StrokeCap.Round
                    ),
                    topLeft = Offset(strokeWidth, strokeWidth),
                    size = androidx.compose.ui.geometry.Size(
                        size.width - strokeWidth * 2,
                        size.height - strokeWidth * 2
                    )
                )
            }
        }
    }
}

/**
 * Animated clothes hanger shape
 */
@Composable
private fun AnimatedHanger(
    modifier: Modifier = Modifier,
    shimmerAlpha: Float
) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val strokeWidth = 2.5.dp.toPx()
        
        val hangerPath = Path().apply {
            // Hook at top
            moveTo(width * 0.5f, height * 0.15f)
            cubicTo(
                width * 0.55f, height * 0.05f,
                width * 0.6f, height * 0.05f,
                width * 0.58f, height * 0.12f
            )
            
            // Left shoulder
            moveTo(width * 0.5f, height * 0.2f)
            lineTo(width * 0.15f, height * 0.45f)
            
            // Right shoulder
            moveTo(width * 0.5f, height * 0.2f)
            lineTo(width * 0.85f, height * 0.45f)
        }
        
        drawPath(
            path = hangerPath,
            color = Primary.copy(alpha = shimmerAlpha * 0.8f),
            style = Stroke(
                width = strokeWidth,
                cap = StrokeCap.Round
            )
        )
    }
}

/**
 * Floating sparkle particles for fashion glamour effect
 */
@Composable
private fun FloatingSparkles(
    modifier: Modifier = Modifier,
    alpha: Float
) {
    val infiniteTransition = rememberInfiniteTransition(label = "sparkles")
    
    // Different phase offsets for each sparkle
    val sparklePositions = remember {
        listOf(
            Offset(0.2f, 0.3f),
            Offset(0.8f, 0.25f),
            Offset(0.15f, 0.7f),
            Offset(0.85f, 0.75f),
            Offset(0.5f, 0.85f)
        )
    }
    
    Canvas(modifier = modifier) {
        sparklePositions.forEachIndexed { index, position ->
            val sparkleAlpha = alpha * (0.5f + (index % 3) * 0.2f)
            val sparkleSize = 3.dp.toPx() + (index % 2) * 2.dp.toPx()
            
            // Draw diamond sparkle
            val centerX = size.width * position.x
            val centerY = size.height * position.y
            
            val sparklePath = Path().apply {
                moveTo(centerX, centerY - sparkleSize)
                lineTo(centerX + sparkleSize * 0.5f, centerY)
                lineTo(centerX, centerY + sparkleSize)
                lineTo(centerX - sparkleSize * 0.5f, centerY)
                close()
            }
            
            drawPath(
                path = sparklePath,
                color = Primary.copy(alpha = sparkleAlpha)
            )
        }
    }
}

/**
 * Compact loading indicator for inline use (buttons, list items)
 */
@Composable
fun VoxcinaLoadingCompact(
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    color: Color = Primary
) {
    val infiniteTransition = rememberInfiniteTransition(label = "compact_loading")
    
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Canvas(
        modifier = modifier
            .size(size)
            .rotate(rotation)
    ) {
        val strokeWidth = 2.dp.toPx()
        val radius = (size.toPx() - strokeWidth) / 2
        
        // Background circle
        drawCircle(
            color = color.copy(alpha = 0.2f),
            radius = radius,
            style = Stroke(width = strokeWidth)
        )
        
        // Animated arc
        drawArc(
            color = color,
            startAngle = 0f,
            sweepAngle = 270f,
            useCenter = false,
            style = Stroke(
                width = strokeWidth,
                cap = StrokeCap.Round
            ),
            topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
            size = androidx.compose.ui.geometry.Size(
                size.toPx() - strokeWidth,
                size.toPx() - strokeWidth
            )
        )
    }
}

/**
 * Full screen loading overlay with glassmorphism background
 */
@Composable
fun VoxcinaLoadingOverlay(
    modifier: Modifier = Modifier,
    loadingText: String = "در حال بارگذاری..."
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.3f)),
        contentAlignment = Alignment.Center
    ) {
        GlassCard(
            modifier = Modifier.padding(32.dp),
            backgroundAlpha = 0.9f,
            borderAlpha = 0.3f,
            cornerRadius = 24.dp
        ) {
            Box(
                modifier = Modifier.padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                VoxcinaLoading(
                    size = 100.dp,
                    loadingText = loadingText
                )
            }
        }
    }
}
