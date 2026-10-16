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
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import com.financetracker.app.ui.theme.StitchIncome
import com.financetracker.app.ui.theme.StitchIncomeContainer
import com.financetracker.app.ui.theme.StitchInvested
import com.financetracker.app.ui.theme.StitchInvestedContainer
import com.financetracker.app.ui.theme.StitchOnSurface
import com.financetracker.app.ui.theme.StitchOnSurfaceVariant
import com.financetracker.app.ui.theme.StitchSpent
import com.financetracker.app.ui.theme.StitchSpentContainer
import com.financetracker.app.viewmodel.FinanceViewModel
import java.util.Locale

@Composable
fun AnalyticsScreen(
    viewModel: FinanceViewModel,
    modifier: Modifier = Modifier
) {
    val summary by viewModel.summary.collectAsState()
    val totalIn = summary.totalIncome
    val totalOut = summary.totalExpense + summary.totalInvestment
    val netCashFlow = totalIn - totalOut

    val savingsRate = if (totalIn > 0) {
        (((summary.totalInvestment + (totalIn - totalOut).coerceAtLeast(0.0)) / totalIn) * 100).toInt()
    } else 0

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(StitchBackground)
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Financial Analytics",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = StitchOnSurface
                )
                Text(
                    text = "Cash flow health & savings rate",
                    fontSize = 12.sp,
                    color = StitchOnSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Net Cash Flow Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = StitchCardLowest),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "Net Cash Flow",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = StitchOnSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = (if (netCashFlow >= 0) "+ ₹" else "- ₹") + String.format(Locale.US, "%,.0f", kotlin.math.abs(netCashFlow)),
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (netCashFlow >= 0) StitchInvested else StitchSpent
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (netCashFlow >= 0) "Positive inflow this period" else "Outflows exceeded incoming money",
                    fontSize = 12.sp,
                    color = StitchOnSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Savings & Wealth Rate Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = StitchCardLowest),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(StitchInvestedContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.TrendingUp,
                        contentDescription = null,
                        tint = StitchInvested,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Wealth Creation Rate",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = StitchOnSurface
                    )
                    Text(
                        text = "$savingsRate% of money retained or invested",
                        fontSize = 12.sp,
                        color = StitchOnSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Category Totals Summary
        Text(
            text = "Period Breakdown",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = StitchOnSurface
        )

        Spacer(modifier = Modifier.height(10.dp))

        AnalyticsRow(
            label = "Money Received (Inflow)",
            amount = "₹" + String.format(Locale.US, "%,.0f", summary.totalIncome),
            color = StitchIncome
        )

        Spacer(modifier = Modifier.height(6.dp))

        AnalyticsRow(
            label = "Investments Made",
            amount = "₹" + String.format(Locale.US, "%,.0f", summary.totalInvestment),
            color = StitchInvested
        )

        Spacer(modifier = Modifier.height(6.dp))

        AnalyticsRow(
            label = "Total Expenses (Outflows)",
            amount = "₹" + String.format(Locale.US, "%,.0f", summary.totalExpense),
            color = StitchSpent
        )
    }
}

@Composable
private fun AnalyticsRow(
    label: String,
    amount: String,
    color: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = StitchCardLowest)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(color))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = label, fontSize = 13.sp, color = StitchOnSurface)
            }
            Text(text = amount, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = color)
        }
    }
}
