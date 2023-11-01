package com.damiandantas.daylighthabits.modules.alert.system

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.PendingIntentCompat
import com.damiandantas.daylighthabits.modules.SunMomentType
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

// TODO: Replace BroadcastReceiver with Activity

@AndroidEntryPoint
class SunriseAlertReceiver @Inject constructor() : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.i("wasd", "Sunrise alert")
    }
}

@AndroidEntryPoint
class SunsetAlertReceiver @Inject constructor() : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.i("wasd", "Sunset alert")
    }
}

fun scheduleAlertIntent(context: Context, type: SunMomentType): PendingIntent =
    PendingIntentCompat.getBroadcast(
        context, 0, receiverIntent(context, type), 0, false
    )!!

fun unscheduleAlertIntent(context: Context, type: SunMomentType): PendingIntent? =
    PendingIntentCompat.getBroadcast(
        context, 0,
        receiverIntent(context, type),
        PendingIntent.FLAG_NO_CREATE, false
    )

private fun receiverIntent(context: Context, type: SunMomentType): Intent =
    Intent(
        context,
        when (type) {
            SunMomentType.SUNRISE -> SunriseAlertReceiver::class.java
            SunMomentType.SUNSET -> SunsetAlertReceiver::class.java
        }
    )
