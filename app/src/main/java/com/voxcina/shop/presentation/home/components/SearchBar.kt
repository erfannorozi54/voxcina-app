package com.voxcina.shop.presentation.home.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.voxcina.shop.R
import com.voxcina.shop.ui.theme.Primary
import com.voxcina.shop.ui.theme.PrimaryDark
import com.voxcina.shop.ui.theme.VazirMatnFamily
import com.voxcina.shop.ui.theme.VoxcinaTheme

@Composable
fun SearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    onSearch: (String) -> Unit = {},
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    readOnly: Boolean = false
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val focusRequester = remember { FocusRequester() }
    
    val borderColor by animateColorAsState(
        targetValue = if (isFocused) Primary else Color.Transparent,
        label = "borderColor"
    )
    
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(16.dp),
                    ambientColor = Primary.copy(alpha = 0.08f),
                    spotColor = Primary.copy(alpha = 0.05f)
                )
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color.White,
                            Color.White.copy(alpha = 0.95f)
                        )
                    )
                )
                .border(
                    width = if (isFocused) 1.5.dp else 1.dp,
                    color = if (isFocused) borderColor else Color.Gray.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(16.dp)
                )
                .height(52.dp)
                .then(
                    if (onClick != null && readOnly) Modifier.clickable(onClick = onClick) else Modifier
                ),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Primary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Search,
                        contentDescription = stringResource(R.string.home_search),
                        tint = Primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                    if (readOnly) {
                        Text(
                            text = stringResource(R.string.home_search_placeholder),
                            color = Color.Gray.copy(alpha = 0.6f),
                            fontSize = 14.sp,
                            fontFamily = VazirMatnFamily
                        )
                    } else {
                        BasicTextField(
                            value = value,
                            onValueChange = onValueChange,
                            modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),
                            enabled = enabled,
                            textStyle = TextStyle(
                                color = PrimaryDark,
                                fontSize = 14.sp,
                                fontFamily = VazirMatnFamily,
                                fontWeight = FontWeight.Normal,
                                textDirection = TextDirection.Rtl
                            ),
                            singleLine = true,
                            interactionSource = interactionSource,
                            cursorBrush = SolidColor(Primary),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(onSearch = { onSearch(value) }),
                            decorationBox = { innerTextField ->
                                Box {
                                    if (value.isEmpty()) {
                                        Text(
                                            text = stringResource(R.string.home_search_placeholder),
                                            color = Color.Gray.copy(alpha = 0.6f),
                                            fontSize = 14.sp,
                                            fontFamily = VazirMatnFamily
                                        )
                                    }
                                    innerTextField()
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchBarPreview() {
    VoxcinaTheme {
        SearchBar(value = "", onValueChange = {}, modifier = Modifier.padding(vertical = 16.dp))
    }
}
