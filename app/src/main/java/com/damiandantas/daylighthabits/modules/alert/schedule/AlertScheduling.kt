package com.damiandantas.daylighthabits.modules.alert.schedule

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.damiandantas.daylighthabits.modules.alert.AlertSchedule
import com.damiandantas.daylighthabits.modules.alert.AlertType
import com.damiandantas.daylighthabits.modules.alert.alertTime
import com.damiandantas.daylighthabits.modules.alert.getTime
import com.damiandantas.daylighthabits.modules.forecast.Forecast
import com.damiandantas.daylighthabits.modules.forecast.UpcomingForecast
import com.damiandantas.daylighthabits.utils.hasLocationPermission
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.Clock
import java.time.Instant
import java.time.ZonedDateTime
import javax.inject.Inject

class AlertScheduler @Inject constructor(
    private val clock: Clock,
    private val upcomingForecast: UpcomingForecast,
    private val scheduler: SystemAlertScheduler
) {
    suspend fun setSchedule(schedule: AlertSchedule) {
        scheduler.unschedule(schedule.type)

        if (schedule.isEnabled) {
            val time = scheduleTime(clock.instant(), upcomingForecast.get(), schedule)
            scheduler.schedule(schedule.type, time)
        }
    }
}

class AlertRescheduler @Inject constructor(
    private val repository: AlertScheduleRepository,
    private val scheduler: AlertScheduler
) {
    suspend fun reschedule() {
        for (type in AlertType.values()) {
            val schedule = repository.load(type) ?: continue
            scheduler.setSchedule(schedule)
        }
    }
}

@AndroidEntryPoint
class ReschedulerBroadcastReceiver : BroadcastReceiver() {
    @Inject
    lateinit var rescheduler: AlertRescheduler

    @OptIn(DelicateCoroutinesApi::class)
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED && intent.action != Intent.ACTION_MY_PACKAGE_REPLACED) {
            return
        }

        val pendingResult = goAsync()

        GlobalScope.launch {
            if (context.hasLocationPermission()) rescheduler.reschedule()
            pendingResult.finish()
        }
    }
}

// TODO: Tests

/*
    Alerts are scheduled in two different situations:

    1. Current time is before alert time. When the alert is triggered, we notify the user
    and reschedule the alert for the next day.

    2. Current time is between alert time and sun event time. We cannot use the alert time
    because it is in the past. We use the sun event time in the scheduling, we don't notify
    the user but we only use this to reschedule the alert for the next day.
 */
fun scheduleTime(now: Instant, forecast: Forecast, schedule: AlertSchedule): ZonedDateTime {
    val alertTime = schedule.alertTime(forecast)

    return if (now.isBefore(alertTime.toInstant())) {
        alertTime
    } else {
        forecast.getTime(schedule.type)
    }
}

fun shouldTriggerAlert(now: Instant, forecast: Forecast, type: AlertType): Boolean =
    now.isAfter(forecast.getTime(type).toInstant())
