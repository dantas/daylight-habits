package com.damiandantas.daylighthabits.modules

import androidx.compose.runtime.Immutable
import com.damiandantas.daylighthabits.modules.alert.Alert
import com.damiandantas.daylighthabits.modules.alert.AlertConfig
import com.damiandantas.daylighthabits.modules.alert.AlertType
import com.damiandantas.daylighthabits.modules.alert.createAlert
import com.damiandantas.daylighthabits.modules.alert.getTime
import com.damiandantas.daylighthabits.modules.alert.scheduling.AlertConfigRepository
import com.damiandantas.daylighthabits.modules.alert.scheduling.AlertScheduler
import com.damiandantas.daylighthabits.modules.forecast.UpcomingForecast
import com.damiandantas.daylighthabits.utils.parallelMap
import kotlinx.coroutines.flow.Flow
import java.time.Duration
import java.time.ZonedDateTime
import javax.inject.Inject

@Immutable
data class SunMoment(
    val type: AlertType,
    val time: ZonedDateTime,
    val alert: Alert?
)

class SunMomentService @Inject constructor(
    private val upcomingForecast: UpcomingForecast,
    private val repository: AlertConfigRepository,
    private val domainScheduler: AlertScheduler
) {
    val moments: Flow<SunMoment> = repository.configs.parallelMap { config ->
        val forecast = upcomingForecast.get()

        SunMoment(
            type = config.type,
            time = forecast.getTime(config.type),
            alert = forecast.createAlert(config)
        )
    }

    suspend fun setEnabled(type: AlertType, isEnabled: Boolean) {
        val config = applyChange(type) { it.copy(isEnabled = isEnabled) }
        domainScheduler.setSchedule(config)
    }

    suspend fun setNoticePeriod(type: AlertType, noticePeriod: Duration) {
        val config = applyChange(type) { it.copy(noticePeriod = noticePeriod) }
        domainScheduler.setSchedule(config)
    }

    private suspend fun applyChange(
        type: AlertType,
        block: (oldConfig: AlertConfig) -> AlertConfig
    ): AlertConfig {
        val oldConfig = repository.load(type).getOrThrow() // TODO: Deal with error

        val newConfig = block(oldConfig)

        repository.save(newConfig) // TODO: Deal with error

        return newConfig
    }
}
