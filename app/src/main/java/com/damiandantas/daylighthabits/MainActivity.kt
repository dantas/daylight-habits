package com.damiandantas.daylighthabits

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import com.damiandantas.daylighthabits.ui.composables.LocationPermissionDialog
import com.damiandantas.daylighthabits.ui.theme.AppTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val activityResult =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) {
                if (!it) finish()
            }

        setContent {
            val hasLocationPermission by produceState(true) {
                value = hasLocationPermission()
            }

            var showDialog by remember { mutableStateOf(hasLocationPermission) }

            AppTheme {
                if (showDialog) {
                    LocationPermissionDialog(onDismissDialog = {
                        showDialog = false
                        finish()
                    }, onClickButton = {
                        showDialog = false
                        activityResult.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
                    })
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