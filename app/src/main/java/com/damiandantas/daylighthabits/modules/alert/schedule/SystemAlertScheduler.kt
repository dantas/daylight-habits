package com.damiandantas.daylighthabits.modules.alert.schedule

import android.app.AlarmManager
import android.content.Context
import com.damiandantas.daylighthabits.modules.alert.AlertTime
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

interface SystemAlertScheduler {
    suspend fun schedule(alertTime: AlertTime)
    suspend fun unschedule(type: AlertType)
}

@Module
@InstallIn(SingletonComponent::class)
private interface SystemAlertSchedulerModule {
    @Binds
    fun bindSystemScheduler(alarmManager: SystemAlertSchedulerAlarmManager): SystemAlertScheduler
}

private class SystemAlertSchedulerAlarmManager @Inject constructor(
    @ApplicationContext private val context: Context
) : SystemAlertScheduler {
    override suspend fun schedule(alertTime: AlertTime) = withContext(Dispatchers.IO) {
        val pendingIntent = scheduleAlertIntent(context, alertTime.type)

        context.alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            alertTime.time.toEpochSecond() * 1000L,
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