package com.damiandantas.daylighthabits.modules.alert.schedule

import android.app.AlarmManager
import android.content.Context
import com.damiandantas.daylighthabits.modules.alert.Alert
import com.damiandantas.daylighthabits.modules.alert.AlertType
import com.damiandantas.daylighthabits.modules.alert.scheduleAlertIntent
import com.damiandantas.daylighthabits.modules.alert.unscheduleAlertIntent
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface SystemScheduler {
    suspend fun schedule(alert: Alert)
    suspend fun unschedule(type: AlertType)
}

@Module
@InstallIn(SingletonComponent::class)
private interface SystemSchedulerModule {
    @Binds
    fun bindSystemScheduler(alarmManager: SystemSchedulerAlarmManager): SystemScheduler
}

private class SystemSchedulerAlarmManager @Inject constructor(
    @ApplicationContext private val context: Context
) : SystemScheduler {
    override suspend fun schedule(alert: Alert) = withContext(Dispatchers.IO) {
        val pendingIntent = scheduleAlertIntent(context, alert.schedule.type)

        context.alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            alert.time.toEpochSecond() * 1000L,
            pendingIntent
        )
    }

    override suspend fun unschedule(type: AlertType) {
        val pendingIntent = unscheduleAlertIntent(context, type) ?: return

        context.alarmManager.cancel(pendingIntent)
    }

    private val Context.alarmManager
        get() = this.getSystemService(AlarmManager::class.java)
}