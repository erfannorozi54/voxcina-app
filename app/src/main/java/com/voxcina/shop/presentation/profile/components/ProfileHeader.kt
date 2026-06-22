package com.voxcina.shop.presentation.profile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.voxcina.shop.ui.theme.Primary
import com.voxcina.shop.ui.theme.VoxcinaTheme

/**
 * Profile header component displaying user avatar, name, and phone number.
 * Features a circular avatar with white border and soft shadow,
 * an edit icon overlay at bottom-right, and user information below.
 *
 * @param avatarUrl URL of the user's avatar image (null shows placeholder)
 * @param userName User's full name displayed in bold
 * @param phoneNumber User's phone number displayed in LTR direction
 * @param onEditClick Callback when the edit icon is clicked
 * @param modifier Modifier for the component
 */
@Composable
fun ProfileHeader(
    avatarUrl: String?,
    userName: String,
    phoneNumber: String,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Avatar with edit icon overlay
        Box(
            contentAlignment = Alignment.Center
        ) {
            // Avatar container with shadow and border
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .shadow(
                        elevation = 8.dp,
                        shape = CircleShape,
                        ambientColor = Color.Black.copy(alpha = 0.1f),
                        spotColor = Color.Black.copy(alpha = 0.1f)
                    )
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(
                        width = 3.dp,
                        color = Color.White,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (avatarUrl != null) {
                    AsyncImage(
                        model = avatarUrl,
                        contentDescription = "تصویر پروفایل",
                        modifier = Modifier
                            .size(94.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    // Placeholder avatar
                    Box(
                        modifier = Modifier
                            .size(94.dp)
                            .clip(CircleShape)
                            .background(Primary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "تصویر پروفایل پیش‌فرض",
                            modifier = Modifier.size(48.dp),
                            tint = Primary.copy(alpha = 0.5f)
                        )
                    }
                }
            }
            
            // Edit icon overlay at bottom-right
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = (-4).dp, y = (-4).dp)
                    .size(32.dp)
                    .shadow(
                        elevation = 4.dp,
                        shape = CircleShape
                    )
                    .clip(CircleShape)
                    .background(Primary)
                    .clickable(onClick = onEditClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "ویرایش پروفایل",
                    modifier = Modifier.size(16.dp),
                    tint = Color.White
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // User name
        Text(
            text = userName,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        // Phone number in LTR direction
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            Text(
                text = phoneNumber,
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}
