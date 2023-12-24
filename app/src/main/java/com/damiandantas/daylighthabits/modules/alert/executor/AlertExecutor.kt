package com.damiandantas.daylighthabits.modules.alert.executor

import javax.inject.Inject

// TODO: Read settings

class AlertExecutor @Inject constructor(
    private val audio: AlertAudio,
    private val vibration: AlertVibration
) {
    fun execute() {
        audio.play()
        vibration.vibrate()
    }

    fun stop() {
        audio.stop()
        vibration.stop()
    }
}