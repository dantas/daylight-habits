package com.damiandantas.daylighthabits.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.damiandantas.daylighthabits.di.Sunrise
import com.damiandantas.daylighthabits.di.Sunset
import com.damiandantas.daylighthabits.domain.Alarm
import com.damiandantas.daylighthabits.domain.AlarmInfo
import com.damiandantas.daylighthabits.domain.SunForecast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class AlarmScreenViewModel @Inject constructor(
    @Sunrise private val sunriseAlarm: Alarm,
    @Sunset private val sunsetAlarm: Alarm,
    private val sunForecast: SunForecast
) : ViewModel() {
    data class SunMoment(
        val time: ZonedDateTime,
        val isAlarmEnabled: Boolean,
        val alarm: AlarmInfo?,
    )

    private val sunriseMomentManager = SunMomentManager(sunriseAlarm)
    private val sunsetMomentManager = SunMomentManager(sunsetAlarm)

    val sunrise: State<SunMoment?> = sunriseMomentManager.state
    val sunset: State<SunMoment?> = sunsetMomentManager.state

    init {
        viewModelScope.launch(Dispatchers.Default) {
            val forecast = sunForecast.nextForecast()

            sunriseMomentManager.apply {
                momentTime = forecast.sunrise
                updateState()
            }

            sunsetMomentManager.apply {
                momentTime = forecast.sunset
                updateState()
            }
        }
    }

    fun onSetSunriseAlarm(enabled: Boolean) {
        viewModelScope.launch {
            sunriseMomentManager.setAlarm(enabled)
        }
    }

    fun onSetSunsetAlarm(enabled: Boolean) {
        viewModelScope.launch {
            sunsetMomentManager.setAlarm(enabled)
        }
    }

    fun onSetSunriseAlarmDuration(duration: Duration) {
        viewModelScope.launch {
            sunriseMomentManager.setDuration(duration)
        }
    }

    fun onSetSunsetAlarmDuration(duration: Duration) {
        viewModelScope.launch {
            sunsetMomentManager.setDuration(duration)
        }
    }
}

private class SunMomentManager(
    private val alarm: Alarm
) {
    val state = mutableStateOf<AlarmScreenViewModel.SunMoment?>(null)

    lateinit var momentTime: ZonedDateTime

    suspend fun updateState() {
        state.value =
            AlarmScreenViewModel.SunMoment(momentTime, alarm.isEnabled(), alarm.info())
    }

    suspend fun setAlarm(enabled: Boolean) {
        if (enabled) {
            alarm.enable()
        } else {
            alarm.disable()
        }

        updateState()
    }

    suspend fun setDuration(duration: Duration) {
        alarm.setSleepDuration(duration)
        updateState()
    }
}