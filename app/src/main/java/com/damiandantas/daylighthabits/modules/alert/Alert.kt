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
data class AlertSettings(
    val vibrate: Boolean,
    val sound: Boolean
)

fun Forecast.getTime(type: AlertType): ZonedDateTime =
    when (type) {
        AlertType.SUNRISE -> sunrise
        AlertType.SUNSET -> sunset
    }

fun AlertSchedule.alertTime(forecast: Forecast): ZonedDateTime =
    forecast.getTime(type) - noticePeriod
