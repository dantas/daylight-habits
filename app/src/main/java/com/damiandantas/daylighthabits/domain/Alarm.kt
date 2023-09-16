package com.damiandantas.daylighthabits.domain

import java.time.Duration
import java.time.ZonedDateTime

class Alarm(
    private val storage: AlarmStorage,
    private val scheduler: AlarmScheduler,
    private val nextForecast: suspend () -> ZonedDateTime
) {
    suspend fun enable() {
        storage.enable()
        scheduleAlarmForNextForecast()
    }

    suspend fun disable() {
        storage.disable()
        scheduler.unschedule()
    }

    suspend fun isEnabled(): Boolean = storage.isEnabled()

    suspend fun setSleepDuration(duration: Duration) {
        storage.setSleepDuration(duration)
        scheduleAlarmForNextForecast()
    }

    suspend fun duration(): Duration? = storage.sleepDuration()

    fun alarmTime(): ZonedDateTime? = scheduler.scheduledTime()

    private suspend fun scheduleAlarmForNextForecast() {
        scheduler.unschedule()
        storage.sleepDuration()
        val alarmTime = nextForecast().minus(duration())
        scheduler.schedule(alarmTime)
    }
}
