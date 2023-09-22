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

    suspend fun alarmTime(): ZonedDateTime? = duration()?.let(nextForecast()::minus)

    private suspend fun scheduleAlarmForNextForecast() {
        val alarmTime = alarmTime() ?: return
        scheduler.unschedule() // TODO: DO we need to unschedule?
        scheduler.schedule(alarmTime)
    }
}
