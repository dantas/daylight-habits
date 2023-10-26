package com.damiandantas.daylighthabits.alert.domain

import kotlinx.coroutines.flow.Flow

interface AlertConfigRepository {
    val configs: Flow<AlertConfig>
    suspend fun save(config: AlertConfig): Result<Unit> // TODO: Check for error
    suspend fun load(type: SunMomentType): Result<AlertConfig?> // TODO: Check for error
}

suspend fun AlertConfigRepository.loadOrDefault(type: SunMomentType): Result<AlertConfig> =
    load(type).map { it ?: AlertConfig(type) }
