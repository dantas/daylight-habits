package com.damiandantas.daylighthabits

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.lifecycleScope
import com.damiandantas.daylighthabits.ui.AppScreen
import com.damiandantas.daylighthabits.ui.composable.LocationPermissionDialog
import com.damiandantas.daylighthabits.ui.theme.AppTheme
import com.damiandantas.daylighthabits.utils.hasLocationPermission
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

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
