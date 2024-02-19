package com.damiandantas.daylighthabits.modules.alert.schedule

import android.app.AlarmManager
import android.content.Context
import com.damiandantas.daylighthabits.modules.alert.AlertType
import com.damiandantas.daylighthabits.modules.alert.receiver.scheduleAlertIntent
import com.damiandantas.daylighthabits.modules.alert.receiver.unscheduleAlertIntent
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.ZonedDateTime
import javax.inject.Inject

interface SystemAlertScheduler {
    suspend fun schedule(type: AlertType, time: ZonedDateTime)
    suspend fun cancel(type: AlertType)
}

@Module
@InstallIn(SingletonComponent::class)
private interface SystemAlertSchedulerModule {
    @Binds
    fun bindSystemScheduler(device: DeviceSystemAlertScheduler): SystemAlertScheduler
}

private class DeviceSystemAlertScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) : SystemAlertScheduler {
    override suspend fun schedule(type: AlertType, time: ZonedDateTime) =
        withContext(Dispatchers.IO) {
            val pendingIntent = scheduleAlertIntent(context, type)

            context.alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                time.toEpochSecond() * 1000L,
                pendingIntent
            )
        }

    override suspend fun cancel(type: AlertType) {
        val pendingIntent = unscheduleAlertIntent(context, type) ?: return

        context.alarmManager.cancel(pendingIntent)
    }

    private val Context.alarmManager
        get() = this.getSystemService(AlarmManager::class.java)
}