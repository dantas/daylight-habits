package com.damiandantas.daylighthabits.modules

import java.time.Duration
import java.time.ZonedDateTime

data class SunMoment(
    val type: SunMomentType,
    val time: ZonedDateTime,
    val alert: Alert?
)

enum class SunMomentType {
    SUNRISE, SUNSET
}

data class Alert(
    val time: ZonedDateTime,
    val config: AlertConfig,
)

data class AlertConfig(
    val type: SunMomentType,
    val noticePeriod: Duration,
    val isEnabled: Boolean
) {
    constructor(type: SunMomentType) : this(type, Duration.ZERO, false)
}

data class Forecast(val sunrise: ZonedDateTime, val sunset: ZonedDateTime)

fun Forecast.createAlert(config: AlertConfig): Alert? {
    if (!config.isEnabled) return null

    return Alert(
        time = getTime(config.type) - config.noticePeriod,
        config = config
    )
}

fun Forecast.getTime(type: SunMomentType): ZonedDateTime =
    when (type) {
        SunMomentType.SUNRISE -> sunrise
        SunMomentType.SUNSET -> sunset
    }
