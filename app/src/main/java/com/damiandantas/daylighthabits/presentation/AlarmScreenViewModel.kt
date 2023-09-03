package com.damiandantas.daylighthabits.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import java.time.Duration
import java.time.ZonedDateTime

class AlarmScreenViewModel : ViewModel() {
    data class Event(
        val time: ZonedDateTime,
        val notificationTime: ZonedDateTime,
        val notificationEnabled: Boolean,
        val notificationDuration: Duration
    )

    private val _sunriseEvent =
        mutableStateOf(Event(ZonedDateTime.now(), ZonedDateTime.now(), false, Duration.ZERO))
    val sunriseEvent: State<Event> = _sunriseEvent

    private val _sunsetEvent =
        mutableStateOf(Event(ZonedDateTime.now(), ZonedDateTime.now(), false, Duration.ZERO))
    val sunsetEvent: State<Event> = _sunsetEvent

    fun onSetSunriseAlarm(enabled: Boolean) {
        _sunriseEvent.value = _sunriseEvent.value.copy(notificationEnabled = enabled)
    }

    fun onSetSunsetAlarm(enabled: Boolean) {
        _sunsetEvent.value = _sunsetEvent.value.copy(notificationEnabled = enabled)
    }

    fun onSetSunriseAlarmDuration(hour: Int, minute: Int) {

    }

    fun onSetSunsetAlarmDuration(hour: Int, minute: Int) {

    }
}