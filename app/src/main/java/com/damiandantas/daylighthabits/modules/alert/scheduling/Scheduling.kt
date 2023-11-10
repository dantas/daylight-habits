package com.damiandantas.daylighthabits.modules.alert.scheduling

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.damiandantas.daylighthabits.modules.alert.AlertConfig
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
    suspend fun setSchedule(config: AlertConfig) {
        val alert = upcomingForecast.get().createAlert(config)

        if (alert != null) {
            scheduler.schedule(alert)
        } else {
            scheduler.unschedule(config.type)
        }
    }
}

class AlertRescheduler @Inject constructor(
    private val repository: AlertConfigRepository,
    private val domainScheduler: AlertScheduler
) {
    suspend fun reschedule() {
        for (type in AlertType.values()) {
            val config = repository.load(type).getOrNull() ?: continue
            domainScheduler.setSchedule(config)
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