package com.damiandantas.daylighthabits.modules

import androidx.compose.runtime.Immutable
import com.damiandantas.daylighthabits.modules.alert.AlertSchedule
import com.damiandantas.daylighthabits.modules.alert.AlertTime
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

@Immutable
data class SunMoment(
    val type: AlertType,
    val sunTime: ZonedDateTime,
    val alertSchedule: AlertSchedule,
    val alertTime: AlertTime?
)

class SunMomentService @Inject constructor(
    private val upcomingForecast: UpcomingForecast,
    private val repository: AlertScheduleRepository,
    private val domainScheduler: AlertScheduler
) {
    val moments: Flow<Result<SunMoment>> = repository.schedules.parallelMap { result ->
        val forecast = upcomingForecast.get()

        val alertSchedule = result.getOrElse { return@parallelMap Result.failure(it) }

        val moment = SunMoment(
            type = alertSchedule.type,
            sunTime = forecast.getTime(alertSchedule.type),
            alertSchedule = alertSchedule,
            alertTime = AlertTime.create(forecast, alertSchedule)
        )

        Result.success(moment)
    }

    suspend fun setEnabled(type: AlertType, isEnabled: Boolean): Boolean =
        updateSavedScheduleAndRescheduleIt(type) { it.copy(isEnabled = isEnabled) }

    suspend fun setNoticePeriod(type: AlertType, noticePeriod: Duration): Boolean =
        updateSavedScheduleAndRescheduleIt(type) { it.copy(noticePeriod = noticePeriod) }

    private suspend fun updateSavedScheduleAndRescheduleIt(
        type: AlertType,
        transform: (oldSchedule: AlertSchedule) -> AlertSchedule
    ): Boolean {
        val savedSchedule = repository.load(type) ?: return false

        val updatedSchedule = transform(savedSchedule)

        val isSaved = repository.save(updatedSchedule)

        if (isSaved) {
            domainScheduler.setSchedule(updatedSchedule)
        }

        return isSaved
    }
}
