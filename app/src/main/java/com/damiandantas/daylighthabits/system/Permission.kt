package com.damiandantas.daylighthabits.system

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
fun Context.hasLocationPermission() =
    ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
suspend fun ComponentActivity.requestLocationPermission(): Boolean = suspendCancellableCoroutine { continuation ->
    val launcher = registerForActivityResult(ActivityResultContracts.RequestPermission(), continuation::resume)

    continuation.invokeOnCancellation {
        launcher.unregister()
    }
}