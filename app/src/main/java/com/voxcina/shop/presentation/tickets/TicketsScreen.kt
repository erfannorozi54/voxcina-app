package com.voxcina.shop.presentation.tickets

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.voxcina.shop.domain.model.Ticket
import com.voxcina.shop.presentation.tickets.components.CreateTicketSheet
import com.voxcina.shop.presentation.tickets.components.TicketListItem
import com.voxcina.shop.ui.components.EmptyState
import com.voxcina.shop.ui.components.ScreenHeader
import com.voxcina.shop.ui.components.VoxcinaLoading
import com.voxcina.shop.ui.theme.Destructive
import com.voxcina.shop.ui.theme.Primary
import com.voxcina.shop.ui.theme.Primary100
import com.voxcina.shop.ui.theme.SecondaryLight
import com.voxcina.shop.ui.theme.VoxcinaTheme

@Composable
fun TicketsScreen(
    onNavigateBack: () -> Unit,
    onTicketClick: (String) -> Unit,
    viewModel: TicketsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    TicketsScreenContent(
        uiState = uiState,
        onBackClick = onNavigateBack,
        onTicketClick = onTicketClick,
        onStatusFilterChanged = { viewModel.onEvent(TicketsEvent.StatusFilterChanged(it)) },
        onShowCreateDialog = { viewModel.onEvent(TicketsEvent.ShowCreateDialog) },
        onHideCreateDialog = { viewModel.onEvent(TicketsEvent.HideCreateDialog) },
        onCreateTicket = { subject, category, priority, message ->
            viewModel.onEvent(TicketsEvent.CreateTicket(subject, category, priority, message))
        },
        onRetry = { viewModel.onEvent(TicketsEvent.Retry) }
    )
}

@Composable
private fun TicketsScreenContent(
    uiState: TicketsUiState,
    onBackClick: () -> Unit,
    onTicketClick: (String) -> Unit,
    onStatusFilterChanged: (String?) -> Unit,
    onShowCreateDialog: () -> Unit,
    onHideCreateDialog: () -> Unit,
    onCreateTicket: (String, String, String, String) -> Unit,
    onRetry: () -> Unit
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            topBar = {
                ScreenHeader(
                    title = "تیکتهای پشتیبانی",
                    onBackClick = onBackClick
                )
            },
            floatingActionButton = {
                if (uiState is TicketsUiState.Success) {
                    FloatingActionButton(
                        onClick = onShowCreateDialog,
                        containerColor = Primary,
                        contentColor = Color.White
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "تیکت جدید")
                    }
                }
            },
            containerColor = SecondaryLight
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when (uiState) {
                    is TicketsUiState.Loading -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            VoxcinaLoading(size = 100.dp, loadingText = "در حال بارگذاری...")
                        }
                    }
                    is TicketsUiState.Success -> {
                        TicketsSuccessContent(
                            state = uiState,
                            onTicketClick = onTicketClick,
                            onStatusFilterChanged = onStatusFilterChanged
                        )
                        
                        if (uiState.showCreateDialog) {
                            CreateTicketSheet(
                                onDismiss = onHideCreateDialog,
                                onCreate = onCreateTicket,
                                isCreating = uiState.isCreating
                            )
                        }
                    }
                    is TicketsUiState.Error -> {
                        EmptyState(
                            icon = Icons.Default.Error,
                            title = "خطا در بارگذاری",
                            subtitle = uiState.message,
                            actionButtonText = "تلاش مجدد",
                            onActionClick = onRetry,
                            iconTint = Destructive.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TicketsSuccessContent(
    state: TicketsUiState.Success,
    onTicketClick: (String) -> Unit,
    onStatusFilterChanged: (String?) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Header
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(Primary100, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ConfirmationNumber,
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Column(modifier = Modifier.padding(start = 12.dp)) {
                    Text(
                        text = "تیکتهای پشتیبانی",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Primary
                    )
                    Text(
                        text = "سوالات و درخواستهای خود را ثبت کنید",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }
        }

        // Status filter tabs
        StatusFilterTabs(
            selectedStatus = state.selectedStatus,
            onStatusSelected = onStatusFilterChanged
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Tickets list
        if (state.tickets.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                EmptyState(
                    icon = Icons.Default.ConfirmationNumber,
                    title = "تیکتی ثبت نشده",
                    subtitle = "برای ثبت تیکت جدید روی دکمه + کلیک کنید"
                )
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.tickets, key = { it.id }) { ticket ->
                    TicketListItem(
                        ticket = ticket,
                        onClick = { onTicketClick(ticket.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusFilterTabs(
    selectedStatus: String?,
    onStatusSelected: (String?) -> Unit
) {
    val tabs = listOf(
        null to "همه",
        "open" to "باز",
        "pending" to "در انتظار",
        "answered" to "پاسخ داده شده",
        "closed" to "بسته شده"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        tabs.forEach { (status, label) ->
            val isSelected = selectedStatus == status
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = if (isSelected) Primary else Color.Gray,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isSelected) Primary100 else Color.Transparent)
                    .clickable { onStatusSelected(status) }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }
}

