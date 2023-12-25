package com.damiandantas.daylighthabits.modules.alert.settings

import com.damiandantas.daylighthabits.modules.alert.AlertSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class AlertSettingsService @Inject constructor(
    private val settingsRepository: AlertSettingsRepository
) {
    val settings: Flow<Result<AlertSettings>> = settingsRepository.settings

    suspend fun setVibrate(isEnabled: Boolean): Boolean =
        updateSavedSettings { it.copy(vibrate = isEnabled) }

    suspend fun setSound(isEnabled: Boolean): Boolean =
        updateSavedSettings { it.copy(sound = isEnabled) }

    private suspend fun updateSavedSettings(
        transform: (old: AlertSettings) -> AlertSettings
    ): Boolean {
        val old = settingsRepository.settings.first().getOrElse { return false }

        val new = transform(old)

        return settingsRepository.save(new)
    }
}