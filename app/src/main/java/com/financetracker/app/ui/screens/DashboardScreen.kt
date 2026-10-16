package com.financetracker.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.financetracker.app.model.TimeFilter
import com.financetracker.app.model.Transaction
import com.financetracker.app.model.TransactionType
import com.financetracker.app.ui.components.SummaryCard
import com.financetracker.app.ui.theme.StitchBackground
import com.financetracker.app.ui.theme.StitchCardLowest
import com.financetracker.app.ui.theme.StitchContainerLow
import com.financetracker.app.ui.theme.StitchIncome
import com.financetracker.app.ui.theme.StitchIncomeContainer
import com.financetracker.app.ui.theme.StitchInvested
import com.financetracker.app.ui.theme.StitchInvestedContainer
import com.financetracker.app.ui.theme.StitchOnSurface
import com.financetracker.app.ui.theme.StitchOnSurfaceVariant
import com.financetracker.app.ui.theme.StitchPrimary
import com.financetracker.app.ui.theme.StitchPrimaryContainer
import com.financetracker.app.ui.theme.StitchSpent
import com.financetracker.app.ui.theme.StitchSpentContainer
import com.financetracker.app.viewmodel.BottomNavTab
import com.financetracker.app.viewmodel.FinanceViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(
    viewModel: FinanceViewModel,
    modifier: Modifier = Modifier
) {
    val selectedFilter by viewModel.selectedFilter.collectAsState()
    val summary by viewModel.summary.collectAsState()
    val transactions by viewModel.transactions.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(StitchBackground)
            .padding(horizontal = 16.dp)
    ) {
        // Stitch Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "FINANCE TRACKER",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp,
                    color = StitchInvested
                )
                Text(
                    text = "Dashboard",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = StitchOnSurface
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(StitchContainerLow)
                        .clickable { viewModel.setTab(BottomNavTab.LEDGER) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = StitchOnSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(StitchPrimaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // Time Filter Chips (Stitch pill style)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TimeFilter.values().forEach { filter ->
                val isSelected = filter == selectedFilter
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50.dp))
                        .background(if (isSelected) StitchPrimaryContainer else StitchContainerLow)
                        .clickable { viewModel.setFilter(filter) }
                        .padding(horizontal = 16.dp, vertical = 7.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = StitchInvestedContainer,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        Text(
                            text = filter.label,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                            color = if (isSelected) Color.White else StitchOnSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Stitch Summary Card with 3 metrics (Invested, Spent, Received)
        SummaryCard(summary = summary)

        Spacer(modifier = Modifier.height(16.dp))

        // Recent Activity Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recent Activity",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = StitchOnSurface
            )
            TextButton(onClick = { viewModel.setTab(BottomNavTab.LEDGER) }) {
                Text(
                    text = "View All",
                    fontSize = 13.sp,
                    color = StitchInvested,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        if (transactions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "No records for ${selectedFilter.label.lowercase()}",
                        fontSize = 14.sp,
                        color = StitchOnSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Tap '+' to add a transaction",
                        fontSize = 12.sp,
                        color = StitchOnSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(transactions.take(8), key = { it.id }) { item ->
                    TransactionItemView(
                        transaction = item,
                        onDelete = { viewModel.deleteTransaction(item) }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun TransactionItemView(
    transaction: Transaction,
    onDelete: () -> Unit
) {
    val (tintColor, containerBg, prefixSign) = when (transaction.type) {
        TransactionType.INCOME -> Triple(StitchIncome, StitchIncomeContainer, "+ ")
        TransactionType.INVESTMENT -> Triple(StitchInvested, StitchInvestedContainer, "+ ")
        TransactionType.EXPENSE -> Triple(StitchSpent, StitchSpentContainer, "- ")
    }

    val formattedDate = remember(transaction.timestamp) {
        val sdf = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
        sdf.format(Date(transaction.timestamp))
    }
    val amountFormatted = "₹" + String.format(Locale.US, "%,.0f", transaction.amount)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = StitchCardLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(containerBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (transaction.type) {
                        TransactionType.INCOME -> Icons.Default.Payments
                        TransactionType.INVESTMENT -> Icons.Default.ArrowUpward
                        TransactionType.EXPENSE -> Icons.Default.ArrowDownward
                    },
                    contentDescription = null,
                    tint = tintColor,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (transaction.note.isNotBlank()) transaction.note else transaction.type.displayName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = StitchOnSurface
                )
                Text(
                    text = formattedDate,
                    fontSize = 11.sp,
                    color = StitchOnSurfaceVariant
                )
            }

            Text(
                text = prefixSign + amountFormatted,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = tintColor
            )

            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Delete",
                    tint = StitchOnSurfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
