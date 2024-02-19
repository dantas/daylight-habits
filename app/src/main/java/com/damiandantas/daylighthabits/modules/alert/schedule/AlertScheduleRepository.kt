package com.damiandantas.daylighthabits.modules.alert.schedule

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.damiandantas.daylighthabits.modules.alert.AlertSchedule
import com.damiandantas.daylighthabits.modules.alert.AlertType
import com.damiandantas.daylighthabits.proto.AlertScheduleProto
import com.damiandantas.daylighthabits.proto.AlertScheduleRepositoryProto
import com.google.protobuf.InvalidProtocolBufferException
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.retryWhen
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.time.Duration
import javax.inject.Inject

interface AlertScheduleRepository {
    val schedules: Flow<Result<AlertSchedule>>
    suspend fun save(schedule: AlertSchedule): Boolean
    suspend fun load(type: AlertType): AlertSchedule? // Null in case of error
}

@Module
@InstallIn(SingletonComponent::class)
private interface AlertScheduleRepositoryModule {
    @Binds
    fun bindAlertScheduleRepository(device: DeviceAlertScheduleRepository): AlertScheduleRepository
}

private class DeviceAlertScheduleRepository @Inject constructor(
    @ApplicationContext private val context: Context
) : AlertScheduleRepository {
    override val schedules: Flow<Result<AlertSchedule>> =
        flow {
            context.alertScheduleDataStore.data.collect { repositoryProto ->
                emit(Result.success(repositoryProto.sunriseAlertSchedule))
                emit(Result.success(repositoryProto.sunsetAlertSchedule))
            }
        }.retryWhen { cause, attempt ->
            val retry = attempt < 2 // Sporadic FS error?
            if (retry) delay(100) else emit(Result.failure(cause))
            retry
        }

    override suspend fun save(schedule: AlertSchedule): Boolean {
        val operation =
            { repositoryProto: AlertScheduleRepositoryProto ->
                val builder = repositoryProto.toBuilder()

                when (schedule.type) {
                    AlertType.SUNRISE -> {
                        builder.hasSunrise = true
                        builder.sunrise = schedule.toAlertScheduleProto()
                    }

                    AlertType.SUNSET -> {
                        builder.hasSunset = true
                        builder.sunset = schedule.toAlertScheduleProto()
                    }
                }

                builder.build()
            }

        return runCatching {
            context.alertScheduleDataStore.updateData(operation)
        }.isSuccess
    }

    override suspend fun load(type: AlertType): AlertSchedule? {
        val repositoryProto =
            try {
                context.alertScheduleDataStore.data.first()
            } catch (_: IOException) {
                return null
            }

        return when (type) {
            AlertType.SUNRISE -> repositoryProto.sunriseAlertSchedule
            AlertType.SUNSET -> repositoryProto.sunsetAlertSchedule
        }
    }
}

private val AlertScheduleRepositoryProto.sunriseAlertSchedule: AlertSchedule
    get() = if (hasSunrise) {
        sunrise.toAlertSchedule(AlertType.SUNRISE)
    } else {
        AlertSchedule(AlertType.SUNRISE)
    }

private val AlertScheduleRepositoryProto.sunsetAlertSchedule: AlertSchedule
    get() = if (hasSunset) {
        sunset.toAlertSchedule(AlertType.SUNSET)
    } else {
        AlertSchedule(AlertType.SUNSET)
    }

private fun AlertScheduleProto.toAlertSchedule(type: AlertType): AlertSchedule =
    AlertSchedule(
        type = type,
        noticePeriod = Duration.ofMillis(noticePeriod),
        isEnabled = isEnabled
    )

private fun AlertSchedule.toAlertScheduleProto(): AlertScheduleProto {
    val builder = AlertScheduleProto.newBuilder()

    builder.noticePeriod = noticePeriod.toMillis()
    builder.isEnabled = isEnabled

    return builder.build()
}

private val Context.alertScheduleDataStore: DataStore<AlertScheduleRepositoryProto> by dataStore(
    fileName = "alert_schedule_repository.pb",
    serializer = AlertScheduleSerializer
)

private object AlertScheduleSerializer : Serializer<AlertScheduleRepositoryProto> {
    override val defaultValue: AlertScheduleRepositoryProto =
        AlertScheduleRepositoryProto.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): AlertScheduleRepositoryProto =
        try {
            AlertScheduleRepositoryProto.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto", exception)
        }

    override suspend fun writeTo(t: AlertScheduleRepositoryProto, output: OutputStream) {
        t.writeTo(output)
    }
}