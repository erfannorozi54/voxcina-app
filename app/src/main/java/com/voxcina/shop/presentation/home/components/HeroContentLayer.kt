package com.voxcina.shop.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.voxcina.shop.domain.model.HeroBadgeStyle
import com.voxcina.shop.domain.model.HeroButtonStyle
import com.voxcina.shop.domain.model.HeroColorStyle
import com.voxcina.shop.domain.model.HeroContent
import com.voxcina.shop.domain.model.HeroElement
import com.voxcina.shop.domain.model.HeroElementType
import com.voxcina.shop.domain.model.HeroOverlayStyle

// ---------------------------------------------------------------------------
// Color / gradient helpers (inline styles — purge-proof equivalent)
// ---------------------------------------------------------------------------

/** Parses `#rgb`, `#rrggbb` or `#rrggbbaa` into a Compose [Color]. */
fun parseHexColor(hex: String, fallback: Color = Color.White): Color {
    val value = hex.trim().removePrefix("#")
    if (value.isEmpty()) return fallback
    return when (value.length) {
        3 -> {
            val r = value[0].digitToIntOrNull(16) ?: return fallback
            val g = value[1].digitToIntOrNull(16) ?: return fallback
            val b = value[2].digitToIntOrNull(16) ?: return fallback
            Color(r * 17, g * 17, b * 17)
        }
        6, 8 -> {
            val r = value.substring(0, 2).toIntOrNull(16) ?: return fallback
            val g = value.substring(2, 4).toIntOrNull(16) ?: return fallback
            val b = value.substring(4, 6).toIntOrNull(16) ?: return fallback
            val a = if (value.length == 8) value.substring(6, 8).toIntOrNull(16) ?: 255 else 255
            Color(r, g, b, a)
        }
        else -> fallback
    }
}

/** Applies a 0–100 opacity to a color, matching the admin-authored alpha. */
fun colorWithAlpha(color: Color, opacity: Int): Color {
    val alpha = opacity.coerceIn(0, 100) / 100f
    return color.copy(alpha = color.alpha * alpha)
}

private fun gradientStartEnd(direction: String): Pair<Offset, Offset> = when (direction) {
    "to-r" -> Offset(0f, 0f) to Offset(Float.POSITIVE_INFINITY, 0f)
    "to-l" -> Offset(Float.POSITIVE_INFINITY, 0f) to Offset(0f, 0f)
    "to-b" -> Offset(0f, 0f) to Offset(0f, Float.POSITIVE_INFINITY)
    "to-t" -> Offset(0f, Float.POSITIVE_INFINITY) to Offset(0f, 0f)
    "to-br" -> Offset(0f, 0f) to Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    "to-bl" -> Offset(Float.POSITIVE_INFINITY, 0f) to Offset(0f, Float.POSITIVE_INFINITY)
    "to-tr" -> Offset(0f, Float.POSITIVE_INFINITY) to Offset(Float.POSITIVE_INFINITY, 0f)
    "to-tl" -> Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY) to Offset(0f, 0f)
    else -> Offset(0f, 0f) to Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
}

/** Builds a linear gradient, skipping an empty middle stop (matches the web). */
fun heroGradientBrush(from: String, via: String, to: String, direction: String): Brush {
    val colors = listOfNotNull(
        parseHexColor(from),
        via.takeIf { it.isNotBlank() }?.let { parseHexColor(it) },
        parseHexColor(to)
    )
    if (colors.size < 2) {
        val solid = colors.firstOrNull() ?: Color.Transparent
        return Brush.linearGradient(colors = listOf(solid, solid))
    }
    val (start, end) = gradientStartEnd(direction)
    return Brush.linearGradient(colors = colors, start = start, end = end)
}

// ---------------------------------------------------------------------------
// Token maps (Tailwind equivalents)
// ---------------------------------------------------------------------------

private fun heroTextSizeSp(size: String): androidx.compose.ui.unit.TextUnit = when (size) {
    "xs" -> 10.sp
    "sm" -> 12.sp
    "md" -> 12.sp
    "lg" -> 14.sp
    "xl" -> 18.sp
    "2xl" -> 20.sp
    "3xl" -> 24.sp
    else -> 12.sp
}

private fun heroFontWeight(weight: String): FontWeight = when (weight) {
    "normal" -> FontWeight.Normal
    "medium" -> FontWeight.Medium
    "semibold" -> FontWeight.SemiBold
    "bold" -> FontWeight.Bold
    "extrabold" -> FontWeight.ExtraBold
    else -> FontWeight.Normal
}

private fun heroSpacingDp(spacing: String): Dp = when (spacing) {
    "none" -> 0.dp
    "xs" -> 4.dp
    "sm" -> 8.dp
    "md" -> 12.dp
    "lg" -> 16.dp
    "xl" -> 24.dp
    else -> 12.dp
}

private fun heroBoxMaxWidth(maxWidth: String): Dp = when (maxWidth) {
    "sm" -> 448.dp
    "md" -> 672.dp
    "lg" -> 768.dp
    "xl" -> 896.dp
    else -> Dp.Infinity
}

private fun heroElementMaxWidth(maxWidth: String): Dp = when (maxWidth) {
    "sm" -> 256.dp
    "md" -> 320.dp
    "lg" -> 384.dp
    else -> Dp.Infinity
}

private fun heroButtonPadding(size: String, scale: Float): PaddingValues = when (size) {
    "sm" -> PaddingValues(horizontal = 16.dp * scale, vertical = 8.dp * scale)
    "md" -> PaddingValues(horizontal = 20.dp * scale, vertical = 8.dp * scale)
    else -> PaddingValues(horizontal = 24.dp * scale, vertical = 10.dp * scale)
}

private fun heroButtonTextSize(size: String): androidx.compose.ui.unit.TextUnit = when (size) {
    "sm" -> 12.sp
    "md" -> 14.sp
    else -> 14.sp
}

private fun heroRoundedShape(rounded: String): RoundedCornerShape = when (rounded) {
    "md" -> RoundedCornerShape(8.dp)
    "lg" -> RoundedCornerShape(12.dp)
    "xl" -> RoundedCornerShape(16.dp)
    else -> RoundedCornerShape(50)
}

private fun resolveHeroAlign(align: String, fallback: String): String =
    if (align == "inherit") fallback else align

private fun heroTextAlign(align: String): TextAlign = when (align) {
    "start" -> TextAlign.Start
    "end" -> TextAlign.End
    else -> TextAlign.Center
}

private fun heroHorizontalAlignment(align: String): Alignment.Horizontal = when (align) {
    "start" -> Alignment.Start
    "end" -> Alignment.End
    else -> Alignment.CenterHorizontally
}

private fun heroSpanStyle(color: HeroColorStyle): SpanStyle =
    if (color.mode == "gradient") {
        SpanStyle(brush = heroGradientBrush(color.from, color.via, color.to, color.direction))
    } else {
        SpanStyle(color = colorWithAlpha(parseHexColor(color.color, Color.White), color.opacity))
    }

private fun heroTextStyle(color: HeroColorStyle): TextStyle =
    if (color.mode == "gradient") {
        TextStyle(brush = heroGradientBrush(color.from, color.via, color.to, color.direction))
    } else {
        TextStyle(color = colorWithAlpha(parseHexColor(color.color, Color.White), color.opacity))
    }

private fun heroElementAnnotatedString(element: HeroElement): AnnotatedString =
    buildAnnotatedString {
        if (element.segments.isNullOrEmpty()) {
            withStyle(heroSpanStyle(element.color)) { append(element.text) }
        } else {
            element.segments.forEach { segment ->
                withStyle(heroSpanStyle(segment.color)) { append(segment.text) }
            }
        }
    }

// ---------------------------------------------------------------------------
// Element renderers
// ---------------------------------------------------------------------------

@Composable
private fun HeroBadgeElement(element: HeroElement, textAlign: TextAlign, scale: Float) {
    val badge = element.badge ?: return
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(colorWithAlpha(parseHexColor(badge.background), badge.backgroundOpacity))
            .border(
                width = 1.dp,
                color = colorWithAlpha(parseHexColor(badge.borderColor), badge.borderOpacity),
                shape = RoundedCornerShape(50)
            )
            .padding(horizontal = 14.dp * scale, vertical = 8.dp * scale),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp * scale)
    ) {
        if (badge.showDot) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(parseHexColor(badge.dotColor))
            )
        }
        Text(
            text = element.text,
            style = heroTextStyle(element.color).copy(
                fontSize = heroTextSizeSp(element.size) * scale,
                fontWeight = heroFontWeight(element.weight)
            ),
            textAlign = textAlign
        )
    }
}

@Composable
private fun HeroHeadingElement(element: HeroElement, textAlign: TextAlign, scale: Float) {
    Text(
        text = heroElementAnnotatedString(element),
        style = TextStyle(
            fontSize = heroTextSizeSp(element.size) * scale,
            fontWeight = heroFontWeight(element.weight),
            lineHeight = heroTextSizeSp(element.size) * scale * 1.2f
        ),
        textAlign = textAlign,
        modifier = Modifier.widthIn(max = heroElementMaxWidth(element.maxWidth))
    )
}

@Composable
private fun HeroParagraphElement(element: HeroElement, textAlign: TextAlign, scale: Float) {
    Text(
        text = heroElementAnnotatedString(element),
        style = TextStyle(
            fontSize = heroTextSizeSp(element.size) * scale,
            fontWeight = heroFontWeight(element.weight),
            lineHeight = heroTextSizeSp(element.size) * scale * 1.6f
        ),
        textAlign = textAlign,
        modifier = Modifier.widthIn(max = heroElementMaxWidth(element.maxWidth))
    )
}

@Composable
private fun HeroButtonIcon(name: String, color: String, scale: Float) {
    val icon: ImageVector? = when (name) {
        "arrowLeft" -> Icons.Filled.ArrowBack
        "arrowRight" -> Icons.Filled.ArrowForward
        "star" -> Icons.Filled.Star
        "sparkles" -> Icons.Filled.AutoAwesome
        "shoppingBag" -> Icons.Filled.ShoppingBag
        "flame" -> Icons.Filled.LocalFireDepartment
        "heart" -> Icons.Filled.Favorite
        "tag" -> Icons.Filled.Sell
        else -> null
    }
    if (icon != null) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = parseHexColor(color, Color.White),
            modifier = Modifier.size(18.dp * scale)
        )
    }
}

@Composable
private fun HeroButtonElement(element: HeroElement, onLinkClick: (String) -> Unit, scale: Float) {
    val button = element.button ?: return
    val shape = heroRoundedShape(button.rounded)
    val hasBorder = button.variant == "glass" || button.variant == "outline"

    val backgroundModifier: Modifier = when (button.variant) {
        "gradient" -> Modifier.background(
            brush = heroGradientBrush(button.from, "", button.to, element.color.direction),
            shape = shape
        )
        "solid", "glass" -> Modifier.background(
            color = colorWithAlpha(parseHexColor(button.from), button.backgroundOpacity),
            shape = shape
        )
        else -> Modifier
    }

    Box(
        modifier = Modifier
            .then(if (button.fullWidthMobile) Modifier.fillMaxWidth() else Modifier.widthIn(max = 320.dp * scale))
            .clip(shape)
            .then(backgroundModifier)
            .then(
                if (hasBorder) {
                    Modifier.border(
                        width = 2.dp,
                        color = colorWithAlpha(parseHexColor(button.borderColor), button.borderOpacity),
                        shape = shape
                    )
                } else {
                    Modifier
                }
            )
            .clickable { onLinkClick(button.href) }
            .padding(heroButtonPadding(button.size, scale)),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp * scale)
        ) {
            if (button.iconPosition == "start") {
                HeroButtonIcon(name = button.icon, color = button.iconColor, scale = scale)
            }
            Text(
                text = element.text,
                style = TextStyle(
                    fontSize = heroButtonTextSize(button.size) * scale,
                    fontWeight = heroFontWeight(element.weight),
                    color = parseHexColor(button.textColor, Color.White)
                ),
                textAlign = TextAlign.Center
            )
            if (button.iconPosition == "end") {
                HeroButtonIcon(name = button.icon, color = button.iconColor, scale = scale)
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Grouping
// ---------------------------------------------------------------------------

private sealed interface HeroGroup {
    data class Single(val element: HeroElement) : HeroGroup
    data class Buttons(val elements: List<HeroElement>) : HeroGroup
}

/** Consecutive buttons share one column, matching the original CTA pair. */
private fun groupElements(elements: List<HeroElement>): List<HeroGroup> {
    val groups = mutableListOf<HeroGroup>()
    for (element in elements) {
        if (element.type == HeroElementType.BUTTON) {
            val last = groups.lastOrNull()
            if (last is HeroGroup.Buttons) {
                groups[groups.size - 1] = HeroGroup.Buttons(last.elements + element)
            } else {
                groups.add(HeroGroup.Buttons(listOf(element)))
            }
        } else {
            groups.add(HeroGroup.Single(element))
        }
    }
    return groups
}

// ---------------------------------------------------------------------------
// Content layer
// ---------------------------------------------------------------------------

/**
 * Renders the admin-authored hero content (badge, heading, paragraph, buttons)
 * on top of the hero image. Mirrors the storefront's `HeroContentLayer`.
 */
@Composable
fun HeroContentOverlay(
    content: HeroContent,
    modifier: Modifier = Modifier,
    onLinkClick: (String) -> Unit = {}
) {
    if (!content.enabled) return
    val visibleElements = content.elements.filter { it.visible }
    if (visibleElements.isEmpty()) return

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val boxMaxWidth = maxWidth
        val boxMaxHeight = maxHeight
        val textScale = (boxMaxWidth / 360.dp).coerceIn(0.85f, 1.3f)
        val boxAlignment: Alignment = when {
            content.horizontalPosition == "start" && content.verticalPosition == "top" -> Alignment.TopStart
            content.horizontalPosition == "start" && content.verticalPosition == "bottom" -> Alignment.BottomStart
            content.horizontalPosition == "start" -> Alignment.CenterStart
            content.horizontalPosition == "end" && content.verticalPosition == "top" -> Alignment.TopEnd
            content.horizontalPosition == "end" && content.verticalPosition == "bottom" -> Alignment.BottomEnd
            content.horizontalPosition == "end" -> Alignment.CenterEnd
            content.verticalPosition == "top" -> Alignment.TopCenter
            content.verticalPosition == "bottom" -> Alignment.BottomCenter
            else -> Alignment.Center
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            contentAlignment = boxAlignment
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = heroBoxMaxWidth(content.maxWidth))
                    .offset(
                        x = boxMaxWidth * (content.offsetX / 100f).coerceIn(-0.08f, 0.08f),
                        y = boxMaxHeight * (content.offsetY / 100f).coerceIn(-0.1f, 0.1f)
                    )
            ) {
                groupElements(visibleElements).forEach { group ->
                    when (group) {
                        is HeroGroup.Single -> {
                            val element = group.element
                            val align = resolveHeroAlign(element.align, content.textAlign)
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = heroSpacingDp(element.spacing) * textScale),
                                horizontalAlignment = heroHorizontalAlignment(align)
                            ) {
                                when (element.type) {
                                    HeroElementType.BADGE -> HeroBadgeElement(element, heroTextAlign(align), textScale)
                                    HeroElementType.HEADING -> HeroHeadingElement(element, heroTextAlign(align), textScale)
                                    HeroElementType.PARAGRAPH -> HeroParagraphElement(element, heroTextAlign(align), textScale)
                                    HeroElementType.BUTTON -> HeroButtonElement(element, onLinkClick, textScale)
                                }
                            }
                        }
                        is HeroGroup.Buttons -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = heroSpacingDp(group.elements.first().spacing) * textScale),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp * textScale)
                            ) {
                                group.elements.forEach { element ->
                                    HeroButtonElement(element, onLinkClick, textScale)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/** The two blurred decorative colour blobs in the hero corners. */
@Composable
fun HeroDecorations(modifier: Modifier = Modifier) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val blobSize = minOf(maxWidth, maxHeight)
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = blobSize * 0.30f, y = -blobSize * 0.30f)
                .size(blobSize * 0.55f)
                .background(
                    brush = Brush.linearGradient(
                        listOf(Color(0xFF3B82F6).copy(alpha = 0.3f), Color(0xFFA855F7).copy(alpha = 0.3f))
                    ),
                    shape = CircleShape
                )
                .blur(radius = 24.dp, edgeTreatment = BlurredEdgeTreatment.Unbounded)
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = -blobSize * 0.35f, y = blobSize * 0.35f)
                .size(blobSize * 0.7f)
                .background(
                    brush = Brush.linearGradient(
                        listOf(Color(0xFFEC4899).copy(alpha = 0.2f), Color(0xFFF97316).copy(alpha = 0.2f))
                    ),
                    shape = CircleShape
                )
                .blur(radius = 28.dp, edgeTreatment = BlurredEdgeTreatment.Unbounded)
        )
    }
}