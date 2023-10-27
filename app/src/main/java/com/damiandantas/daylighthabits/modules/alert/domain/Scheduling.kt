package com.damiandantas.daylighthabits.modules.alert.domain

import com.damiandantas.daylighthabits.modules.Alert
import com.damiandantas.daylighthabits.modules.AlertConfig
import com.damiandantas.daylighthabits.modules.SunMomentType
import com.damiandantas.daylighthabits.modules.createAlert
import com.damiandantas.daylighthabits.modules.forecast.domain.UpcomingForecast
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

interface SystemScheduler {
    suspend fun schedule(alert: Alert)
    suspend fun unschedule(type: SunMomentType)
}

class AlertRescheduler @Inject constructor(
    private val repository: AlertConfigRepository,
    private val domainScheduler: AlertScheduler
) {
    suspend fun reschedule() {
        for (type in SunMomentType.values()) {
            val config = repository.load(type).getOrNull() ?: continue
            domainScheduler.setSchedule(config)
        }
    }
}
