package com.damiandantas.daylighthabits.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import java.time.LocalTime

class AlarmScreenViewModel : ViewModel() {
    data class SleepTime(val isEnabled: Boolean, val duration: LocalTime)

    private val _sleepTime = mutableStateOf(SleepTime(false, LocalTime.now()))
    val sleepTime: androidx.compose.runtime.State<SleepTime> = _sleepTime

    var sunrise: LocalTime by mutableStateOf(LocalTime.now())
        private set

    init {
        sunrise = LocalTime.now()
    }

    fun onSetSleepTimeAlarm(enabled: Boolean) {
        _sleepTime.value = _sleepTime.value.copy(isEnabled = enabled)
    }

    fun onSetSleepTimeDuration(hour: Int, minute: Int) {
        _sleepTime.value = _sleepTime.value.copy(duration = LocalTime.of(hour, minute))
    }
}