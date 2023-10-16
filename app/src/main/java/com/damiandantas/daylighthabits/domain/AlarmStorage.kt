package com.damiandantas.daylighthabits.domain

import kotlinx.coroutines.flow.Flow
import java.time.Duration

interface AlarmStorage {
    val isEnabled: Flow<Boolean>
    suspend fun setEnabled(enabled: Boolean): Result<Unit>

    val timerDuration: Flow<Duration>
    suspend fun setTimerDuration(duration: Duration): Result<Unit>
}