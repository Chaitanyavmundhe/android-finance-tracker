package com.financetracker.app.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.financetracker.app.ui.theme.StitchBackground
import com.financetracker.app.ui.theme.StitchCardLowest
import com.financetracker.app.ui.theme.StitchContainerLow
import com.financetracker.app.ui.theme.StitchErrorContainer
import com.financetracker.app.ui.theme.StitchInvested
import com.financetracker.app.ui.theme.StitchInvestedContainer
import com.financetracker.app.ui.theme.StitchOnSurface
import com.financetracker.app.ui.theme.StitchOnSurfaceVariant
import com.financetracker.app.ui.theme.StitchSpent
import com.financetracker.app.viewmodel.FinanceViewModel
import java.util.Locale

@Composable
fun BudgetsScreen(
    viewModel: FinanceViewModel,
    modifier: Modifier = Modifier
) {
    val summary by viewModel.summary.collectAsState()
    val monthlyBudget = 50000.0 // Default suggested baseline budget limit
    val spentRatio = (summary.totalExpense / monthlyBudget).toFloat().coerceIn(0f, 1f)
    val remaining = (monthlyBudget - summary.totalExpense).coerceAtLeast(0.0)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(StitchBackground)
            .padding(16.dp)
    ) {
        // Stitch Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Budgets & Limits",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = StitchOnSurface
                )
                Text(
                    text = "Monthly expenditure caps & targets",
                    fontSize = 12.sp,
                    color = StitchOnSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Main Budget Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = StitchCardLowest),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Monthly Spending Cap",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = StitchOnSurface
                    )
                    Text(
                        text = "${(spentRatio * 100).toInt()}% used",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (spentRatio > 0.8f) StitchSpent else StitchInvested
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Progress Bar
                LinearProgressIndicator(
                    progress = spentRatio,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(CircleShape),
                    color = if (spentRatio > 0.85f) StitchSpent else StitchInvested,
                    trackColor = StitchContainerLow
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = "Spent this month", fontSize = 11.sp, color = StitchOnSurfaceVariant)
                        Text(
                            text = "₹" + String.format(Locale.US, "%,.0f", summary.totalExpense),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = StitchSpent
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "Remaining allowance", fontSize = 11.sp, color = StitchOnSurfaceVariant)
                        Text(
                            text = "₹" + String.format(Locale.US, "%,.0f", remaining),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = StitchInvested
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Budget Advice & Insights
        Text(
            text = "Healthy Spending Tips",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = StitchOnSurface
        )

        Spacer(modifier = Modifier.height(10.dp))

        TipCard(
            title = "50/30/20 Rule",
            description = "Aim to allocate 50% for Needs (Expenses), 20% for Wealth (Investments), and save the rest.",
            iconColor = StitchInvested,
            iconBg = StitchInvestedContainer
        )

        Spacer(modifier = Modifier.height(8.dp))

        TipCard(
            title = "Investment Ratio",
            description = if (summary.totalInvestment >= summary.totalExpense) {
                "Great work! Your investments are outpacing your monthly expenses."
            } else {
                "Tip: Try boosting your monthly investments to match or exceed living expenses."
            },
            iconColor = StitchSpent,
            iconBg = StitchErrorContainer
        )
    }
}

@Composable
private fun TipCard(
    title: String,
    description: String,
    iconColor: Color,
    iconBg: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = StitchCardLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.Top) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PieChart,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = StitchOnSurface)
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = description, fontSize = 11.sp, color = StitchOnSurfaceVariant, lineHeight = 16.sp)
            }
        }
    }
}
