package com.financetracker.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val StitchLightColorScheme = lightColorScheme(
    primary = StitchPrimary,
    onPrimary = Color.White,
    primaryContainer = StitchPrimaryContainer,
    onPrimaryContainer = Color.White,
    secondary = StitchInvested,
    onSecondary = Color.White,
    secondaryContainer = StitchActivePill,
    onSecondaryContainer = StitchOnActivePill,
    error = StitchSpent,
    onError = Color.White,
    errorContainer = StitchSpentContainer,
    onErrorContainer = StitchOnSpentContainer,
    background = StitchBackground,
    onBackground = StitchOnSurface,
    surface = StitchCardLowest,
    onSurface = StitchOnSurface,
    surfaceVariant = StitchContainerLow,
    onSurfaceVariant = StitchOnSurfaceVariant
)

@Composable
fun SimpleFinanceTrackerTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = StitchLightColorScheme,
        content = content
    )
}
