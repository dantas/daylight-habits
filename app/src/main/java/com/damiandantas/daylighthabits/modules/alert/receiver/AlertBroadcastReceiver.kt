package com.damiandantas.daylighthabits.modules.alert.receiver

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.PendingIntentCompat
import com.damiandantas.daylighthabits.modules.alert.AlertType
import com.damiandantas.daylighthabits.modules.alert.notifier.AlertNotifierActivity
import com.damiandantas.daylighthabits.utils.alertType
import com.damiandantas.daylighthabits.utils.put
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AlertBroadcastReceiver : BroadcastReceiver() {
    @Inject
    lateinit var executor: AlertExecutor

    @OptIn(DelicateCoroutinesApi::class)
    override fun onReceive(context: Context, intent: Intent) {
        val type = intent.alertType ?: return // TODO: Track this?

        GlobalScope.launch {
            if (executor.execute(type) == AlertExecutor.ShouldTriggerEvent) {
                val activityIntent =
                    AlertNotifierActivity.intent(context, type)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

                context.startActivity(activityIntent)
            }
        }
    }
}

fun scheduleAlertIntent(context: Context, type: AlertType): PendingIntent =
    getPendingIntent(context, type, false)!!

fun cancelAlertIntent(context: Context, type: AlertType): PendingIntent? =
    getPendingIntent(context, type, true)

/*
        Since scheduled alarms are lost when the app is reinstalled, we can use AlertType.name
    without worrying about refactoring that changes the enum names
 */
private fun getPendingIntent(context: Context, type: AlertType, noCreate: Boolean): PendingIntent? {
    val intent = Intent(context, AlertBroadcastReceiver::class.java).put(type)

    return PendingIntentCompat.getBroadcast(
        context, 0, intent,
        if (noCreate) PendingIntent.FLAG_NO_CREATE else 0, false
    )
}
