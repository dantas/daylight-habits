package com.damiandantas.daylighthabits.ui.screen.alert

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.damiandantas.daylighthabits.alert.domain.Alert
import com.damiandantas.daylighthabits.alert.domain.AlertService
import com.damiandantas.daylighthabits.alert.domain.createAlert
import com.damiandantas.daylighthabits.common.di.Sunrise
import com.damiandantas.daylighthabits.common.di.Sunset
import com.damiandantas.daylighthabits.forecast.domain.forecast.UpcomingForecast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class AlertScreenSunriseViewModel @Inject constructor(
    @Sunrise alertService: AlertService,
    private val upcomingForecast: UpcomingForecast,
) : AlertScreenViewModel(
    alertService = alertService,
    forecast = { upcomingForecast.get().sunrise }
)

@HiltViewModel
class AlertScreenSunsetViewModel @Inject constructor(
    @Sunset alertService: AlertService,
    private val upcomingForecast: UpcomingForecast,
) : AlertScreenViewModel(
    alertService = alertService,
    forecast = { upcomingForecast.get().sunset }
)

data class AlertScreenCardState(
    val sunTime: ZonedDateTime,
    val isEnabled: Boolean,
    val alert: Alert?,
)

open class AlertScreenViewModel(
    private val alertService: AlertService,
    private val forecast: suspend () -> ZonedDateTime
) : ViewModel() {
    val cardState: StateFlow<AlertScreenCardState?> =
        combine(
            alertService.isEnabled,
            alertService.alert
        ) { isEnabled, alert ->
            AlertScreenCardState(
                forecast(),
                isEnabled,
                alert
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    fun setEnabled(enabled: Boolean) {
        viewModelScope.launch {
            alertService.setEnabled(enabled)
        }
    }

    fun setNoticeTime(duration: Duration) {
        viewModelScope.launch {
            val alert = forecast().createAlert(duration)
            alertService.setAlert(alert)
        }
    }
}
