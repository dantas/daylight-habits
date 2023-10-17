package com.damiandantas.daylighthabits.forecast.di

import com.damiandantas.daylighthabits.forecast.domain.LocationProvider
import com.damiandantas.daylighthabits.forecast.system.FusedLocationProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface LocationModule {
    @Binds
    fun bindLocationProvider(fusedProvider: FusedLocationProvider): LocationProvider
}