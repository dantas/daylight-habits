package com.damiandantas.daylighthabits.system

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.PendingIntentCompat
import com.damiandantas.daylighthabits.domain.AlarmScheduler
import dagger.hilt.android.AndroidEntryPoint
import java.time.ZonedDateTime
import javax.inject.Inject

// TODO: Replace with an Activity
@AndroidEntryPoint
class AlarmReceiver @Inject constructor() : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val type = AlarmSchedulerAlarmType.getAlarmType(intent)
        Log.i("wasd", "TRIGGERING ALARM: $type")
    }
}

@AndroidEntryPoint
class AlarmSchedulerBootReceiver @Inject constructor() : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // TODO: Reschedule alarms because we lost them after reboot
    }
}

@AndroidEntryPoint
class AlarmSchedulerPackageReplaced @Inject constructor() : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.i("wasd", "PACKAGE REPLACED")
        // TODO: Reschedule alarms because we lost them after reinstall
    }
}

class AlarmManagerScheduler(
    private val context: Context,
    private val alarmType: AlarmSchedulerAlarmType,
) : AlarmScheduler {
    override fun schedule(time: ZonedDateTime): Boolean =
        try {
            context.getSystemService(AlarmManager::class.java)
                .setExact(
                    AlarmManager.RTC_WAKEUP,
                    time.toEpochSecond() * 1000L,
                    pendingIntent()
                )

            true
        } catch (e: SecurityException) {
            false
        }

    override fun unschedule() {
        context.getSystemService(AlarmManager::class.java).cancel(pendingIntent())
    }

    private fun pendingIntent(): PendingIntent {
        val intent = Intent(context, AlarmReceiver::class.java)

        alarmType.setParam(intent)

        return PendingIntentCompat.getBroadcast(
            context, 0, intent, 0, false
        )
    }
}

enum class AlarmSchedulerAlarmType {
    Sunrise,
    Sunset;

    fun setParam(intent: Intent) {
        intent.putExtra(KEY, toString())
    }

    companion object {
        private const val KEY = "type"

        fun getAlarmType(intent: Intent): AlarmSchedulerAlarmType? {
            val stringId = intent.getStringExtra(KEY) ?: return null
            return AlarmSchedulerAlarmType.valueOf(stringId)
        }
    }
}
