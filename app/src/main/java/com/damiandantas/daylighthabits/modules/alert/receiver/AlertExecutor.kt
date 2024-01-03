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
    object ShouldTriggerEvent

    suspend fun execute(type: AlertType): ShouldTriggerEvent? {
        // TODO: Store time to speed up shouldTriggerAlert
        val show = shouldTriggerAlert(clock.instant(), upcomingForecast.get(), type)
        rescheduler.reschedule()
        return if (show) ShouldTriggerEvent else null
    }
}