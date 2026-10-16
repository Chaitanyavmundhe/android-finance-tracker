package com.financetracker.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.financetracker.app.model.TransactionType
import com.financetracker.app.ui.theme.StitchCardLowest
import com.financetracker.app.ui.theme.StitchContainerLow
import com.financetracker.app.ui.theme.StitchIncome
import com.financetracker.app.ui.theme.StitchInvested
import com.financetracker.app.ui.theme.StitchOnSurface
import com.financetracker.app.ui.theme.StitchOnSurfaceVariant
import com.financetracker.app.ui.theme.StitchOutlineVariant
import com.financetracker.app.ui.theme.StitchPrimary
import com.financetracker.app.ui.theme.StitchSpent

@Composable
fun AddTransactionDialog(
    onDismiss: () -> Unit,
    onConfirm: (amount: Double, type: TransactionType, note: String) -> Unit
) {
    var amountText by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(TransactionType.EXPENSE) }
    var noteText by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = StitchCardLowest,
        titleContentColor = StitchOnSurface,
        textContentColor = StitchOnSurfaceVariant,
        shape = RoundedCornerShape(20.dp),
        title = {
            Text(
                text = "New Transaction",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = StitchOnSurface
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // 3 Category Tabs: Received / Invested / Spent
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(StitchContainerLow)
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    TransactionType.values().forEach { type ->
                        val isSelected = selectedType == type
                        val activeColor = when (type) {
                            TransactionType.INCOME -> StitchIncome
                            TransactionType.INVESTMENT -> StitchInvested
                            TransactionType.EXPENSE -> StitchSpent
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) activeColor else Color.Transparent)
                                .clickable { selectedType = type }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = type.displayName,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 12.sp,
                                color = if (isSelected) Color.White else StitchOnSurfaceVariant
                            )
                        }
                    }
                }

                // Amount Field
                OutlinedTextField(
                    value = amountText,
                    onValueChange = {
                        amountText = it
                        errorMessage = null
                    },
                    label = { Text("Amount") },
                    prefix = { Text("₹ ", color = StitchOnSurface, fontWeight = FontWeight.Bold) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = StitchOnSurface,
                        unfocusedTextColor = StitchOnSurface,
                        focusedBorderColor = StitchPrimary,
                        unfocusedBorderColor = StitchOutlineVariant,
                        focusedLabelColor = StitchPrimary,
                        unfocusedLabelColor = StitchOnSurfaceVariant
                    ),
                    isError = errorMessage != null,
                    modifier = Modifier.fillMaxWidth()
                )

                if (errorMessage != null) {
                    Text(
                        text = errorMessage ?: "",
                        color = StitchSpent,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                // Note Field
                OutlinedTextField(
                    value = noteText,
                    onValueChange = { noteText = it },
                    label = { Text("Note / Description (Optional)") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = StitchOnSurface,
                        unfocusedTextColor = StitchOnSurface,
                        focusedBorderColor = StitchPrimary,
                        unfocusedBorderColor = StitchOutlineVariant,
                        focusedLabelColor = StitchPrimary,
                        unfocusedLabelColor = StitchOnSurfaceVariant
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            val buttonColor = when (selectedType) {
                TransactionType.INCOME -> StitchIncome
                TransactionType.INVESTMENT -> StitchInvested
                TransactionType.EXPENSE -> StitchSpent
            }
            Button(
                onClick = {
                    val amount = amountText.toDoubleOrNull()
                    if (amount == null || amount <= 0.0) {
                        errorMessage = "Please enter a valid amount"
                        return@Button
                    }
                    onConfirm(amount, selectedType, noteText)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = buttonColor,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(text = "Add Entry", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = StitchOnSurfaceVariant)
            }
        }
    )
}
