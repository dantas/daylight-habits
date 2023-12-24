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
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

interface AlertSettingsRepository {
    suspend fun save(settings: AlertSettings): Boolean
    suspend fun load(): AlertSettings? // Null in case of error
}

@Module
@InstallIn(SingletonComponent::class)
private interface AlertSettingsRepositoryModule {
    @Binds
    fun bindAlertSettingsRepository(device: DeviceAlertSettingsRepository): AlertSettingsRepository
}

private class DeviceAlertSettingsRepository @Inject constructor(
    private val context: Context
) : AlertSettingsRepository {
    override suspend fun save(settings: AlertSettings): Boolean =
        try {
            context.alertSettingsDataStore.updateData { proto ->
                proto.toBuilder().apply {
                    vibrate = settings.vibrate
                    sound = settings.sound
                }.build()
            }

            true
        } catch (e: IOException) {
            false
        }

    override suspend fun load(): AlertSettings? =
        try {
            val proto = context.alertSettingsDataStore.data.first()
            AlertSettings(vibrate = proto.vibrate, sound = proto.sound)
        } catch (e: IOException) {
            null
        }
}

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