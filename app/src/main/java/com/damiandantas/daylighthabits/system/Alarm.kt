package com.damiandantas.daylighthabits.system

import android.app.AlarmManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.getSystemService
import java.time.ZonedDateTime

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        TODO("Not yet implemented")
    }
}

//interface AlarmScheduler {
//    fun schedule(dateTime: ZonedDateTime)
//    fun unschedule()
//}

class AlarmScheduler {
    fun schedule(context: Context, dateTime: ZonedDateTime) {
        // TODO: Think what to do here
        val alarmManager = context.getSystemService(AlarmManager::class.java)

//        val intent = Intent(context, AlarmReceiver::class.java)
//        val pendingIntent = PendingIntentCompat.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE, true)
//
//        dateTime.re
//        alarmManager.setExact(AlarmManager.RTC_WAKEUP, 3200, pendingIntent)
    }

    fun unschedule(context: Context) {
        val alarmManager = context.getSystemService<AlarmManager>() as AlarmManager

//        alarmManager.cancel()
    }

    private fun pendingIntent() {

    }
}