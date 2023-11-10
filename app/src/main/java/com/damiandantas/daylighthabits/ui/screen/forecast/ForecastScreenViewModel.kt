package com.damiandantas.daylighthabits.ui.screen.forecast

import androidx.lifecycle.ViewModel
import com.damiandantas.daylighthabits.modules.forecast.Forecast
import com.damiandantas.daylighthabits.modules.forecast.OnDateForecast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.Clock
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ForecastScreenViewModel @Inject constructor(
    private val onDateForecast: OnDateForecast,
    private val clock: Clock
) : ViewModel() {
    val nextDaysForecast: Flow<Forecast> = flow {
        for (day in 0L until 7L) {
            val now = LocalDate.now(clock).plusDays(day)
            val forecast = onDateForecast.onDate(now)
            emit(forecast)
        }
    }
}