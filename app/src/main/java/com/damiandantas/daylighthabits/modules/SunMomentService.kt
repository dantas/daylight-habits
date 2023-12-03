package com.damiandantas.daylighthabits.modules

import androidx.compose.runtime.Immutable
import com.damiandantas.daylighthabits.modules.alert.Alert
import com.damiandantas.daylighthabits.modules.alert.AlertSchedule
import com.damiandantas.daylighthabits.modules.alert.AlertType
import com.damiandantas.daylighthabits.modules.alert.getTime
import com.damiandantas.daylighthabits.modules.alert.schedule.AlertScheduleRepository
import com.damiandantas.daylighthabits.modules.alert.schedule.AlertScheduler
import com.damiandantas.daylighthabits.modules.forecast.UpcomingForecast
import com.damiandantas.daylighthabits.utils.parallelMap
import kotlinx.coroutines.flow.Flow
import java.time.Duration
import java.time.ZonedDateTime
import javax.inject.Inject

// TODO: Define states like error, loading, moment?

@Immutable
data class SunMoment(
    val type: AlertType,
    val time: ZonedDateTime,
    val alert: Alert?
)

class SunMomentService @Inject constructor(
    private val upcomingForecast: UpcomingForecast,
    private val repository: AlertScheduleRepository,
    private val domainScheduler: AlertScheduler
) {
    val moments: Flow<SunMoment> = repository.schedules.parallelMap { schedule ->
        val forecast = upcomingForecast.get()

        SunMoment(
            type = schedule.type,
            time = forecast.getTime(schedule.type),
            alert = Alert.create(forecast, schedule)
        )
    }

    suspend fun setEnabled(type: AlertType, isEnabled: Boolean): Boolean {
        val newSchedule = updateSchedule(type) { it.copy(isEnabled = isEnabled) } ?: return false
        domainScheduler.setSchedule(newSchedule)
        return true
    }

    suspend fun setNoticePeriod(type: AlertType, noticePeriod: Duration): Boolean {
        val newSchedule =
            updateSchedule(type) { it.copy(noticePeriod = noticePeriod) } ?: return false
        domainScheduler.setSchedule(newSchedule)
        return true
    }

    private suspend fun updateSchedule(
        type: AlertType,
        block: (oldSchedule: AlertSchedule) -> AlertSchedule
    ): AlertSchedule? {
        val oldSchedule = repository.load(type) ?: return null

        val newSchedule = block(oldSchedule)

        return if (repository.save(newSchedule)) {
            newSchedule
        } else {
            null
        }
    }
}
