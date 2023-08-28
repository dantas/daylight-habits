package com.damiandantas.daylighthabits.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.damiandantas.daylighthabits.domain.SunTime
import java.time.LocalTime
import java.time.ZonedDateTime

class AlarmScreenViewModel : ViewModel() {
    // Use Duration?
    data class Alarm(val isEnabled: Boolean, val duration: LocalTime)

    private val _sunriseAlarm = mutableStateOf(Alarm(false, LocalTime.now()))
    val sunriseAlarm: State<Alarm> = _sunriseAlarm

    private val _sunsetAlarm = mutableStateOf(Alarm(false, LocalTime.now()))
    val sunsetAlarm: State<Alarm> = _sunriseAlarm

    private val _sunTime = mutableStateOf(SunTime(ZonedDateTime.now(), ZonedDateTime.now()))
    var sunTime: State<SunTime> = _sunTime

    fun onSetSunriseAlarm(enabled: Boolean) {

    }

    fun onSetSunsetAlarm(enabled: Boolean) {

    }

    fun onSetSunriseAlarmDuration(hour: Int, minute: Int) {

    }

    fun onSetSunsetAlarmDuration(hour: Int, minute: Int) {

    }
}