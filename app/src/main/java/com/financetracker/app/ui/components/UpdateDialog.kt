package com.financetracker.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.financetracker.app.BuildConfig
import com.financetracker.app.ui.theme.StitchCardLowest
import com.financetracker.app.ui.theme.StitchContainerLow
import com.financetracker.app.ui.theme.StitchIncome
import com.financetracker.app.ui.theme.StitchIncomeContainer
import com.financetracker.app.ui.theme.StitchOnSurface
import com.financetracker.app.ui.theme.StitchOnSurfaceVariant
import com.financetracker.app.ui.theme.StitchPrimary
import com.financetracker.app.ui.theme.StitchPrimaryContainer
import com.financetracker.app.ui.theme.StitchSpent
import com.financetracker.app.updater.AppUpdater
import com.financetracker.app.updater.UpdateStatus

@Composable
fun UpdateDialog(
    status: UpdateStatus,
    onDismiss: () -> Unit,
    onUpdateClick: (com.financetracker.app.updater.UpdateInfo) -> Unit
) {
    val context = LocalContext.current

    when (status) {
        is UpdateStatus.Checking -> {
            AlertDialog(
                onDismissRequest = onDismiss,
                containerColor = StitchCardLowest,
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = StitchPrimary,
                            strokeWidth = 2.5.dp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Checking for Updates...",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = StitchOnSurface
                        )
                    }
                },
                text = {
                    Text(
                        text = "Connecting to GitHub Releases...",
                        fontSize = 14.sp,
                        color = StitchOnSurfaceVariant
                    )
                },
                confirmButton = {}
            )
        }

        is UpdateStatus.UpdateAvailable -> {
            val info = status.info
            AlertDialog(
                onDismissRequest = onDismiss,
                containerColor = StitchCardLowest,
                shape = RoundedCornerShape(24.dp),
                icon = {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(StitchPrimaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SystemUpdate,
                            contentDescription = "Update Available",
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                },
                title = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "New Update Available!",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = StitchOnSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50.dp))
                                .background(StitchIncomeContainer)
                                .padding(horizontal = 12.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = info.latestVersion,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = StitchIncome
                            )
                        }
                    }
                },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(StitchContainerLow)
                            .padding(12.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = "What's New:",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = StitchOnSurface
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = info.releaseNotes.ifBlank { "Performance improvements and bug fixes." },
                            fontSize = 13.sp,
                            color = StitchOnSurfaceVariant,
                            lineHeight = 18.sp
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { onUpdateClick(info) },
                        colors = ButtonDefaults.buttonColors(containerColor = StitchPrimary),
                        shape = RoundedCornerShape(50.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudDownload,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Update Now", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = onDismiss) {
                        Text("Later", color = StitchOnSurfaceVariant)
                    }
                }
            )
        }

        is UpdateStatus.Downloading -> {
            val progress = status.progress
            AlertDialog(
                onDismissRequest = {}, // Do not dismiss while downloading
                containerColor = StitchCardLowest,
                shape = RoundedCornerShape(24.dp),
                title = {
                    Text(
                        text = "Downloading Update...",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = StitchOnSurface
                    )
                },
                text = {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        if (progress >= 0f) {
                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = StitchPrimary,
                                trackColor = StitchContainerLow
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "${(progress * 100).toInt()}% downloaded",
                                fontSize = 12.sp,
                                color = StitchOnSurfaceVariant,
                                modifier = Modifier.align(Alignment.End)
                            )
                        } else {
                            LinearProgressIndicator(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = StitchPrimary,
                                trackColor = StitchContainerLow
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "The installer will prompt automatically once the download finishes.",
                            fontSize = 12.sp,
                            color = StitchOnSurfaceVariant
                        )
                    }
                },
                confirmButton = {}
            )
        }

        is UpdateStatus.ReadyToInstall -> {
            AlertDialog(
                onDismissRequest = onDismiss,
                containerColor = StitchCardLowest,
                shape = RoundedCornerShape(24.dp),
                icon = {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = StitchIncome,
                        modifier = Modifier.size(40.dp)
                    )
                },
                title = {
                    Text(
                        text = "Download Complete",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = StitchOnSurface
                    )
                },
                text = {
                    Text(
                        text = "Tap 'Install' to apply the update without losing any data.",
                        fontSize = 14.sp,
                        color = StitchOnSurfaceVariant
                    )
                },
                confirmButton = {
                    Button(
                        onClick = { AppUpdater.installApk(context, status.apkFile) },
                        colors = ButtonDefaults.buttonColors(containerColor = StitchPrimary),
                        shape = RoundedCornerShape(50.dp)
                    ) {
                        Text("Install", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = StitchOnSurfaceVariant)
                    }
                }
            )
        }

        is UpdateStatus.UpToDate -> {
            AlertDialog(
                onDismissRequest = onDismiss,
                containerColor = StitchCardLowest,
                shape = RoundedCornerShape(24.dp),
                icon = {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = StitchIncome,
                        modifier = Modifier.size(36.dp)
                    )
                },
                title = {
                    Text(
                        text = "You're Up to Date!",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = StitchOnSurface
                    )
                },
                text = {
                    Text(
                        text = "You are currently running version v${BuildConfig.VERSION_NAME}. No updates found.",
                        fontSize = 14.sp,
                        color = StitchOnSurfaceVariant
                    )
                },
                confirmButton = {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = StitchPrimary),
                        shape = RoundedCornerShape(50.dp)
                    ) {
                        Text("OK", color = Color.White)
                    }
                }
            )
        }

        is UpdateStatus.Error -> {
            AlertDialog(
                onDismissRequest = onDismiss,
                containerColor = StitchCardLowest,
                shape = RoundedCornerShape(24.dp),
                icon = {
                    Icon(
                        imageVector = Icons.Default.ErrorOutline,
                        contentDescription = null,
                        tint = StitchSpent,
                        modifier = Modifier.size(36.dp)
                    )
                },
                title = {
                    Text(
                        text = "Update Check Failed",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = StitchOnSurface
                    )
                },
                text = {
                    Text(
                        text = status.message,
                        fontSize = 13.sp,
                        color = StitchOnSurfaceVariant
                    )
                },
                confirmButton = {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = StitchPrimary),
                        shape = RoundedCornerShape(50.dp)
                    ) {
                        Text("OK", color = Color.White)
                    }
                }
            )
        }

        is UpdateStatus.Idle -> {
            // Nothing to show
        }
    }
}
