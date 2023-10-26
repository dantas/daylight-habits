package com.damiandantas.daylighthabits.location.system

import android.annotation.SuppressLint
import android.content.Context
import com.damiandantas.daylighthabits.location.domain.Location
import com.damiandantas.daylighthabits.location.domain.LocationProvider
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
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

@Module
@InstallIn(SingletonComponent::class)
interface LocationModule {
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

    val location = task.await()

    Location(location.latitude, location.longitude, location.altitude)
}