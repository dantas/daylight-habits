package com.damiandantas.daylighthabits.domain

import java.time.Duration

interface AlarmStorage {
    suspend fun isEnabled(): Boolean
    suspend fun enable()
    suspend fun disable()
    suspend fun sleepDuration(): Duration?
    suspend fun setSleepDuration(duration: Duration)
}