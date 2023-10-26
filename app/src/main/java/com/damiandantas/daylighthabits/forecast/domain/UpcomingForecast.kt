package com.damiandantas.daylighthabits.forecast.domain

import com.damiandantas.daylighthabits.utils.di.DispatcherDefault
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.time.Clock
import java.time.ZonedDateTime
import javax.inject.Inject

class UpcomingForecast @Inject constructor(
    private val onDateForecast: OnDateForecast,
    private val clock: Clock,
    @DispatcherDefault private val dispatcherDefault: CoroutineDispatcher
) {
    suspend fun get(): Forecast = withContext(dispatcherDefault) {
        val now = ZonedDateTime.now(clock)

        val todayForecast = onDateForecast.onDate(now.toLocalDate())

        if (now.isBefore(todayForecast.sunrise)) {
            return@withContext todayForecast
        }

        val tomorrowForecast = onDateForecast.onDate(now.toLocalDate().plusDays(1))

        if (now.isAfter(todayForecast.sunset)) {
            return@withContext tomorrowForecast
        }

        Forecast(
            sunrise = tomorrowForecast.sunrise,
            sunset = todayForecast.sunset,
        )
    }
}