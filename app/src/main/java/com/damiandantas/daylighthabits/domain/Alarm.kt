package com.damiandantas.daylighthabits.domain

import java.time.Duration
import java.time.ZonedDateTime

class Alarm(
    private val storage: AlarmStorage,
    private val scheduler: AlarmScheduler,
    private val tomorrowForecast: suspend () -> ZonedDateTime
) {
    suspend fun enable() {
        storage.enable()
        scheduleForTomorrow()
    }

    suspend fun disable() {
        storage.disable()
        scheduler.unschedule()
    }

    suspend fun isEnabled(): Boolean = storage.isEnabled()

    suspend fun setSleepDuration(duration: Duration) {
        storage.setSleepDuration(duration)
        scheduleForTomorrow()
    }

    suspend fun sleepDuration(): Duration = storage.sleepDuration()

    private suspend fun scheduleForTomorrow() {
        scheduler.unschedule()
        val alarmTime = tomorrowForecast().minus(sleepDuration())
        scheduler.schedule(alarmTime)
    }
}
