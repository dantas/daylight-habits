package com.damiandantas.daylighthabits.modules.alert.executor

import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject

interface AlertVibration {
    fun vibrate()
    fun stop()
}

@Module
@InstallIn(SingletonComponent::class)
interface AlertVibrationModule {
    @Binds
    fun bindAlertVibration(deviceVibration: DeviceAlertVibration): AlertVibration
}

class DeviceAlertVibration @Inject constructor(
    @ApplicationContext private val context: Context
) : AlertVibration {
    private val vibrator = context.getSystemService(Vibrator::class.java)

    override fun vibrate() {
        val timings: LongArray = longArrayOf(50, 50, 50, 50, 50, 100, 350, 25, 25, 25, 25, 200)
        val amplitudes: IntArray = intArrayOf(33, 51, 75, 113, 170, 255, 0, 38, 62, 100, 160, 255)
        val repeatIndex = 0

        vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, repeatIndex))
    }

    override fun stop() {
        vibrator.cancel()
    }
}