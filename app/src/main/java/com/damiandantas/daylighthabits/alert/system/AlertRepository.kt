package com.damiandantas.daylighthabits.alert.system

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.damiandantas.daylighthabits.alert.domain.Alert
import com.damiandantas.daylighthabits.alert.domain.AlertRepository
import com.damiandantas.daylighthabits.common.di.Sunrise
import com.damiandantas.daylighthabits.common.di.Sunset
import com.damiandantas.daylighthabits.common.suspendRunCatching
import com.damiandantas.daylighthabits.data.AlertProto
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
import java.time.Duration
import java.time.ZonedDateTime
import java.time.format.DateTimeParseException
import javax.inject.Singleton

class AlertRepositoryDataStore(
    private val alertDataStore: DataStore<AlertProto>
) : AlertRepository {
    override val alert: Flow<Alert?> = alertDataStore.data.map { proto ->
        Alert(
            time = try {
                ZonedDateTime.parse(proto.time)
            } catch (e: DateTimeParseException) {
                return@map null
            },
            noticeTime = Duration.ofMillis(proto.noticeTime)
        )
    }

    override suspend fun save(alert: Alert): Result<Unit> = suspendRunCatching {
        alertDataStore.updateData {
            it.toBuilder().apply {
                time = alert.time.toString()
                noticeTime = alert.noticeTime.toMillis()
            }.build()
        }
    }
}

@Module
@InstallIn(SingletonComponent::class)
object AlertRepositoryModule {
    @Sunrise
    @Provides
    @Singleton
    fun provideSunriseAlertRepository(@ApplicationContext context: Context): AlertRepository =
        AlertRepositoryDataStore(context.sunriseAlertDataStore)

    @Sunset
    @Provides
    @Singleton
    fun provideSunsetAlertRepository(@ApplicationContext context: Context): AlertRepository =
        AlertRepositoryDataStore(context.sunsetAlertDataStore)
}

private val Context.sunriseAlertDataStore: DataStore<AlertProto> by dataStore(
    fileName = "sunrise_alert.pb",
    serializer = AlertSerializer
)

private val Context.sunsetAlertDataStore: DataStore<AlertProto> by dataStore(
    fileName = "sunset_alert.pb",
    serializer = AlertSerializer
)

private object AlertSerializer : Serializer<AlertProto> {
    override val defaultValue: AlertProto = AlertProto.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): AlertProto =
        try {
            AlertProto.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto", exception)
        }

    override suspend fun writeTo(t: AlertProto, output: OutputStream) {
        t.writeTo(output)
    }
}