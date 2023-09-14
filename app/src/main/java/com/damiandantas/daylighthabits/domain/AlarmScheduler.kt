package com.damiandantas.daylighthabits.domain

import java.time.ZonedDateTime

interface AlarmScheduler {
    fun schedule(time: ZonedDateTime)
    fun unschedule()
    fun scheduledTime(): ZonedDateTime?// TODO: Get from AlarmManager = ZonedDateTime.now()
}