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
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.financetracker.app.BuildConfig
import com.financetracker.app.model.TimeFilter
import com.financetracker.app.model.Transaction
import com.financetracker.app.model.TransactionType
import com.financetracker.app.ui.components.SummaryCard
import com.financetracker.app.ui.components.UpdateDialog
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
import com.financetracker.app.data.sync.SyncStatus
import com.financetracker.app.updater.UpdateStatus
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
    val context = LocalContext.current
    val selectedFilter by viewModel.selectedFilter.collectAsState()
    val summary by viewModel.summary.collectAsState()
    val transactions by viewModel.transactions.collectAsState()
    val updateStatus by viewModel.updateStatus.collectAsState()
    val syncStatus by viewModel.syncStatus.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val isGoogleUser = currentUser != null && !currentUser!!.isAnonymous
    var showProfileDialog by remember { mutableStateOf(false) }

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val idToken = account?.idToken
            if (idToken != null) {
                viewModel.handleGoogleSignInResult(idToken) { success ->
                    if (success) {
                        Toast.makeText(context, "Signed in as ${account.email}", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Google Sign-In failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Sign-in error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }

    // In-App Update Dialog
    UpdateDialog(
        status = updateStatus,
        onDismiss = { viewModel.dismissUpdateDialog() },
        onUpdateClick = { info -> viewModel.downloadAndInstallUpdate(context, info) }
    )

    // Profile & App Settings Dialog
    if (showProfileDialog) {
        AlertDialog(
            onDismissRequest = { showProfileDialog = false },
            containerColor = StitchCardLowest,
            shape = RoundedCornerShape(24.dp),
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(if (isGoogleUser) StitchIncomeContainer else StitchPrimaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isGoogleUser) {
                                (currentUser?.displayName ?: currentUser?.email ?: "U").take(1).uppercase()
                            } else {
                                "G"
                            },
                            fontWeight = FontWeight.Bold,
                            color = if (isGoogleUser) StitchIncome else Color.White,
                            fontSize = 16.sp
                        )
                    }
                    Column {
                        Text(
                            text = if (isGoogleUser) (currentUser?.displayName ?: "Google User") else "Finance Tracker",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = StitchOnSurface
                        )
                        Text(
                            text = if (isGoogleUser) (currentUser?.email ?: "") else "Version v${BuildConfig.VERSION_NAME}",
                            fontSize = 12.sp,
                            color = StitchOnSurfaceVariant
                        )
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Google Account Card
                    Card(
                        colors = CardDefaults.cardColors(containerColor = StitchContainerLow),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            if (isGoogleUser) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "Google Account",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = StitchOnSurface
                                        )
                                        Text(
                                            text = "Connected & Backed Up",
                                            fontSize = 12.sp,
                                            color = StitchIncome
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                androidx.compose.material3.OutlinedButton(
                                    onClick = { viewModel.signOut(context) },
                                    shape = RoundedCornerShape(50.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Sign Out", fontSize = 12.sp, color = StitchSpent)
                                }
                            } else {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AccountCircle,
                                        contentDescription = null,
                                        tint = StitchInvested,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = "Google Sign-In",
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 14.sp,
                                        color = StitchOnSurface
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Sign in with Google to backup your transactions across all your devices.",
                                    fontSize = 12.sp,
                                    color = StitchOnSurfaceVariant,
                                    lineHeight = 16.sp
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Button(
                                    onClick = {
                                        val intent = viewModel.getGoogleSignInIntent(context)
                                        if (intent != null) {
                                            googleSignInLauncher.launch(intent)
                                        } else {
                                            Toast.makeText(context, "Google Sign-In not ready", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = StitchPrimary),
                                    shape = RoundedCornerShape(50.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Sign in with Google", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                            }
                        }
                    }

                    // Update check card
                    Card(
                        colors = CardDefaults.cardColors(containerColor = StitchContainerLow),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.SystemUpdate,
                                        contentDescription = null,
                                        tint = StitchInvested,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = "In-App Updates",
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 14.sp,
                                        color = StitchOnSurface
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Instantly download & install new releases from GitHub without losing your data.",
                                fontSize = 12.sp,
                                color = StitchOnSurfaceVariant,
                                lineHeight = 16.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = {
                                    showProfileDialog = false
                                    viewModel.checkForUpdates(silent = false)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = StitchPrimary),
                                shape = RoundedCornerShape(50.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Check for Updates", color = Color.White, fontSize = 13.sp)
                            }
                        }
                    }

                    // Cloud Sync Card (Firebase Firestore)
                    Card(
                        colors = CardDefaults.cardColors(containerColor = StitchContainerLow),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CloudSync,
                                        contentDescription = null,
                                        tint = StitchIncome,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = "Cloud Sync (Firebase)",
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 14.sp,
                                        color = StitchOnSurface
                                    )
                                }

                                when (syncStatus) {
                                    is SyncStatus.Syncing -> {
                                        androidx.compose.material3.CircularProgressIndicator(
                                            modifier = Modifier.size(16.dp),
                                            strokeWidth = 2.dp,
                                            color = StitchIncome
                                        )
                                    }
                                    is SyncStatus.Synced -> {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(50.dp))
                                                .background(StitchIncomeContainer)
                                                .padding(horizontal = 8.dp, vertical = 2.dp)
                                        ) {
                                            Text("Active", fontSize = 11.sp, color = StitchIncome, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                    is SyncStatus.Error -> {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(50.dp))
                                                .background(StitchSpentContainer)
                                                .padding(horizontal = 8.dp, vertical = 2.dp)
                                        ) {
                                            Text("Offline", fontSize = 11.sp, color = StitchSpent, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                    SyncStatus.Idle -> {}
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            val statusDescription = when (val s = syncStatus) {
                                is SyncStatus.Syncing -> "Syncing transactions with Firestore..."
                                is SyncStatus.Synced -> "Synced (${s.count} transactions backed up)"
                                is SyncStatus.Error -> "Offline / Local mode (${s.message})"
                                SyncStatus.Idle -> "Transactions backed up to Google Cloud automatically."
                            }
                            Text(
                                text = statusDescription,
                                fontSize = 12.sp,
                                color = StitchOnSurfaceVariant,
                                lineHeight = 16.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            androidx.compose.material3.OutlinedButton(
                                onClick = { viewModel.triggerSync() },
                                shape = RoundedCornerShape(50.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Sync Now", fontSize = 13.sp, color = StitchOnSurface)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showProfileDialog = false }) {
                    Text("Close", color = StitchPrimary, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

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
                // Search Button
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

                // Check Updates Quick Button
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(if (updateStatus is UpdateStatus.UpdateAvailable) StitchIncomeContainer else StitchContainerLow)
                        .clickable { viewModel.checkForUpdates(silent = false) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SystemUpdate,
                        contentDescription = "Check for Updates",
                        tint = if (updateStatus is UpdateStatus.UpdateAvailable) StitchIncome else StitchOnSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Profile / Settings Button
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(StitchPrimaryContainer)
                        .clickable { showProfileDialog = true },
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

        // New Update Banner if available
        if (updateStatus is UpdateStatus.UpdateAvailable) {
            val updateInfo = (updateStatus as UpdateStatus.UpdateAvailable).info
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { /* dialog already handled */ },
                colors = CardDefaults.cardColors(containerColor = StitchIncomeContainer),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudDownload,
                        contentDescription = null,
                        tint = StitchIncome,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "New Update ${updateInfo.latestVersion} Available!",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = StitchIncome
                        )
                        Text(
                            text = "Tap to install in 1 click",
                            fontSize = 11.sp,
                            color = StitchOnSurfaceVariant
                        )
                    }
                    Button(
                        onClick = { viewModel.downloadAndInstallUpdate(context, updateInfo) },
                        colors = ButtonDefaults.buttonColors(containerColor = StitchIncome),
                        shape = RoundedCornerShape(50.dp)
                    ) {
                        Text("Install", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
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
