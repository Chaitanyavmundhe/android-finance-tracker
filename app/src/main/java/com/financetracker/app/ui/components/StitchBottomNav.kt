package com.financetracker.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.financetracker.app.ui.theme.StitchActivePill
import com.financetracker.app.ui.theme.StitchCardLowest
import com.financetracker.app.ui.theme.StitchOnActivePill
import com.financetracker.app.ui.theme.StitchOnSurfaceVariant
import com.financetracker.app.ui.theme.StitchOutlineVariant
import com.financetracker.app.viewmodel.BottomNavTab

@Composable
fun StitchBottomNav(
    currentTab: BottomNavTab,
    onTabSelected: (BottomNavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = StitchCardLowest.copy(alpha = 0.95f),
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavItem(
                    label = "Dashboard",
                    icon = Icons.Default.AccountBalanceWallet,
                    isSelected = currentTab == BottomNavTab.DASHBOARD,
                    onClick = { onTabSelected(BottomNavTab.DASHBOARD) }
                )

                NavItem(
                    label = "Ledger",
                    icon = Icons.Default.ReceiptLong,
                    isSelected = currentTab == BottomNavTab.LEDGER,
                    onClick = { onTabSelected(BottomNavTab.LEDGER) }
                )

                NavItem(
                    label = "Budgets",
                    icon = Icons.Default.PieChart,
                    isSelected = currentTab == BottomNavTab.BUDGETS,
                    onClick = { onTabSelected(BottomNavTab.BUDGETS) }
                )

                NavItem(
                    label = "Analytics",
                    icon = Icons.Default.Insights,
                    isSelected = currentTab == BottomNavTab.ANALYTICS,
                    onClick = { onTabSelected(BottomNavTab.ANALYTICS) }
                )
            }

            // Bottom subtle indicator bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .width(96.dp)
                        .height(3.dp)
                        .clip(CircleShape)
                        .background(StitchOutlineVariant.copy(alpha = 0.4f))
                )
            }
        }
    }
}

@Composable
private fun NavItem(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val pillBg by animateColorAsState(
        targetValue = if (isSelected) StitchActivePill else Color.Transparent,
        label = "pillBg"
    )
    val iconColor by animateColorAsState(
        targetValue = if (isSelected) StitchOnActivePill else StitchOnSurfaceVariant,
        label = "iconColor"
    )

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .width(56.dp)
                .height(30.dp)
                .clip(CircleShape)
                .background(pillBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = iconColor,
                modifier = Modifier.size(20.dp)
            )
        }
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = iconColor,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}
