package com.damiandantas.daylighthabits.modules

import com.damiandantas.daylighthabits.modules.alert.domain.AlertConfigRepository
import com.damiandantas.daylighthabits.modules.alert.domain.AlertScheduler
import com.damiandantas.daylighthabits.modules.forecast.domain.UpcomingForecast
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Duration
import javax.inject.Inject

class SunMomentService @Inject constructor(
    private val upcomingForecast: UpcomingForecast,
    private val repository: AlertConfigRepository,
    private val domainScheduler: AlertScheduler
) {
    val moments: Flow<SunMoment> = repository.configs.map { config ->
        val forecast = upcomingForecast.get()

        SunMoment(
            type = config.type,
            time = forecast.getTime(config.type),
            alert = forecast.createAlert(config)
        )
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
        val oldConfig = repository.load(type).getOrThrow() // TODO: Deal with error

        val newConfig = block(oldConfig)

        repository.save(newConfig) // TODO: Deal with error

        return newConfig
    }
}