package com.damiandantas.daylighthabits.modules.alert

import androidx.compose.runtime.Immutable
import com.damiandantas.daylighthabits.modules.forecast.Forecast
import java.time.Duration
import java.time.ZonedDateTime

enum class AlertType {
    SUNRISE, SUNSET
}

@Immutable
data class Alert(
    val time: ZonedDateTime,
    val config: AlertConfig,
)

@Immutable
data class AlertConfig(
    val type: AlertType,
    val noticePeriod: Duration,
    val isEnabled: Boolean
) {
    constructor(type: AlertType) : this(type, Duration.ZERO, false)
}

fun Forecast.createAlert(config: AlertConfig): Alert? {
    if (!config.isEnabled) return null

    return Alert(
        time = getTime(config.type) - config.noticePeriod,
        config = config
    )
}

fun Forecast.getTime(type: AlertType): ZonedDateTime =
    when (type) {
        AlertType.SUNRISE -> sunrise
        AlertType.SUNSET -> sunset
    }
