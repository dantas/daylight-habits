package com.damiandantas.daylighthabits.domain

import java.time.Duration
import java.time.ZonedDateTime
import javax.inject.Inject

class SunriseAlarm @Inject constructor() {
    private var isEnabled = false
    private var sleepDuration = Duration.ofHours(8)

    fun enable() {
        isEnabled = true
    }

    fun disable() {
        isEnabled = false
    }

    fun isEnabled(): Boolean = isEnabled

    fun setSleepDuration(duration: Duration) {
        sleepDuration = duration
    }

    fun sleepDuration(): Duration = sleepDuration

    fun alarmTime(): ZonedDateTime = ZonedDateTime.now()
}