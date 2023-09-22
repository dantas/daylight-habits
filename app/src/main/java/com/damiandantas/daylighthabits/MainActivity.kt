package com.damiandantas.daylighthabits

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import com.damiandantas.daylighthabits.ui.app.AppScreen
import com.damiandantas.daylighthabits.ui.dialog.LocationPermissionDialog
import com.damiandantas.daylighthabits.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var showAppScreen by mutableStateOf(false)

        val activityResult =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) {
                showAppScreen = it
                if (!it) finish()
            }

        setContent {
            AppTheme {
                var showDialog by remember { mutableStateOf(false) }

                LaunchedEffect(true) {
                    showDialog = !hasLocationPermission()
                }

                if (showDialog) {
                    LocationPermissionDialog(
                        onDismissDialog = {
                            showDialog = false
                            finish()
                        }, onClickButton = {
                            showDialog = false
                            activityResult.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
                        }
                    )
                }

                if (showAppScreen) {
                    AppScreen()
                }
            }
        }
    }

    private suspend fun hasLocationPermission() = withContext(Dispatchers.IO) {
        ContextCompat.checkSelfPermission(
            this@MainActivity,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
}