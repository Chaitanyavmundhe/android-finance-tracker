package com.financetracker.app.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.financetracker.app.ui.theme.StitchCardLowest
import com.financetracker.app.ui.theme.StitchContainerLow
import com.financetracker.app.ui.theme.StitchErrorContainer
import com.financetracker.app.ui.theme.StitchIncome
import com.financetracker.app.ui.theme.StitchIncomeContainer
import com.financetracker.app.ui.theme.StitchInvested
import com.financetracker.app.ui.theme.StitchInvestedContainer
import com.financetracker.app.ui.theme.StitchOnInvestedContainer
import com.financetracker.app.ui.theme.StitchOnSpentContainer
import com.financetracker.app.ui.theme.StitchOnSurface
import com.financetracker.app.ui.theme.StitchOnSurfaceVariant
import com.financetracker.app.ui.theme.StitchSpent
import com.financetracker.app.viewmodel.FinanceSummary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SummaryCard(
    summary: FinanceSummary,
    modifier: Modifier = Modifier,
    isCloudSynced: Boolean = false
) {
    val total = summary.totalInvestment + summary.totalExpense + summary.totalIncome
    val investPercent = if (total > 0) ((summary.totalInvestment / total) * 100).toInt() else 0
    val expensePercent = if (total > 0) ((summary.totalExpense / total) * 100).toInt() else 0
    val incomePercent = if (total > 0) ((summary.totalIncome / total) * 100).toInt() else 0

    val investRatio = if (total > 0) (summary.totalInvestment / total).toFloat() else 0.33f
    val expenseRatio = if (total > 0) (summary.totalExpense / total).toFloat() else 0.33f
    val incomeRatio = if (total > 0) (summary.totalIncome / total).toFloat() else 0.34f

    val investFormatted = "₹" + String.format(Locale.US, "%,.0f", summary.totalInvestment)
    val expenseFormatted = "₹" + String.format(Locale.US, "%,.0f", summary.totalExpense)
    val incomeFormatted = "₹" + String.format(Locale.US, "%,.0f", summary.totalIncome)

    val budget = summary.budget
    val budgetFormatted = if (budget < 0) {
        "-₹" + String.format(Locale.US, "%,.0f", Math.abs(budget))
    } else {
        "₹" + String.format(Locale.US, "%,.0f", budget)
    }

    val todayDateFormatted = SimpleDateFormat("EEEE, dd MMM", Locale.getDefault()).format(Date())

    // Pulsing live sync animation
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = StitchCardLowest
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            // Header: Date and Live Sync Badge (Exact Stitch Layout)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "OVERVIEW",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = StitchInvested
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = todayDateFormatted,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = StitchOnSurface
                    )
                }

                Row(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(StitchContainerLow)
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(
                                if (isCloudSynced) StitchInvested.copy(alpha = pulseAlpha)
                                else StitchOnSurfaceVariant.copy(alpha = 0.5f)
                            )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isCloudSynced) "Auto Synced" else "Local Storage",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = StitchOnSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 4-Metric 2x2 Grid (Budget, Received, Spent, Invested)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Budget Metric (Received - Spent - Invested)
                MetricBox(
                    modifier = Modifier.weight(1f),
                    title = "Budget",
                    amount = budgetFormatted,
                    badgeText = if (budget >= 0) "Net" else "Deficit",
                    badgeBg = if (budget >= 0) StitchIncomeContainer else StitchErrorContainer,
                    badgeTextCol = if (budget >= 0) StitchIncome else StitchSpent,
                    icon = Icons.Default.AccountBalanceWallet
                )

                // Received Metric
                MetricBox(
                    modifier = Modifier.weight(1f),
                    title = "Received",
                    amount = incomeFormatted,
                    badgeText = "$incomePercent%",
                    badgeBg = StitchIncomeContainer,
                    badgeTextCol = StitchIncome,
                    icon = Icons.Default.Payments
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Spent Metric
                MetricBox(
                    modifier = Modifier.weight(1f),
                    title = "Spent",
                    amount = expenseFormatted,
                    badgeText = "$expensePercent%",
                    badgeBg = StitchErrorContainer,
                    badgeTextCol = StitchOnSpentContainer,
                    icon = Icons.Default.ArrowDownward
                )

                // Invested Metric
                MetricBox(
                    modifier = Modifier.weight(1f),
                    title = "Invested",
                    amount = investFormatted,
                    badgeText = "$investPercent%",
                    badgeBg = StitchInvestedContainer,
                    badgeTextCol = StitchOnInvestedContainer,
                    icon = Icons.Default.ArrowUpward
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Formula indicator: budget = recieved - spent - invested
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(StitchContainerLow)
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Budget = Received − Spent − Invested",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = StitchOnSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tri-Tone Ratio Progress Bar (Matching Stitch design)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(StitchInvested))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "$investPercent% Invested", fontSize = 11.sp, color = StitchInvested, fontWeight = FontWeight.Medium)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(StitchSpent))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "$expensePercent% Spent", fontSize = 11.sp, color = StitchSpent, fontWeight = FontWeight.Medium)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(StitchIncome))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "$incomePercent% Received", fontSize = 11.sp, color = StitchIncome, fontWeight = FontWeight.Medium)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Progress bar track
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(CircleShape)
                    .background(StitchContainerLow)
            ) {
                if (total > 0) {
                    Box(
                        modifier = Modifier
                            .weight(investRatio.coerceAtLeast(0.04f))
                            .height(8.dp)
                            .background(StitchInvested)
                    )
                    Box(
                        modifier = Modifier
                            .weight(expenseRatio.coerceAtLeast(0.04f))
                            .height(8.dp)
                            .background(StitchSpent)
                    )
                    Box(
                        modifier = Modifier
                            .weight(incomeRatio.coerceAtLeast(0.04f))
                            .height(8.dp)
                            .background(StitchIncome)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .background(StitchContainerLow)
                    )
                }
            }
        }
    }
}

@Composable
private fun MetricBox(
    title: String,
    amount: String,
    badgeText: String,
    badgeBg: Color,
    badgeTextCol: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(StitchContainerLow)
            .padding(10.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = StitchOnSurfaceVariant
                )
                Row(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(badgeBg)
                        .padding(horizontal = 5.dp, vertical = 1.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = badgeTextCol,
                        modifier = Modifier.size(10.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = badgeText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = badgeTextCol
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = amount,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = StitchOnSurface
            )
        }
    }
}
