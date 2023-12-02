package com.damiandantas.daylighthabits.modules.alert

import androidx.compose.runtime.Immutable
import com.damiandantas.daylighthabits.modules.forecast.Forecast
import java.time.Duration
import java.time.ZonedDateTime

enum class AlertType {
    SUNRISE, SUNSET
}

@Immutable
// Should've been a data class but then we wouldn't be able to hide its copy constructor
class Alert private constructor(
    val time: ZonedDateTime,
    val schedule: AlertSchedule,
) {
    override fun equals(other: Any?): Boolean {
        if (other !is Alert) return false
        return time == other.time && schedule == other.schedule
    }

    override fun hashCode(): Int = 31 * time.hashCode() + schedule.hashCode()

    companion object {
        fun create(forecast: Forecast, schedule: AlertSchedule): Alert? {
            if (!schedule.isEnabled) return null

            return Alert(
                time = forecast.getTime(schedule.type) - schedule.noticePeriod,
                schedule = schedule
            )
        }
    }
}

@Immutable
data class AlertSchedule(
    val type: AlertType,
    val noticePeriod: Duration,
    val isEnabled: Boolean
) {
    constructor(type: AlertType) : this(type, Duration.ZERO, false)
}

fun Forecast.getTime(type: AlertType): ZonedDateTime =
    when (type) {
        AlertType.SUNRISE -> sunrise
        AlertType.SUNSET -> sunset
    }
