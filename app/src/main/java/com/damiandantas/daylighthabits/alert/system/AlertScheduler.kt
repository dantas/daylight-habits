package com.damiandantas.daylighthabits.alert.system

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.damiandantas.daylighthabits.alert.domain.Alert
import com.damiandantas.daylighthabits.alert.domain.AlertScheduler
import com.damiandantas.daylighthabits.common.di.Sunrise
import com.damiandantas.daylighthabits.common.di.Sunset
import com.damiandantas.daylighthabits.common.suspendRunCatching
import com.damiandantas.daylighthabits.proto.AlertSchedulerProto
import com.google.protobuf.InvalidProtocolBufferException
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.InputStream
import java.io.OutputStream

// TODO: Check how to reschedule alarms

class AlertSchedulerAlarmManager(
    private val alarmManager: AlarmManager,
    private val pendingIntent: PendingIntent,
    private val dataStore: DataStore<AlertSchedulerProto>
) : AlertScheduler {
    override val isScheduled: Flow<Boolean> = dataStore.data.map { it.isScheduled }

    override suspend fun schedule(alert: Alert) {
        saveScheduled(true)// TODO: Check for error

        try {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                alert.time.toEpochSecond() * 1000L,
                pendingIntent
            )
        } catch (e: SecurityException) {
            // TODO: Suppress error?
        }
    }

    override suspend fun unschedule() {
        saveScheduled(false) // TODO: Check for error
        alarmManager.cancel(pendingIntent)
    }

    private suspend fun saveScheduled(scheduled: Boolean): Result<Unit> = suspendRunCatching {
        dataStore.updateData {
            it.toBuilder().setIsScheduled(scheduled).build()
        }
    }
}

@Module
@InstallIn(SingletonComponent::class)
object AlertSchedulerModule {
    @Sunrise
    @Provides
    fun provideSunriseAlertScheduler(@ApplicationContext context: Context): AlertScheduler =
        AlertSchedulerAlarmManager(
            alarmManager = context.androidAlarmManager(),
            pendingIntent = SunriseAlertReceiver.pendingIntent(context),
            dataStore = context.sunriseAlertSchedulerDataStore,
        )

    @Sunset
    @Provides
    fun provideSunsetAlertScheduler(@ApplicationContext context: Context): AlertScheduler =
        AlertSchedulerAlarmManager(
            alarmManager = context.androidAlarmManager(),
            pendingIntent = SunsetAlertReceiver.pendingIntent(context),
            dataStore = context.sunsetAlertSchedulerDataStore,
        )

    private fun Context.androidAlarmManager() = getSystemService(AlarmManager::class.java)
}

private val Context.sunriseAlertSchedulerDataStore: DataStore<AlertSchedulerProto> by dataStore(
    fileName = "sunrise_alert_scheduler.pb",
    serializer = AlertSchedulerSerializer
)

private val Context.sunsetAlertSchedulerDataStore: DataStore<AlertSchedulerProto> by dataStore(
    fileName = "sunset_alert_scheduler.pb",
    serializer = AlertSchedulerSerializer
)

private object AlertSchedulerSerializer : Serializer<AlertSchedulerProto> {
    override val defaultValue: AlertSchedulerProto = AlertSchedulerProto.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): AlertSchedulerProto =
        try {
            AlertSchedulerProto.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto", exception)
        }

    override suspend fun writeTo(t: AlertSchedulerProto, output: OutputStream) {
        t.writeTo(output)
    }
}
