package com.damiandantas.daylighthabits.modules.alert

import androidx.compose.runtime.Immutable
import com.damiandantas.daylighthabits.modules.forecast.Forecast
import java.time.Duration
import java.time.ZonedDateTime

enum class AlertType {
    SUNRISE, SUNSET
}

@Immutable
data class AlertSchedule(
    val type: AlertType,
    val noticePeriod: Duration,
    val isEnabled: Boolean
) {
    constructor(type: AlertType) : this(type, Duration.ZERO, false)
}

@Immutable
class AlertTime private constructor(
    val type: AlertType,
    val time: ZonedDateTime
) {
    override fun equals(other: Any?): Boolean {
        if (other !is AlertTime) return false
        return time == other.time && type == other.type
    }

    override fun hashCode(): Int = 31 * time.hashCode() + type.hashCode()

    companion object {
        fun create(forecast: Forecast, schedule: AlertSchedule): AlertTime? {
            if (!schedule.isEnabled) return null

            return AlertTime(
                type = schedule.type,
                time = forecast.getTime(schedule.type) - schedule.noticePeriod,
            )
        }
    }
}

fun Forecast.getTime(type: AlertType): ZonedDateTime =
    when (type) {
        AlertType.SUNRISE -> sunrise
        AlertType.SUNSET -> sunset
    }
