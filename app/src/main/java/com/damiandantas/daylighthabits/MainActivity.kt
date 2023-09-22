package com.damiandantas.daylighthabits

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.damiandantas.daylighthabits.ui.app.AppScreen
import com.damiandantas.daylighthabits.ui.dialog.LocationPermissionDialog
import com.damiandantas.daylighthabits.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var hasLocationPermission by mutableStateOf(false)

        val activityResult =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) {
                hasLocationPermission = it
                if (!it) finish()
            }

        lifecycleScope.launch {
            hasLocationPermission = hasLocationPermission()
        }

        setContent {
            AppTheme {
                if (hasLocationPermission) {
                    AppScreen()
                } else {
                    LocationPermissionDialog(
                        onDismissDialog = {
                            finish()
                        }, onClickButton = {
                            activityResult.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
                        }
                    )
                }
            }
        }
    }
}

private suspend fun Context.hasLocationPermission() = withContext(Dispatchers.IO) {
    ContextCompat.checkSelfPermission(
        this@hasLocationPermission,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
}
