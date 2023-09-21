package com.damiandantas.daylighthabits.di

import android.content.Context
import com.damiandantas.daylighthabits.data.AlarmStorageDataStore
import com.damiandantas.daylighthabits.data.sunriseAlarmDataStore
import com.damiandantas.daylighthabits.data.sunsetAlarmDataStore
import com.damiandantas.daylighthabits.domain.AlarmStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object StorageModule {
    @Sunrise
    @Provides
    fun provideSunriseAlarmStorage(@ApplicationContext context: Context): AlarmStorage =
        AlarmStorageDataStore(context.sunriseAlarmDataStore)

    @Sunset
    @Provides
    fun provideSunsetAlarmStorage(@ApplicationContext context: Context): AlarmStorage =
        AlarmStorageDataStore(context.sunsetAlarmDataStore)
}