package com.damiandantas.daylighthabits.system

import android.annotation.SuppressLint
import android.content.Context
import com.damiandantas.daylighthabits.domain.Location
import com.damiandantas.daylighthabits.domain.LocationProvider
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FusedLocationProvider @Inject constructor(
    @ApplicationContext private val context: Context
) : LocationProvider {
    override suspend fun currentLocation(): Location =
        context.getCurrentLocation()
}

@SuppressLint("MissingPermission")
private suspend fun Context.getCurrentLocation(): Location = withContext(Dispatchers.IO) {
    val client = LocationServices.getFusedLocationProviderClient(this@getCurrentLocation)

    val task = client.getCurrentLocation(
        Priority.PRIORITY_BALANCED_POWER_ACCURACY,
        CancellationTokenSource().token
    )

    val location = task.await()

    Location(location.latitude, location.longitude, location.altitude)
}
