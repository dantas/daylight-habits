package com.damiandantas.daylighthabits.di

import com.damiandantas.daylighthabits.domain.LocationProvider
import com.damiandantas.daylighthabits.system.FusedLocationProvider
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