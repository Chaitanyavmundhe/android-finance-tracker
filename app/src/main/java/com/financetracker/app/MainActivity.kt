package com.financetracker.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.financetracker.app.ui.screens.HomeScreen
import com.financetracker.app.ui.screens.SplashScreen
import com.financetracker.app.ui.theme.SimpleFinanceTrackerTheme
import com.financetracker.app.ui.theme.StitchBackground
import com.financetracker.app.viewmodel.FinanceViewModel
import com.financetracker.app.viewmodel.FinanceViewModelFactory

class MainActivity : ComponentActivity() {

    private val viewModel: FinanceViewModel by viewModels {
        val app = application as FinanceApp
        FinanceViewModelFactory(app.repository, app.syncManager)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SimpleFinanceTrackerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = StitchBackground
                ) {
                    var isSplashVisible by remember { mutableStateOf(true) }

                    Crossfade(
                        targetState = isSplashVisible,
                        animationSpec = tween(durationMillis = 400),
                        label = "splashTransition"
                    ) { showSplash ->
                        if (showSplash) {
                            SplashScreen(
                                onSplashFinished = {
                                    isSplashVisible = false
                                }
                            )
                        } else {
                            HomeScreen(viewModel = viewModel)
                        }
                    }
                }
            }
        }
    }
}
