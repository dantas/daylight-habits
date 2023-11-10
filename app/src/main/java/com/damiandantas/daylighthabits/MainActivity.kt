package com.damiandantas.daylighthabits

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.damiandantas.daylighthabits.ui.AppScreen
import com.damiandantas.daylighthabits.ui.composable.LocationPermissionDialog
import com.damiandantas.daylighthabits.ui.theme.AppTheme
import com.damiandantas.daylighthabits.utils.hasLocationPermission
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var showSplashScreen by mutableStateOf(true)

        installSplashScreen().setKeepOnScreenCondition {
            showSplashScreen
        }

        setContent {
            var showAppScreen by remember { mutableStateOf(false) }

            LaunchedEffect(this) {
                showAppScreen = hasLocationPermission()
                showSplashScreen = false
            }

            val launcher =
                rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { enabled ->
                    showAppScreen = enabled
                    if (!enabled) finish()
                }

            AppTheme {
                if (showAppScreen) {
                    AppScreen()
                } else {
                    LocationPermissionDialog(
                        onDismissDialog = {
                            finish()
                        }, onClickButton = {
                            launcher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
                        }
                    )
                }
            }
        }
    }
}
