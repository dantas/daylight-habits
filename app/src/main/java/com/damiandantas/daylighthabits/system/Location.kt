package com.damiandantas.daylighthabits.system

import android.annotation.SuppressLint
import android.content.Context
import com.damiandantas.daylighthabits.domain.Location
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

@SuppressLint("MissingPermission")
suspend fun Context.getCurrentLocation(): Location? = withContext(Dispatchers.IO) {
    if (!hasLocationPermission()) {
        return@withContext null
    }

    val client = LocationServices.getFusedLocationProviderClient(this@getCurrentLocation)
    val task = client.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, CancellationTokenSource().token)
    val location = task.await()

    Location(location.latitude,location.longitude,location.altitude)
}
