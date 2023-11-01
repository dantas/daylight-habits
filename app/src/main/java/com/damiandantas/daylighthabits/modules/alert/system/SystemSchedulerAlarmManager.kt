package com.damiandantas.daylighthabits.modules.alert.system

import android.app.AlarmManager
import android.content.Context
import com.damiandantas.daylighthabits.modules.Alert
import com.damiandantas.daylighthabits.modules.SunMomentType
import com.damiandantas.daylighthabits.modules.alert.domain.SystemScheduler
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SystemSchedulerAlarmManager @Inject constructor(
    @ApplicationContext private val context: Context
) : SystemScheduler {
    override suspend fun schedule(alert: Alert) = withContext(Dispatchers.IO) {
        val pendingIntent = scheduleAlertIntent(context, alert.config.type)

        context.alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            alert.time.toEpochSecond() * 1000L,
            pendingIntent
        )
    }

    override suspend fun unschedule(type: SunMomentType) {
        val pendingIntent = unscheduleAlertIntent(context, type) ?: return

        context.alarmManager.cancel(pendingIntent)
    }

    private val Context.alarmManager
        get() = this.getSystemService(AlarmManager::class.java)
}

@Module
@InstallIn(SingletonComponent::class)
interface SystemSchedulerModule {
    @Binds
    fun bindSystemScheduler(alarmManager: SystemSchedulerAlarmManager): SystemScheduler
}