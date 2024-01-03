package com.damiandantas.daylighthabits.modules.alert.notifier

import android.content.Context
import android.media.RingtoneManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject

interface AlertAudio {
    fun play()
    fun stop()
}

@Module
@InstallIn(SingletonComponent::class)
private interface AlertAudioModule {
    @Binds
    fun bindAlertAudio(deviceAudio: DeviceAlertAudio): AlertAudio
}

private class DeviceAlertAudio @Inject constructor(
    @ApplicationContext private val context: Context
) : AlertAudio {
    private val manager = RingtoneManager(context)
    private val ringtone = manager.getRingtone(RingtoneManager.TYPE_ALARM)

    override fun play() {
        ringtone.play()
    }

    override fun stop() {
        ringtone.stop()
    }
}