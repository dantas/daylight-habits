package com.damiandantas.daylighthabits.alert.system

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.PendingIntentCompat
import com.damiandantas.daylighthabits.alert.domain.SunMomentType
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

fun Context.getPendingIntent(type: SunMomentType): PendingIntent {
    val cls = when (type) {
        SunMomentType.SUNRISE -> SunriseAlertReceiver::class.java
        SunMomentType.SUNSET -> SunsetAlertReceiver::class.java
    }

    return PendingIntentCompat.getBroadcast(
        this, 0, Intent(this, cls), 0, false
    )
}