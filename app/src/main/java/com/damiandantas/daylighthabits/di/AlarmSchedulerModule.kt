package com.damiandantas.daylighthabits.di

import android.content.Context
import com.damiandantas.daylighthabits.domain.AlarmScheduler
import com.damiandantas.daylighthabits.system.AlarmManagerScheduler
import com.damiandantas.daylighthabits.system.AlarmSchedulerAlarmType
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AlarmSchedulerModule {
    @Sunrise
    @Provides
    fun provideSunriseAlarmScheduler(@ApplicationContext context: Context): AlarmScheduler =
        AlarmManagerScheduler(context, AlarmSchedulerAlarmType.Sunrise)

    @Sunset
    @Provides
    fun provideSunsetAlarmScheduler(@ApplicationContext context: Context): AlarmScheduler =
        AlarmManagerScheduler(context, AlarmSchedulerAlarmType.Sunset)
}