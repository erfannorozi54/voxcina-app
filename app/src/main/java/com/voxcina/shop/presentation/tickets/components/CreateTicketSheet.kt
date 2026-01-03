package com.voxcina.shop.presentation.tickets.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.voxcina.shop.domain.model.TicketCategory
import com.voxcina.shop.domain.model.TicketPriority
import com.voxcina.shop.ui.components.VoxcinaPrimaryButton
import com.voxcina.shop.ui.theme.Primary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTicketSheet(
    onDismiss: () -> Unit,
    onCreate: (subject: String, category: String, priority: String, message: String) -> Unit,
    isCreating: Boolean
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    
    var subject by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(TicketCategory.GENERAL) }
    var selectedPriority by remember { mutableStateOf(TicketPriority.MEDIUM) }
    var message by remember { mutableStateOf("") }
    
    var categoryExpanded by remember { mutableStateOf(false) }
    var priorityExpanded by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 32.dp)
            ) {
                Text(
                    text = "ثبت تیکت جدید",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Primary
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Subject
                OutlinedTextField(
                    value = subject,
                    onValueChange = { subject = it },
                    label = { Text("عنوان تیکت") },
                    placeholder = { Text("مثلاً: مشکل در ثبت سفارش") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = textFieldColors()
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Category dropdown
                    ExposedDropdownMenuBox(
                        expanded = categoryExpanded,
                        onExpandedChange = { categoryExpanded = it },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = selectedCategory.displayName,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("دستهبندی") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                            modifier = Modifier.menuAnchor(),
                            shape = RoundedCornerShape(12.dp),
                            colors = textFieldColors()
                        )
                        ExposedDropdownMenu(
                            expanded = categoryExpanded,
                            onDismissRequest = { categoryExpanded = false }
                        ) {
                            TicketCategory.entries.forEach { category ->
                                DropdownMenuItem(
                                    text = { Text(category.displayName) },
                                    onClick = {
                                        selectedCategory = category
                                        categoryExpanded = false
                                    }
                                )
                            }
                        }
                    }
                    
                    // Priority dropdown
                    ExposedDropdownMenuBox(
                        expanded = priorityExpanded,
                        onExpandedChange = { priorityExpanded = it },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = selectedPriority.displayName,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("اولویت") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = priorityExpanded) },
                            modifier = Modifier.menuAnchor(),
                            shape = RoundedCornerShape(12.dp),
                            colors = textFieldColors()
                        )
                        ExposedDropdownMenu(
                            expanded = priorityExpanded,
                            onDismissRequest = { priorityExpanded = false }
                        ) {
                            TicketPriority.entries.forEach { priority ->
                                DropdownMenuItem(
                                    text = { Text(priority.displayName) },
                                    onClick = {
                                        selectedPriority = priority
                                        priorityExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Message
                OutlinedTextField(
                    value = message,
                    onValueChange = { message = it },
                    label = { Text("متن پیام") },
                    placeholder = { Text("لطفاً مشکل یا سوال خود را با جزئیات توضیح دهید...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = textFieldColors()
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                VoxcinaPrimaryButton(
                    text = if (isCreating) "در حال ثبت..." else "ثبت تیکت",
                    onClick = {
                        if (subject.isNotBlank() && message.isNotBlank()) {
                            onCreate(subject, selectedCategory.apiValue, selectedPriority.apiValue, message)
                        }
                    },
                    enabled = subject.isNotBlank() && message.isNotBlank() && !isCreating,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun textFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Primary,
    unfocusedBorderColor = Color(0xFFE5E7EB),
    focusedLabelColor = Primary,
    cursorColor = Primary
)
