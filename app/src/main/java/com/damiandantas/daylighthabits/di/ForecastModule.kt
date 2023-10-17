package com.damiandantas.daylighthabits.di

import com.damiandantas.daylighthabits.domain.forecast.CachedOnDateForecast
import com.damiandantas.daylighthabits.domain.forecast.CalculateOnDateForecast
import com.damiandantas.daylighthabits.domain.forecast.OnDateForecast
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ForecastModule {
    @Provides
    @Singleton
    fun provideOnDateForecast(calculate: CalculateOnDateForecast): OnDateForecast =
        CachedOnDateForecast(calculate)
}