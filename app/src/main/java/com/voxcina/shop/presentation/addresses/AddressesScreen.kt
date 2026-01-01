package com.voxcina.shop.presentation.addresses

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.voxcina.shop.domain.model.UserAddress
import com.voxcina.shop.ui.components.EmptyState
import com.voxcina.shop.ui.components.ScreenHeader
import com.voxcina.shop.ui.components.SoftShadowCard
import com.voxcina.shop.ui.components.VoxcinaLoading
import com.voxcina.shop.ui.components.VoxcinaLoadingCompact
import com.voxcina.shop.ui.components.VoxcinaPrimaryButton
import com.voxcina.shop.ui.components.VoxcinaTextField
import com.voxcina.shop.ui.theme.Destructive
import com.voxcina.shop.ui.theme.Primary
import com.voxcina.shop.ui.theme.SecondaryLight
import com.voxcina.shop.ui.theme.Success

@Composable
fun AddressesScreen(
    onNavigateBack: () -> Unit,
    viewModel: AddressesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val formState by viewModel.formState.collectAsState()

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            topBar = {
                ScreenHeader(
                    title = "آدرس‌های من",
                    onBackClick = onNavigateBack
                )
            },
            floatingActionButton = {
                if (!uiState.isLoading && uiState.error == null) {
                    FloatingActionButton(
                        onClick = { viewModel.showAddDialog() },
                        containerColor = Primary,
                        contentColor = Color.White
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "افزودن آدرس")
                    }
                }
            },
            containerColor = SecondaryLight
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                when {
                    uiState.isLoading -> {
                        VoxcinaLoading(modifier = Modifier.align(Alignment.Center))
                    }
                    uiState.error != null -> {
                        ErrorContent(
                            message = uiState.error!!,
                            onRetry = { viewModel.loadAddresses() },
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    uiState.addresses.isEmpty() -> {
                        EmptyState(
                            icon = Icons.Outlined.LocationOn,
                            title = "آدرسی ثبت نشده است",
                            subtitle = "برای ثبت سفارش، آدرس خود را اضافه کنید",
                            actionButtonText = "افزودن آدرس",
                            onActionClick = { viewModel.showAddDialog() }
                        )
                    }
                    else -> {
                        AddressList(
                            addresses = uiState.addresses,
                            deletingIndex = uiState.isDeleting,
                            onEdit = { viewModel.showEditDialog(it) },
                            onDelete = { viewModel.deleteAddress(it) }
                        )
                    }
                }
            }
        }

        if (uiState.showAddDialog) {
            AddressDialog(
                formState = formState,
                isEditing = uiState.editingIndex != null,
                isSaving = uiState.isSaving,
                error = uiState.saveError,
                onFormChange = { viewModel.updateForm(it) },
                onSave = { viewModel.saveAddress() },
                onDismiss = { viewModel.dismissDialog() }
            )
        }
    }
}

@Composable
private fun AddressList(
    addresses: List<UserAddress>,
    deletingIndex: Int?,
    onEdit: (Int) -> Unit,
    onDelete: (Int) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        itemsIndexed(addresses) { index, address ->
            AddressCard(
                address = address,
                isDeleting = deletingIndex == index,
                onEdit = { onEdit(index) },
                onDelete = { onDelete(index) }
            )
        }
    }
}

@Composable
private fun AddressCard(
    address: UserAddress,
    isDeleting: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }

    SoftShadowCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 12.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Primary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Outlined.LocationOn,
                            contentDescription = null,
                            tint = Primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = address.city,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Primary
                        )
                        if (address.isDefault) {
                            Text(
                                text = "پیش‌فرض",
                                style = MaterialTheme.typography.labelSmall,
                                color = Success
                            )
                        }
                    }
                }

                Row {
                    IconButton(onClick = onEdit) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "ویرایش",
                            tint = Primary
                        )
                    }
                    if (isDeleting) {
                        VoxcinaLoadingCompact(size = 24.dp)
                    } else {
                        IconButton(onClick = { showDeleteConfirm = true }) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "حذف",
                                tint = Destructive
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            val fullAddress = buildString {
                address.street?.let { append(it) }
                address.address?.let {
                    if (isNotEmpty()) append("، ")
                    append(it)
                }
            }
            if (fullAddress.isNotBlank()) {
                Text(
                    text = fullAddress,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }

            Text(
                text = "کد پستی: ${address.postalCode}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray.copy(alpha = 0.7f)
            )
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("حذف آدرس") },
            text = { Text("آیا از حذف این آدرس اطمینان دارید؟") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteConfirm = false
                    onDelete()
                }) {
                    Text("حذف", color = Destructive)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("انصراف")
                }
            }
        )
    }
}

@Composable
private fun AddressDialog(
    formState: AddressFormState,
    isEditing: Boolean,
    isSaving: Boolean,
    error: String?,
    onFormChange: (AddressFormState) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { if (!isSaving) onDismiss() },
        title = {
            Text(
                text = if (isEditing) "ویرایش آدرس" else "افزودن آدرس",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                VoxcinaTextField(
                    value = formState.city,
                    onValueChange = { onFormChange(formState.copy(city = it)) },
                    label = "شهر *",
                    enabled = !isSaving
                )
                VoxcinaTextField(
                    value = formState.street,
                    onValueChange = { onFormChange(formState.copy(street = it)) },
                    label = "خیابان",
                    enabled = !isSaving
                )
                VoxcinaTextField(
                    value = formState.address,
                    onValueChange = { onFormChange(formState.copy(address = it)) },
                    label = "آدرس کامل *",
                    enabled = !isSaving,
                    singleLine = false
                )
                VoxcinaTextField(
                    value = formState.postalCode,
                    onValueChange = { onFormChange(formState.copy(postalCode = it)) },
                    label = "کد پستی *",
                    keyboardType = KeyboardType.Number,
                    enabled = !isSaving
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(enabled = !isSaving) {
                            onFormChange(formState.copy(isDefault = !formState.isDefault))
                        }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = formState.isDefault,
                        onCheckedChange = { onFormChange(formState.copy(isDefault = it)) },
                        enabled = !isSaving,
                        colors = CheckboxDefaults.colors(checkedColor = Primary)
                    )
                    Text("آدرس پیش‌فرض")
                }
                if (error != null) {
                    Text(text = error, color = Destructive, style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            VoxcinaPrimaryButton(
                text = if (isEditing) "ذخیره تغییرات" else "افزودن",
                onClick = onSave,
                isLoading = isSaving,
                enabled = formState.isValid(),
                modifier = Modifier.width(140.dp)
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isSaving) {
                Text("انصراف")
            }
        }
    )
}

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = Destructive
        )
        Spacer(modifier = Modifier.height(16.dp))
        VoxcinaPrimaryButton(
            text = "تلاش مجدد",
            onClick = onRetry,
            modifier = Modifier.width(160.dp)
        )
    }
}
