package com.damiandantas.daylighthabits.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun ColorSystemUi() {
    val systemUiController = rememberSystemUiController()
    val useDarkIcons = !isSystemInDarkTheme()
    val statusBarColor = MaterialTheme.colorScheme.background
    val navigationBarColor =
        MaterialTheme.colorScheme.surfaceColorAtElevation(NavigationBarDefaults.Elevation)

    DisposableEffect(systemUiController, useDarkIcons, statusBarColor) {
        systemUiController.setStatusBarColor(statusBarColor, useDarkIcons)
        systemUiController.setNavigationBarColor(navigationBarColor, useDarkIcons)

        onDispose {}
    }
}