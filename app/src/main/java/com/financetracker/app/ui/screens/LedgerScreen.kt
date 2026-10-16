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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.financetracker.app.model.TransactionType
import com.financetracker.app.ui.theme.StitchBackground
import com.financetracker.app.ui.theme.StitchCardLowest
import com.financetracker.app.ui.theme.StitchContainerLow
import com.financetracker.app.ui.theme.StitchIncome
import com.financetracker.app.ui.theme.StitchIncomeContainer
import com.financetracker.app.ui.theme.StitchInvested
import com.financetracker.app.ui.theme.StitchInvestedContainer
import com.financetracker.app.ui.theme.StitchOnSurface
import com.financetracker.app.ui.theme.StitchOnSurfaceVariant
import com.financetracker.app.ui.theme.StitchOutlineVariant
import com.financetracker.app.ui.theme.StitchPrimary
import com.financetracker.app.ui.theme.StitchPrimaryContainer
import com.financetracker.app.ui.theme.StitchSpent
import com.financetracker.app.ui.theme.StitchSpentContainer
import com.financetracker.app.viewmodel.FinanceViewModel

@Composable
fun LedgerScreen(
    viewModel: FinanceViewModel,
    modifier: Modifier = Modifier
) {
    val allTransactions by viewModel.allTransactions.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf<TransactionType?>(null) }

    val filteredList = remember(allTransactions, searchQuery, selectedCategoryFilter) {
        allTransactions.filter { item ->
            val matchesCategory = selectedCategoryFilter == null || item.type == selectedCategoryFilter
            val matchesSearch = searchQuery.isBlank() ||
                item.note.contains(searchQuery, ignoreCase = true) ||
                item.type.displayName.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesSearch
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(StitchBackground)
            .padding(horizontal = 16.dp)
    ) {
        // Stitch Ledger Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Ledger Feed",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = StitchOnSurface
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(StitchContainerLow)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${filteredList.size} total",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = StitchInvested
                        )
                    }
                }
                Text(
                    text = "Track and reconcile all transactions",
                    fontSize = 12.sp,
                    color = StitchOnSurfaceVariant
                )
            }
        }

        // Search Bar (Exact Stitch Style)
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search description, category...", fontSize = 13.sp, color = StitchOnSurfaceVariant) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = StitchOnSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            },
            trailingIcon = {
                if (searchQuery.isNotBlank()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear",
                            tint = StitchOnSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = StitchOnSurface,
                unfocusedTextColor = StitchOnSurface,
                focusedContainerColor = StitchCardLowest,
                unfocusedContainerColor = StitchCardLowest,
                focusedBorderColor = StitchPrimary,
                unfocusedBorderColor = Color.Transparent
            ),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Quick Category Filter Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // "All" chip
            val isAll = selectedCategoryFilter == null
            CategoryChip(
                label = "All",
                isSelected = isAll,
                onClick = { selectedCategoryFilter = null }
            )

            // "Received" chip
            CategoryChip(
                label = "Received",
                isSelected = selectedCategoryFilter == TransactionType.INCOME,
                onClick = { selectedCategoryFilter = TransactionType.INCOME }
            )

            // "Invested" chip
            CategoryChip(
                label = "Invested",
                isSelected = selectedCategoryFilter == TransactionType.INVESTMENT,
                onClick = { selectedCategoryFilter = TransactionType.INVESTMENT }
            )

            // "Spent" chip
            CategoryChip(
                label = "Spent",
                isSelected = selectedCategoryFilter == TransactionType.EXPENSE,
                onClick = { selectedCategoryFilter = TransactionType.EXPENSE }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (filteredList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "No matching ledger records",
                        fontSize = 14.sp,
                        color = StitchOnSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Use '+' to record an entry",
                        fontSize = 12.sp,
                        color = StitchOnSurfaceVariant.copy(alpha = 0.6f)
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
                items(filteredList, key = { it.id }) { item ->
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
private fun CategoryChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50.dp))
            .background(if (isSelected) StitchPrimaryContainer else StitchCardLowest)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
            color = if (isSelected) Color.White else StitchOnSurfaceVariant
        )
    }
}
