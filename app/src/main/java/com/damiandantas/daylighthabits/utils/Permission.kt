package com.damiandantas.daylighthabits.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun Context.hasLocationPermission() = withContext(Dispatchers.IO) {
    ContextCompat.checkSelfPermission(
        this@hasLocationPermission,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
}
