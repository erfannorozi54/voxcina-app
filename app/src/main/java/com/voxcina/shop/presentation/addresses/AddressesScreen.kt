package com.voxcina.shop.presentation.addresses

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.voxcina.shop.data.remote.dto.CityDto
import com.voxcina.shop.data.remote.dto.ProvinceDto
import com.voxcina.shop.domain.model.UserAddress
import com.voxcina.shop.ui.components.EmptyState
import com.voxcina.shop.ui.components.LatLng
import com.voxcina.shop.ui.components.LocationPicker
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
                ScreenHeader(title = "آدرسهای من", onBackClick = onNavigateBack)
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
            Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                when {
                    uiState.isLoading -> VoxcinaLoading(modifier = Modifier.align(Alignment.Center))
                    uiState.error != null -> ErrorContent(
                        message = uiState.error!!,
                        onRetry = { viewModel.loadAddresses() },
                        modifier = Modifier.align(Alignment.Center)
                    )
                    uiState.addresses.isEmpty() -> EmptyState(
                        icon = Icons.Default.Home,
                        title = "آدرسی ثبت نشده است",
                        subtitle = "برای ثبت سفارش، آدرس خود را اضافه کنید",
                        actionButtonText = "افزودن آدرس",
                        onActionClick = { viewModel.showAddDialog() }
                    )
                    else -> AddressList(
                        addresses = uiState.addresses,
                        deletingIndex = uiState.isDeleting,
                        onEdit = { viewModel.showEditDialog(it) },
                        onDelete = { viewModel.deleteAddress(it) }
                    )
                }
            }
        }

        if (uiState.showAddDialog) {
            AddressDialog(
                formState = formState,
                uiState = uiState,
                isEditing = uiState.editingIndex != null,
                onFormChange = { viewModel.updateForm(it) },
                onProvinceSelected = { viewModel.loadCities(it) },
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
    val isWork = address.title?.contains("کار") == true || address.title?.contains("دفتر") == true

    SoftShadowCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 12.dp) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(40.dp).clip(CircleShape).background(Primary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            if (isWork) Icons.Default.Work else Icons.Default.Home,
                            contentDescription = null,
                            tint = Primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = address.title ?: "آدرس",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Primary
                        )
                        if (address.isDefault) {
                            Text("پیشفرض", style = MaterialTheme.typography.labelSmall, color = Success)
                        }
                    }
                }
                Row {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "ویرایش", tint = Primary)
                    }
                    if (isDeleting) {
                        VoxcinaLoadingCompact(size = 24.dp)
                    } else {
                        IconButton(onClick = { showDeleteConfirm = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "حذف", tint = Destructive)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            val fullName = listOfNotNull(address.firstName, address.lastName).joinToString(" ")
            if (fullName.isNotBlank()) {
                Text(fullName, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            }

            val location = listOfNotNull(address.province, address.city).joinToString("، ")
            if (location.isNotBlank()) {
                Text(location, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            }

            address.address?.let {
                Text(it, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }

            Text("کد پستی: ${address.postalCode}", style = MaterialTheme.typography.bodySmall, color = Color.Gray.copy(alpha = 0.7f))

            address.phoneNumber?.let {
                Text("شماره تماس: $it", style = MaterialTheme.typography.bodySmall, color = Primary)
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("حذف آدرس") },
            text = { Text("آیا از حذف این آدرس اطمینان دارید؟") },
            confirmButton = {
                TextButton(onClick = { showDeleteConfirm = false; onDelete() }) {
                    Text("حذف", color = Destructive)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("انصراف") }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddressDialog(
    formState: AddressFormState,
    uiState: AddressesUiState,
    isEditing: Boolean,
    onFormChange: (AddressFormState) -> Unit,
    onProvinceSelected: (Int) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit
) {
    val isSaving = uiState.isSaving
    val error = uiState.saveError

    Dialog(
        onDismissRequest = { if (!isSaving) onDismiss() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(0.95f).fillMaxHeight(0.95f),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isEditing) "ویرایش آدرس" else "افزودن آدرس",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = { if (!isSaving) onDismiss() }) {
                        Icon(Icons.Default.Close, contentDescription = "بستن")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Address Type Selection
                    item {
                        Text("نوع آدرس", style = MaterialTheme.typography.labelMedium, color = Primary)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            AddressTypeButton(
                                icon = Icons.Default.Home,
                                label = "خانه",
                                selected = formState.addressType == AddressType.HOME,
                                enabled = !isSaving,
                                onClick = { onFormChange(formState.copy(addressType = AddressType.HOME)) }
                            )
                            AddressTypeButton(
                                icon = Icons.Default.Work,
                                label = "محل کار",
                                selected = formState.addressType == AddressType.WORK,
                                enabled = !isSaving,
                                onClick = { onFormChange(formState.copy(addressType = AddressType.WORK)) }
                            )
                        }
                    }

                    item {
                        VoxcinaTextField(
                            value = formState.title,
                            onValueChange = { onFormChange(formState.copy(title = it)) },
                            label = "عنوان آدرس (مثال: خانه، محل کار)",
                            enabled = !isSaving
                        )
                    }

                    // Name fields
                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            VoxcinaTextField(
                                value = formState.firstName,
                                onValueChange = { onFormChange(formState.copy(firstName = it)) },
                                label = "نام *",
                                enabled = !isSaving,
                                modifier = Modifier.weight(1f)
                            )
                            VoxcinaTextField(
                                value = formState.lastName,
                                onValueChange = { onFormChange(formState.copy(lastName = it)) },
                                label = "نام خانوادگی *",
                                enabled = !isSaving,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    item {
                        VoxcinaTextField(
                            value = formState.phoneNumber,
                            onValueChange = { onFormChange(formState.copy(phoneNumber = it)) },
                            label = "شماره تماس *",
                            keyboardType = KeyboardType.Phone,
                            enabled = !isSaving
                        )
                    }

                    // Province dropdown
                    item {
                        DropdownField(
                            label = "استان *",
                            value = formState.province,
                            options = uiState.provinces.map { it.provinceName },
                            loading = uiState.loadingProvinces,
                            enabled = !isSaving && !uiState.loadingProvinces,
                            onSelect = { name ->
                                val province = uiState.provinces.find { it.provinceName == name }
                                if (province != null) {
                                    onFormChange(formState.copy(
                                        province = province.provinceName,
                                        provinceCode = province.provinceCode,
                                        city = "",
                                        cityCode = 0
                                    ))
                                    onProvinceSelected(province.provinceCode)
                                }
                            }
                        )
                    }

                    // City dropdown
                    item {
                        DropdownField(
                            label = "شهر *",
                            value = formState.city,
                            options = uiState.cities.map { it.cityName },
                            loading = uiState.loadingCities,
                            enabled = !isSaving && !uiState.loadingCities && formState.province.isNotBlank(),
                            onSelect = { name ->
                                val city = uiState.cities.find { it.cityName == name }
                                if (city != null) {
                                    onFormChange(formState.copy(city = city.cityName, cityCode = city.cityCode))
                                }
                            }
                        )
                    }

                    item {
                        VoxcinaTextField(
                            value = formState.address,
                            onValueChange = { onFormChange(formState.copy(address = it)) },
                            label = "آدرس کامل *",
                            enabled = !isSaving,
                            singleLine = false
                        )
                    }

                    item {
                        VoxcinaTextField(
                            value = formState.postalCode,
                            onValueChange = { onFormChange(formState.copy(postalCode = it)) },
                            label = "کد پستی *",
                            keyboardType = KeyboardType.Number,
                            enabled = !isSaving
                        )
                    }

                    // Map picker
                    item {
                        Text("موقعیت روی نقشه *", style = MaterialTheme.typography.labelMedium, color = Primary)
                        Spacer(modifier = Modifier.height(8.dp))
                        LocationPicker(
                            initialLocation = if (formState.latitude != 0.0) LatLng(formState.latitude, formState.longitude) else null,
                            onLocationSelected = { onFormChange(formState.copy(latitude = it.latitude, longitude = it.longitude)) }
                        )
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp))
                                .clickable(enabled = !isSaving) { onFormChange(formState.copy(isDefault = !formState.isDefault)) }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = formState.isDefault,
                                onCheckedChange = { onFormChange(formState.copy(isDefault = it)) },
                                enabled = !isSaving,
                                colors = CheckboxDefaults.colors(checkedColor = Primary)
                            )
                            Text("تنظیم به عنوان آدرس پیشفرض")
                        }
                    }

                    if (error != null) {
                        item {
                            Text(error, color = Destructive, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    TextButton(onClick = onDismiss, enabled = !isSaving, modifier = Modifier.weight(1f)) {
                        Text("انصراف")
                    }
                    VoxcinaPrimaryButton(
                        text = if (isEditing) "ذخیره تغییرات" else "افزودن آدرس",
                        onClick = onSave,
                        isLoading = isSaving,
                        enabled = formState.isValid(),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun AddressTypeButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(enabled = enabled, onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(if (selected) Primary.copy(alpha = 0.1f) else Color.Transparent)
                .border(2.dp, if (selected) Primary else Color.Gray.copy(alpha = 0.3f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = if (selected) Primary else Color.Gray, modifier = Modifier.size(24.dp))
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, style = MaterialTheme.typography.labelSmall, color = if (selected) Primary else Color.Gray)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DropdownField(
    label: String,
    value: String,
    options: List<String>,
    loading: Boolean,
    enabled: Boolean,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(expanded = expanded && enabled, onExpandedChange = { if (enabled) expanded = it }) {
        OutlinedTextField(
            value = if (loading) "در حال بارگذاری..." else value,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor(),
            enabled = enabled,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Primary,
                focusedLabelColor = Primary
            )
        )
        ExposedDropdownMenu(expanded = expanded && enabled, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = { onSelect(option); expanded = false }
                )
            }
        }
    }
}

@Composable
private fun ErrorContent(message: String, onRetry: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(message, style = MaterialTheme.typography.bodyLarge, color = Destructive)
        Spacer(modifier = Modifier.height(16.dp))
        VoxcinaPrimaryButton(text = "تلاش مجدد", onClick = onRetry, modifier = Modifier.width(160.dp))
    }
}
