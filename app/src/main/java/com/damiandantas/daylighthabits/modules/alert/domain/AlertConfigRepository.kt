package com.damiandantas.daylighthabits.modules.alert.domain

import com.damiandantas.daylighthabits.modules.AlertConfig
import com.damiandantas.daylighthabits.modules.SunMomentType
import kotlinx.coroutines.flow.Flow

interface AlertConfigRepository {
    val configs: Flow<AlertConfig>
    suspend fun save(config: AlertConfig): Result<Unit> // TODO: Check for error
    suspend fun load(type: SunMomentType): Result<AlertConfig> // TODO: Check for error
}
