package com.damiandantas.daylighthabits.domain

import java.time.Duration
import java.time.ZonedDateTime

data class AlarmInfo(val duration: Duration, val time: ZonedDateTime)

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

    suspend fun info(): AlarmInfo? {
        val duration = storage.sleepDuration() ?: return null
        val time = nextForecast().minus(duration)
        return AlarmInfo(duration, time)
    }

    private suspend fun scheduleAlarmForNextForecast() {
        val alarmInfo = info() ?: return
        scheduler.unschedule() // TODO: DO we need to unschedule?
        scheduler.schedule(alarmInfo.time)
    }
}
