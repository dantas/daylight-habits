package com.damiandantas.daylighthabits.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.Duration
import java.time.ZonedDateTime

// TODO: Notify storage error

data class Timer(val time: ZonedDateTime, val duration: Duration)

class Alarm(
    private val storage: AlarmStorage,
    private val scheduler: AlarmScheduler,
    private val nextForecast: suspend () -> ZonedDateTime
) {
    val isEnabled: Flow<Boolean> = storage.isEnabled

    val timer: Flow<Timer> =
        storage.timerDuration.map { duration ->
            Timer(time = nextForecast().minus(duration), duration = duration)
        }

    suspend fun setEnabled(isEnabled: Boolean) {
        if (storage.setEnabled(isEnabled).isFailure) return

        if (isEnabled) {
            scheduleTimer()
        } else {
            scheduler.unschedule()
        }
    }

    suspend fun setTimerDuration(duration: Duration) {
        if (storage.setTimerDuration(duration).isSuccess) {
            scheduleTimer()
        }
    }

    private suspend fun scheduleTimer() {
        if (!isEnabled.first()) return
        val timer = timer.first()
        scheduler.unschedule() // TODO: DO we need to unschedule?
        scheduler.schedule(timer.time)
    }
}
