package com.damiandantas.daylighthabits.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.damiandantas.daylighthabits.domain.SunriseAlarm
import com.damiandantas.daylighthabits.domain.SunsetAlarm
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Duration
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class AlarmScreenViewModel @Inject constructor(
    private val sunriseAlarm: SunriseAlarm,
    private val sunsetAlarm: SunsetAlarm
) : ViewModel() {
    data class SunAlarm(
        val time: ZonedDateTime,
        val notificationTime: ZonedDateTime,
        val notificationEnabled: Boolean,
        val notificationDuration: Duration
    )

    private val _sunriseAlarmState =
        mutableStateOf(SunAlarm(ZonedDateTime.now(), ZonedDateTime.now(), false, Duration.ZERO))
    val sunriseAlarmState: State<SunAlarm> = _sunriseAlarmState

    private val _sunsetAlarmState =
        mutableStateOf(SunAlarm(ZonedDateTime.now(), ZonedDateTime.now(), false, Duration.ZERO))
    val sunsetAlarmState: State<SunAlarm> = _sunsetAlarmState

    fun onSetSunriseAlarm(enabled: Boolean) {
        if (enabled) {
            sunriseAlarm.enable()
        } else {
            sunriseAlarm.disable()
        }

        _sunriseAlarmState.value =
            _sunriseAlarmState.value.copy(notificationEnabled = sunriseAlarm.isEnabled())
    }

    fun onSetSunsetAlarm(enabled: Boolean) {
        if (enabled) {
            sunsetAlarm.enable()
        } else {
            sunsetAlarm.disable()
        }

        _sunsetAlarmState.value =
            _sunsetAlarmState.value.copy(notificationEnabled = sunsetAlarm.isEnabled())
    }

    fun onSetSunriseAlarmDuration(duration: Duration) {
        sunriseAlarm.setSleepDuration(duration)

        _sunriseAlarmState.value = _sunriseAlarmState.value.copy(
            notificationTime = sunriseAlarm.alarmTime(),
            notificationEnabled = sunriseAlarm.isEnabled(),
            notificationDuration = sunriseAlarm.sleepDuration()
        )
    }

    fun onSetSunsetAlarmDuration(duration: Duration) {
        sunsetAlarm.setSleepDuration(duration)

        _sunsetAlarmState.value = _sunsetAlarmState.value.copy(
            notificationTime = sunsetAlarm.alarmTime(),
            notificationEnabled = sunsetAlarm.isEnabled(),
            notificationDuration = sunsetAlarm.sleepDuration()
        )
    }
}