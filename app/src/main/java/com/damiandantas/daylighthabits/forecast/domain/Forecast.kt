package com.damiandantas.daylighthabits.forecast.domain

import java.time.ZonedDateTime

data class Forecast(val sunrise: ZonedDateTime, val sunset: ZonedDateTime)