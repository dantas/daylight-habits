package com.damiandantas.daylighthabits.di

import com.damiandantas.daylighthabits.domain.Alarm
import com.damiandantas.daylighthabits.domain.AlarmScheduler
import com.damiandantas.daylighthabits.domain.AlarmStorage
import com.damiandantas.daylighthabits.domain.SunForecast
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AlarmModule {
    @Sunrise
    @Provides
    fun provideSunriseAlarm(
        sunForecast: SunForecast,
        @Sunrise storage: AlarmStorage,
        scheduler: AlarmScheduler
    ): Alarm = Alarm(storage, scheduler) { sunForecast.tomorrow().sunrise }

    @Sunset
    @Provides
    fun provideSunsetAlarm(
        sunForecast: SunForecast,
        @Sunset storage: AlarmStorage,
        scheduler: AlarmScheduler
    ): Alarm = Alarm(storage, scheduler) { sunForecast.tomorrow().sunset }
}