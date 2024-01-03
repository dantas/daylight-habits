package com.damiandantas.daylighthabits.modules.alert.receiver

import com.damiandantas.daylighthabits.modules.alert.AlertType
import com.damiandantas.daylighthabits.modules.alert.schedule.AlertRescheduler
import com.damiandantas.daylighthabits.modules.alert.schedule.shouldTriggerAlert
import com.damiandantas.daylighthabits.modules.forecast.UpcomingForecast
import java.time.Clock
import javax.inject.Inject

class AlertExecutor @Inject constructor(
    private val clock: Clock,
    private val upcomingForecast: UpcomingForecast,
    private val rescheduler: AlertRescheduler
) {
    object ShowActivityEvent

    suspend fun execute(type: AlertType): ShowActivityEvent? {
        // TODO: Store time to speed up shouldTriggerAlert
        val show = shouldTriggerAlert(clock.instant(), upcomingForecast.get(), type)
        rescheduler.reschedule()
        return if (show) ShowActivityEvent else null
    }
}