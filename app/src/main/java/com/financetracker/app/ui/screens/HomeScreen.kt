package com.financetracker.app.ui.screens

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.financetracker.app.ui.components.AddTransactionDialog
import com.financetracker.app.ui.components.StitchBottomNav
import com.financetracker.app.ui.theme.StitchBackground
import com.financetracker.app.ui.theme.StitchPrimaryContainer
import com.financetracker.app.viewmodel.BottomNavTab
import com.financetracker.app.viewmodel.FinanceViewModel

@Composable
fun HomeScreen(
    viewModel: FinanceViewModel,
    modifier: Modifier = Modifier
) {
    val currentTab by viewModel.currentTab.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            StitchBottomNav(
                currentTab = currentTab,
                onTabSelected = { viewModel.setTab(it) }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = StitchPrimaryContainer,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Transaction",
                    modifier = Modifier.padding(14.dp)
                )
            }
        },
        containerColor = StitchBackground,
        modifier = modifier
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Crossfade(targetState = currentTab, label = "tabTransition") { tab ->
                when (tab) {
                    BottomNavTab.DASHBOARD -> DashboardScreen(viewModel = viewModel)
                    BottomNavTab.LEDGER -> LedgerScreen(viewModel = viewModel)
                    BottomNavTab.BUDGETS -> BudgetsScreen(viewModel = viewModel)
                    BottomNavTab.ANALYTICS -> AnalyticsScreen(viewModel = viewModel)
                }
            }
        }

        if (showAddDialog) {
            AddTransactionDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = { amount, type, note ->
                    viewModel.addTransaction(amount, type, note)
                    showAddDialog = false
                }
            )
        }
    }
}
