package com.damiandantas.daylighthabits.modules

import androidx.compose.runtime.Immutable
import com.damiandantas.daylighthabits.modules.alert.AlertSchedule
import com.damiandantas.daylighthabits.modules.alert.AlertType
import com.damiandantas.daylighthabits.modules.alert.alertTime
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
    val alertTime: ZonedDateTime
)

class SunMomentService @Inject constructor(
    private val upcomingForecast: UpcomingForecast,
    private val scheduleRepository: AlertScheduleRepository,
    private val scheduler: AlertScheduler
) {
    val moments: Flow<Result<SunMoment>> = scheduleRepository.schedules.parallelMap { result ->
        val forecast = upcomingForecast.get()

        val schedule = result.getOrElse { return@parallelMap Result.failure(it) }

        val moment = SunMoment(
            type = schedule.type,
            sunTime = forecast.getTime(schedule.type),
            alertSchedule = schedule,
            alertTime = schedule.alertTime(forecast)
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
        val current = scheduleRepository.load(type) ?: return false

        val new = transform(current)

        val isSaved = scheduleRepository.save(new)

        if (isSaved) {
            scheduler.setSchedule(new)
        }

        return isSaved
    }
}
