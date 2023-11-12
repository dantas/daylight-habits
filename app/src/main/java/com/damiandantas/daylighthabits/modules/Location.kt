package com.damiandantas.daylighthabits.modules

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.resume

data class Location(val latitude: Double, val longitude: Double, val altitude: Double)

interface LocationProvider {
    suspend fun currentLocation(): Location
}

private class FusedLocationProvider @Inject constructor(
    @ApplicationContext private val context: Context
) : LocationProvider {
    override suspend fun currentLocation(): Location =
        context.getCurrentLocation()
}

@Module
@InstallIn(SingletonComponent::class)
private interface LocationModule {
    @Binds
    fun bindLocationProvider(fusedProvider: FusedLocationProvider): LocationProvider
}

@SuppressLint("MissingPermission")
private suspend fun Context.getCurrentLocation(): Location = withContext(Dispatchers.IO) {
    // TODO: Provide reusable instance?
    val client = LocationServices.getFusedLocationProviderClient(this@getCurrentLocation)

    val task = client.getCurrentLocation(
        Priority.PRIORITY_BALANCED_POWER_ACCURACY,
        CancellationTokenSource().token
    )

    val androidLocation = task.await() ?: client.requestSingleLocation()

    Location(androidLocation.latitude, androidLocation.longitude, androidLocation.altitude)
}

// If client.getCurrentLocation returns null, attempt to manually request a location
// Some say there is no workaround for this but I say lets see what happens:
// https://medium.com/@debuggingisfun/fusedlocationproviderclients-null-location-2e13d6128031
@SuppressLint("MissingPermission")
private suspend fun FusedLocationProviderClient.requestSingleLocation(): android.location.Location =
    suspendCancellableCoroutine { continuation ->
        lateinit var removeCallback: () -> Unit

        val listener = LocationListener { location ->
            removeCallback()
            continuation.resume(location)
        }

        removeCallback = { removeLocationUpdates(listener) }

        requestLocationUpdates(
            LocationRequest.Builder(60_000)
                .setPriority(Priority.PRIORITY_BALANCED_POWER_ACCURACY)
                .build(),
            listener,
            Looper.getMainLooper()
        )

        continuation.invokeOnCancellation {
            removeCallback()
        }
    }