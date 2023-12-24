package com.damiandantas.daylighthabits.modules.alert

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.PendingIntentCompat
import com.damiandantas.daylighthabits.modules.alert.executor.AlertExecutor
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

// TODO: Replace BroadcastReceiver with Activity

@AndroidEntryPoint
class AlertReceiver @Inject constructor(
    private val alertExecutor: AlertExecutor
) : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
//        Log.i("wasd", "Alert ${getAlertType(intent)}")
        alertExecutor.execute()
    }
}

fun scheduleAlertIntent(context: Context, type: AlertType): PendingIntent =
    PendingIntentCompat.getBroadcast(
        context, 0, receiverIntent(context, type), 0, false
    )!!

fun unscheduleAlertIntent(context: Context, type: AlertType): PendingIntent? =
    PendingIntentCompat.getBroadcast(
        context, 0,
        receiverIntent(context, type),
        PendingIntent.FLAG_NO_CREATE, false
    )

/*
        Since scheduled alarms are lost when the app is reinstalled, we can use AlertType.name
    without worrying about refactoring that changes the enum names
 */
private fun receiverIntent(context: Context, type: AlertType): Intent =
    Intent(context, AlertReceiver::class.java).apply {
        action = type.name
    }

private fun getAlertType(intent: Intent): AlertType? =
    runCatching { AlertType.valueOf(intent.action ?: "") }.getOrNull()
