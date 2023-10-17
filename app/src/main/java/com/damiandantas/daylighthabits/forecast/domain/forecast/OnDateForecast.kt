package com.damiandantas.daylighthabits.forecast.domain.forecast

import com.damiandantas.daylighthabits.di.DispatcherDefault
import com.damiandantas.daylighthabits.forecast.domain.LocationProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.ZoneId
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

interface OnDateForecast {
    suspend fun onDate(date: LocalDate): Forecast
}

class CachedOnDateForecast @Inject constructor(
    private val forecast: OnDateForecast
) : OnDateForecast {
    private val cache = ConcurrentHashMap<LocalDate, Forecast>()

    override suspend fun onDate(date: LocalDate): Forecast =
        cache.getOrPut(date) {
            forecast.onDate(date)
        }
}

class CalculateOnDateForecast @Inject constructor(
    private val locationProvider: LocationProvider,
    private val zoneId: ZoneId,
    @DispatcherDefault private val dispatcherDefault: CoroutineDispatcher
) : OnDateForecast {
    override suspend fun onDate(date: LocalDate): Forecast = withContext(dispatcherDefault) {
        calculateForecast(locationProvider.currentLocation(), date, zoneId)
    }
}