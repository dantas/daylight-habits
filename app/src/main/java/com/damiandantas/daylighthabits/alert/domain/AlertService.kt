package com.damiandantas.daylighthabits.alert.domain

import com.damiandantas.daylighthabits.common.di.Sunrise
import com.damiandantas.daylighthabits.common.di.Sunset
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class AlertService(
    private val repository: AlertRepository,
    private val scheduler: AlertScheduler
) {
    val isEnabled: Flow<Boolean> = scheduler.isScheduled
    val alert: Flow<Alert?> = repository.alert

    suspend fun setEnabled(isEnabled: Boolean) {
        if (isEnabled) {
            val alert = alert.first() ?: return
            scheduler.schedule(alert) // TODO: Check for error
        } else {
            scheduler.unschedule()
        }
    }

    suspend fun setAlert(alert: Alert) {
        repository.save(alert)    // TODO: Check for error
        scheduler.schedule(alert) // TODO: Check for error
    }
}

interface AlertRepository {
    val alert: Flow<Alert?>
    suspend fun save(alert: Alert): Result<Unit>
}

interface AlertScheduler {
    val isScheduled: Flow<Boolean>
    suspend fun schedule(alert: Alert)
    suspend fun unschedule()
}

@Module
@InstallIn(SingletonComponent::class)
object AlertServiceModule {
    @Sunrise
    @Provides
    fun provideSunriseAlertService(
        @Sunrise repository: AlertRepository,
        @Sunrise scheduler: AlertScheduler
    ): AlertService = AlertService(repository, scheduler)

    @Sunset
    @Provides
    fun provideSunsetAlertService(
        @Sunset repository: AlertRepository,
        @Sunset scheduler: AlertScheduler
    ): AlertService = AlertService(repository, scheduler)
}
