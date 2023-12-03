package com.damiandantas.daylighthabits.modules.alert.schedule

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.damiandantas.daylighthabits.modules.alert.Alert
import com.damiandantas.daylighthabits.modules.alert.AlertSchedule
import com.damiandantas.daylighthabits.modules.alert.AlertType
import com.damiandantas.daylighthabits.modules.forecast.UpcomingForecast
import javax.inject.Inject

class AlertScheduler @Inject constructor(
    private val upcomingForecast: UpcomingForecast,
    private val scheduler: SystemAlertScheduler
) {
    suspend fun setSchedule(schedule: AlertSchedule) {
        val alert = Alert.create(upcomingForecast.get(), schedule)

        if (alert != null) {
            scheduler.unschedule(schedule.type)
            scheduler.schedule(alert)
        } else {
            scheduler.unschedule(schedule.type)
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

class ReschedulerBroadcastReceiver : BroadcastReceiver() {
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context, intent: Intent) {
        /*
            Do nothing, this exists to bring the application up after is updated
            or after the device rebooted.
            MainApplication class will ensure alarms are reschedule.
            As long as it is doing nothing, it is OK to suppress UnsafeProtectedBroadcastReceiver
         */
    }
}