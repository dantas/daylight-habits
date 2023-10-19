package com.damiandantas.daylighthabits.alert.domain

import java.time.Duration
import java.time.ZonedDateTime

data class Alert(val time: ZonedDateTime, val noticeTime: Duration)

fun ZonedDateTime.createAlert(duration: Duration): Alert =
    Alert(
        time = this.minus(duration),
        noticeTime = duration
    )
