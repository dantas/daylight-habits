package com.damiandantas.daylighthabits.domain

import java.time.ZonedDateTime

interface AlarmScheduler {
    fun schedule(time: ZonedDateTime)
    fun unschedule()
}