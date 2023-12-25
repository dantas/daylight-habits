package com.damiandantas.daylighthabits.modules.alert.executor

import com.damiandantas.daylighthabits.modules.alert.settings.AlertSettingsRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class AlertExecutor @Inject constructor(
    private val audio: AlertAudio,
    private val vibration: AlertVibration,
    private val repository: AlertSettingsRepository
) {
    suspend fun execute() {
        val settings = repository.settings.first().getOrNull() ?: return

        if (settings.vibrate) {
            vibration.vibrate()
        }

        if (settings.sound) {
            audio.play()
        }
    }

    fun stop() {
        audio.stop()
        vibration.stop()
    }
}