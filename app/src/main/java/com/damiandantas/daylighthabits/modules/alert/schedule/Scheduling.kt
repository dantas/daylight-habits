package com.damiandantas.daylighthabits.modules.alert.schedule

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.damiandantas.daylighthabits.modules.alert.AlertSchedule
import com.damiandantas.daylighthabits.modules.alert.AlertType
import com.damiandantas.daylighthabits.modules.alert.createAlert
import com.damiandantas.daylighthabits.modules.forecast.UpcomingForecast
import com.damiandantas.daylighthabits.utils.hasLocationPermission
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class AlertScheduler @Inject constructor(
    private val upcomingForecast: UpcomingForecast,
    private val scheduler: SystemScheduler
) {
    suspend fun setSchedule(schedule: AlertSchedule) {
        val alert = upcomingForecast.get().createAlert(schedule)

        if (alert != null) {
            scheduler.schedule(alert)
        } else {
            scheduler.unschedule(schedule.type)
        }
    }
}

class AlertRescheduler @Inject constructor(
    private val repository: AlertScheduleRepository,
    private val domainScheduler: AlertScheduler
) {
    suspend fun reschedule() {
        for (type in AlertType.values()) {
            val schedule = repository.load(type).getOrNull() ?: continue
            domainScheduler.setSchedule(schedule)
        }
    }
}

@AndroidEntryPoint
class ReschedulerBroadcastReceiver @Inject constructor(
    private val rescheduler: AlertRescheduler
) : BroadcastReceiver() {
    @OptIn(DelicateCoroutinesApi::class)
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context, intent: Intent) {
        GlobalScope.launch {
            if (context.hasLocationPermission()) rescheduler.reschedule()
        }
    }
}