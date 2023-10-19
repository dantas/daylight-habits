package com.damiandantas.daylighthabits.alert.system

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.PendingIntentCompat
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

// TODO: Replace BroadcastReceiver with Activity

@AndroidEntryPoint
class SunriseAlertReceiver @Inject constructor() : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.i("wasd", "Sunrise alert")
    }

    companion object {
        fun pendingIntent(context: Context): PendingIntent {
            val intent = Intent(context, SunriseAlertReceiver::class.java)
            return PendingIntentCompat.getBroadcast(
                context, 0, intent, 0, false
            )
        }
    }
}

@AndroidEntryPoint
class SunsetAlertReceiver @Inject constructor() : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.i("wasd", "Sunset alert")
    }

    companion object {
        fun pendingIntent(context: Context): PendingIntent {
            val intent = Intent(context, SunsetAlertReceiver::class.java)
            return PendingIntentCompat.getBroadcast(
                context, 0, intent, 0, false
            )
        }
    }
}