package com.damiandantas.daylighthabits.ui.app

import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.damiandantas.daylighthabits.ui.screen.alert.AlertScreen
import com.damiandantas.daylighthabits.ui.theme.AppTheme

private const val ALERT = "alert"
private const val FORECAST = "forecast"
private const val SETTINGS = "settings"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScreen() {
    val navController = rememberNavController()

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        ColorSystemUi()

        Scaffold(
            bottomBar = {
                AppNavigationBar(0) { index ->
                    when (index) {
                        0 -> navController.navigate(ALERT)
                        1 -> navController.navigate(FORECAST)
                        2 -> navController.navigate(SETTINGS)
                    }
                }
            },
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = ALERT,
                modifier = Modifier.padding(paddingValues)
            ) {
                composable(ALERT) {
                    AlertScreen()
                }
                composable(FORECAST) {
                    Text(text = "Forecast")
                }
                composable(SETTINGS) {
                    Text(text = "Settings")
                }
            }
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun AppScreenPreview() {
    AppTheme {
        AppScreen()
    }
}