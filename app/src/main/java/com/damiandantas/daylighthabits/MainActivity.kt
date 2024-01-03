package com.damiandantas.daylighthabits

import android.Manifest
import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
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

        setContent {
            AppTheme {
                ActivityScreen { state ->
                    when (state) {
                        State.Splash -> Unit

                        is State.Permission -> {
                            LocationPermissionDialog(
                                onDismissDialog = ::finish,
                                onClickButton = state.requestPermission
                            )
                        }

                        State.App -> AppScreen()
                    }
                }
            }
        }
    }
}

private sealed class State {
    object Splash : State()
    data class Permission(val requestPermission: () -> Unit) : State()
    object App : State()
}

@Composable
private fun Activity.ActivityScreen(content: @Composable (state: State) -> Unit) {
    var state: State by remember { mutableStateOf(State.Splash) }

    installSplashScreen().setKeepOnScreenCondition {
        state == State.Splash
    }

    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { enabled ->
            if (enabled) {
                state = State.App
            } else {
                finish()
            }
        }

    LaunchedEffect(true) {
        state = if (hasLocationPermission()) {
            State.App
        } else {
            State.Permission { launcher.launch(Manifest.permission.ACCESS_COARSE_LOCATION) }
        }
    }

    content(state)
}