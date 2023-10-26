package com.damiandantas.daylighthabits.alert.domain

import com.damiandantas.daylighthabits.forecast.domain.UpcomingForecast
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.Duration
import javax.inject.Inject

class SunMomentService @Inject constructor(
    private val upcomingForecast: UpcomingForecast,
    private val repository: AlertConfigRepository,
    private val domainScheduler: AlertScheduler
) {
    val moments: Flow<SunMoment> = flow {
        val forecast = upcomingForecast.get()

        for (type in SunMomentType.values()) {
            val config = repository.loadOrDefault(type).getOrThrow() // TODO: Fix error

            val moment = SunMoment(
                type = type,
                time = forecast.getTime(config.type),
                alert = forecast.createAlert(config)
            )

            emit(moment)
        }

        repository.configs.collect { config ->
            val forecast = upcomingForecast.get()

            val moment = SunMoment(
                type = config.type,
                time = forecast.getTime(config.type),
                alert = forecast.createAlert(config)
            )

            emit(moment)
        }
    }

    suspend fun setEnabled(type: SunMomentType, isEnabled: Boolean) {
        val config = applyChange(type) { it.copy(isEnabled = isEnabled) }
        domainScheduler.setSchedule(config)
    }

    suspend fun setNoticePeriod(type: SunMomentType, noticePeriod: Duration) {
        val config = applyChange(type) { it.copy(noticePeriod = noticePeriod) }
        domainScheduler.setSchedule(config)
    }

    private suspend fun applyChange(
        type: SunMomentType,
        block: (oldConfig: AlertConfig) -> AlertConfig
    ): AlertConfig {
        val oldConfig = repository.loadOrDefault(type).getOrThrow() // TODO: Deal with error

        val newConfig = block(oldConfig)

        repository.save(newConfig) // TODO: Deal with error

        return newConfig
    }
}