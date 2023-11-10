package com.damiandantas.daylighthabits.modules.forecast

import com.damiandantas.daylighthabits.modules.LocationProvider
import com.damiandantas.daylighthabits.utils.di.DispatcherDefault
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.ZoneId
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

interface OnDateForecast {
    suspend fun onDate(date: LocalDate): Forecast
}

@Module
@InstallIn(SingletonComponent::class)
private object ForecastModule {
    @Provides
    @Singleton
    fun provideOnDateForecast(calculate: CalculateOnDateForecast): OnDateForecast =
        CachedOnDateForecast(calculate)
}

private class CachedOnDateForecast @Inject constructor(
    private val forecast: OnDateForecast
) : OnDateForecast {
    private val cache = ConcurrentHashMap<LocalDate, Forecast>()

    override suspend fun onDate(date: LocalDate): Forecast =
        cache.getOrPut(date) {
            forecast.onDate(date)
        }
}

private class CalculateOnDateForecast @Inject constructor(
    private val locationProvider: LocationProvider,
    private val zoneId: ZoneId,
    @DispatcherDefault private val dispatcherDefault: CoroutineDispatcher
) : OnDateForecast {
    override suspend fun onDate(date: LocalDate): Forecast = withContext(dispatcherDefault) {
        calculateForecast(locationProvider.currentLocation(), date, zoneId)
    }
}