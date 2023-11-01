package com.damiandantas.daylighthabits.utils

import java.time.Duration

val Duration.hours
    get() = toHours().toInt()

val Duration.minutes
    get() = (toMinutes() % 60).toInt()