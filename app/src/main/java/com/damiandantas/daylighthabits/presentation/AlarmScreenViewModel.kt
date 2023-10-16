package com.damiandantas.daylighthabits.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.damiandantas.daylighthabits.di.Sunrise
import com.damiandantas.daylighthabits.di.Sunset
import com.damiandantas.daylighthabits.domain.Alarm
import com.damiandantas.daylighthabits.domain.SunForecast
import com.damiandantas.daylighthabits.domain.Timer
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
class AlarmScreenViewModel @Inject constructor(
    @Sunrise private val sunriseAlarm: Alarm,
    @Sunset private val sunsetAlarm: Alarm,
    private val sunForecast: SunForecast // TODO: Replace with some sort of cache
) : ViewModel() {
    data class CardState(
        val sunTime: ZonedDateTime,
        val isAlarmEnabled: Boolean,
        val timer: Timer,
    )

    val sunrise: StateFlow<CardState?> =
        combine(
            sunriseAlarm.isEnabled,
            sunriseAlarm.timer,
        ) { isEnabled, timeDuration ->
            CardState(sunForecast.nextForecast().sunrise, isEnabled, timeDuration)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    val sunset: StateFlow<CardState?> =
        combine(
            sunsetAlarm.isEnabled,
            sunsetAlarm.timer,
        ) { isEnabled, timeDuration ->
            CardState(sunForecast.nextForecast().sunset, isEnabled, timeDuration)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    fun onSetSunriseAlarm(enabled: Boolean) {
        viewModelScope.launch {
            sunriseAlarm.setEnabled(enabled)
        }
    }

    fun onSetSunsetAlarm(enabled: Boolean) {
        viewModelScope.launch {
            sunsetAlarm.setEnabled(enabled)
        }
    }

    fun onSetSunriseTimerDuration(duration: Duration) {
        viewModelScope.launch {
            sunriseAlarm.setTimerDuration(duration)
        }
    }

    fun onSetSunsetTimerDuration(duration: Duration) {
        viewModelScope.launch {
            sunsetAlarm.setTimerDuration(duration)
        }
    }
}