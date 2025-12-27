package com.voxcina.shop.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.voxcina.shop.R
import com.voxcina.shop.domain.model.Category
import com.voxcina.shop.ui.theme.Primary
import com.voxcina.shop.ui.theme.PrimaryDark
import com.voxcina.shop.ui.theme.VoxcinaTheme

/**
 * Category section component displaying categories in a horizontally scrollable row.
 * Implements Requirements 3.2, 3.5, 3.6 from the home screen spec.
 *
 * @param categories List of categories to display
 * @param modifier Modifier for the section container
 * @param onCategoryClick Callback when a category is clicked with category ID
 * @param onViewAllClick Callback when "View All" button is clicked
 */
@Composable
fun CategorySection(
    categories: List<Category>,
    modifier: Modifier = Modifier,
    onCategoryClick: (String) -> Unit = {},
    onViewAllClick: () -> Unit = {}
) {
    if (categories.isEmpty()) return
    
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column(
            modifier = modifier.fillMaxWidth()
        ) {
            // Section header with title and "View All" button
            SectionHeader(
                title = stringResource(R.string.home_categories_title),
                onViewAllClick = onViewAllClick,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Horizontally scrollable category row without visible scrollbar
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(
                    items = categories,
                    key = { it.id }
                ) { category ->
                    CategoryItem(
                        category = category,
                        onClick = { onCategoryClick(category.id) }
                    )
                }
            }
        }
    }
}


/**
 * Section header with title and "View All" button.
 * Reusable component for all home screen sections.
 *
 * @param title Section title text
 * @param modifier Modifier for the header row
 * @param onViewAllClick Callback when "View All" button is clicked
 */
@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    onViewAllClick: () -> Unit = {}
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = PrimaryDark,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        
        TextButton(
            onClick = onViewAllClick
        ) {
            Text(
                text = stringResource(R.string.home_view_all),
                color = Primary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = null,
                tint = Primary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

/**
 * Individual category item with circular icon and name.
 * Implements Requirements 3.3, 3.4 from the home screen spec.
 *
 * @param category The category data to display
 * @param onClick Callback when the category is clicked
 * @param modifier Modifier for the item container
 */
@Composable
fun CategoryItem(
    category: Category,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(72.dp)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Circular icon container with colored background
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(category.iconColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = category.name,
                tint = category.iconColor,
                modifier = Modifier.size(28.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Category name below icon
        Text(
            text = category.name,
            color = PrimaryDark,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// ============ Preview Functions ============

@Preview(showBackground = true)
@Composable
private fun CategorySectionPreview() {
    VoxcinaTheme {
        CategorySection(
            categories = sampleCategories,
            modifier = Modifier.padding(vertical = 16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CategoryItemPreview() {
    VoxcinaTheme {
        CategoryItem(
            category = Category(
                id = "1",
                name = "پوشاک مردانه",
                slug = "mens-clothing",
                imageUrl = null,
                iconName = null,
                iconColor = Primary,
                parentId = null,
                isActive = true
            ),
            onClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SectionHeaderPreview() {
    VoxcinaTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            SectionHeader(
                title = "دسته‌بندی‌ها",
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CategorySectionEmptyPreview() {
    VoxcinaTheme {
        CategorySection(
            categories = emptyList(),
            modifier = Modifier.padding(vertical = 16.dp)
        )
    }
}

// Sample data for previews
private val sampleCategories = listOf(
    Category(
        id = "1",
        name = "پوشاک مردانه",
        slug = "mens-clothing",
        imageUrl = null,
        iconName = null,
        iconColor = Color(0xFF1A3C69),
        parentId = null,
        isActive = true
    ),
    Category(
        id = "2",
        name = "پوشاک زنانه",
        slug = "womens-clothing",
        imageUrl = null,
        iconName = null,
        iconColor = Color(0xFF10B981),
        parentId = null,
        isActive = true
    ),
    Category(
        id = "3",
        name = "کفش",
        slug = "shoes",
        imageUrl = null,
        iconName = null,
        iconColor = Color(0xFFF59E0B),
        parentId = null,
        isActive = true
    ),
    Category(
        id = "4",
        name = "اکسسوری",
        slug = "accessories",
        imageUrl = null,
        iconName = null,
        iconColor = Color(0xFFEF4444),
        parentId = null,
        isActive = true
    ),
    Category(
        id = "5",
        name = "ورزشی",
        slug = "sports",
        imageUrl = null,
        iconName = null,
        iconColor = Color(0xFF8B5CF6),
        parentId = null,
        isActive = true
    ),
    Category(
        id = "6",
        name = "بچگانه",
        slug = "kids",
        imageUrl = null,
        iconName = null,
        iconColor = Color(0xFF06B6D4),
        parentId = null,
        isActive = true
    )
)
