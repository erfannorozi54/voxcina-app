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
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.voxcina.shop.R
import com.voxcina.shop.ui.theme.Primary
import com.voxcina.shop.ui.theme.PrimaryDark
import com.voxcina.shop.ui.theme.Secondary
import com.voxcina.shop.ui.theme.VoxcinaTheme

/**
 * Home screen search bar component with focus state styling.
 * Implements Requirements 1.4, 1.5 from the home screen spec.
 *
 * @param value Current search text
 * @param onValueChange Callback when search text changes
 * @param modifier Modifier for the search bar container
 * @param onSearch Callback when search is submitted
 * @param onClick Callback when search bar is clicked (for navigation to search screen)
 * @param enabled Whether the search bar is interactive
 * @param readOnly Whether the search bar is read-only (clickable but not editable)
 */
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
    
    // Animate border color based on focus state
    val borderColor by animateColorAsState(
        targetValue = if (isFocused) Primary else Color(0xFFE5E7EB),
        label = "borderColor"
    )
    
    // Animate border width based on focus state
    val borderWidth = if (isFocused) 2.dp else 1.dp
    
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Secondary)
                .border(
                    width = borderWidth,
                    color = borderColor,
                    shape = RoundedCornerShape(12.dp)
                )
                .then(
                    if (onClick != null && readOnly) {
                        Modifier.clickable(onClick = onClick)
                    } else {
                        Modifier
                    }
                ),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Search icon
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = stringResource(R.string.home_search),
                    tint = if (isFocused) Primary else Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                // Text input or placeholder
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (readOnly) {
                        // Read-only mode - just show placeholder
                        Text(
                            text = stringResource(R.string.home_search_placeholder),
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    } else {
                        // Editable mode
                        BasicTextField(
                            value = value,
                            onValueChange = onValueChange,
                            modifier = Modifier
                                .fillMaxWidth()
                                .focusRequester(focusRequester),
                            enabled = enabled,
                            textStyle = TextStyle(
                                color = PrimaryDark,
                                fontSize = 14.sp,
                                textDirection = TextDirection.Rtl
                            ),
                            singleLine = true,
                            interactionSource = interactionSource,
                            cursorBrush = SolidColor(Primary),
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Search
                            ),
                            keyboardActions = KeyboardActions(
                                onSearch = { onSearch(value) }
                            ),
                            decorationBox = { innerTextField ->
                                Box {
                                    if (value.isEmpty()) {
                                        Text(
                                            text = stringResource(R.string.home_search_placeholder),
                                            color = Color.Gray,
                                            fontSize = 14.sp
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
private fun SearchBarEmptyPreview() {
    VoxcinaTheme {
        SearchBar(
            value = "",
            onValueChange = {},
            modifier = Modifier.padding(vertical = 16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchBarWithTextPreview() {
    VoxcinaTheme {
        SearchBar(
            value = "کفش ورزشی",
            onValueChange = {},
            modifier = Modifier.padding(vertical = 16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchBarReadOnlyPreview() {
    VoxcinaTheme {
        SearchBar(
            value = "",
            onValueChange = {},
            readOnly = true,
            onClick = {},
            modifier = Modifier.padding(vertical = 16.dp)
        )
    }
}
