package com.damiandantas.daylighthabits.modules.alert.settings

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.damiandantas.daylighthabits.modules.alert.AlertSettings
import com.damiandantas.daylighthabits.proto.AlertSettingsRepositoryProto
import com.google.protobuf.InvalidProtocolBufferException
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.retryWhen
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

interface AlertSettingsRepository {
    val settings: Flow<Result<AlertSettings>>
    suspend fun save(settings: AlertSettings): Boolean
}

@Module
@InstallIn(SingletonComponent::class)
private interface AlertSettingsRepositoryModule {
    @Binds
    fun bindAlertSettingsRepository(device: DeviceAlertSettingsRepository): AlertSettingsRepository
}

private class DeviceAlertSettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) : AlertSettingsRepository {
    override val settings: Flow<Result<AlertSettings>> = flow {
        context.alertSettingsDataStore.data.collect { proto ->
            emit(Result.success(proto.toSettings()))
        }
    }.retryWhen { cause, attempt ->
        val retry = attempt < 2 // Sporadic FS error?
        if (retry) delay(100) else emit(Result.failure(cause))
        retry
    }

    override suspend fun save(settings: AlertSettings): Boolean =
        runCatching {
            context.alertSettingsDataStore.updateData { proto ->
                proto.protoFrom(settings)
            }
        }.isSuccess
}

private fun AlertSettingsRepositoryProto.toSettings(): AlertSettings =
    AlertSettings(vibrate = vibrate, sound = sound)

private fun AlertSettingsRepositoryProto.protoFrom(settings: AlertSettings): AlertSettingsRepositoryProto =
    toBuilder().apply {
        vibrate = settings.vibrate
        sound = settings.sound
    }.build()

private val Context.alertSettingsDataStore: DataStore<AlertSettingsRepositoryProto> by dataStore(
    fileName = "alert_settings_repository.pb",
    serializer = AlertSettingsSerializer
)

private object AlertSettingsSerializer : Serializer<AlertSettingsRepositoryProto> {
    override val defaultValue: AlertSettingsRepositoryProto =
        AlertSettingsRepositoryProto.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): AlertSettingsRepositoryProto =
        try {
            AlertSettingsRepositoryProto.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto", exception)
        }

    override suspend fun writeTo(t: AlertSettingsRepositoryProto, output: OutputStream) {
        t.writeTo(output)
    }
}