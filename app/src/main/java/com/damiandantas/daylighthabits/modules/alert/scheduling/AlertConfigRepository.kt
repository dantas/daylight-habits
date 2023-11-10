package com.damiandantas.daylighthabits.modules.alert.scheduling

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.damiandantas.daylighthabits.modules.alert.AlertConfig
import com.damiandantas.daylighthabits.modules.alert.AlertType
import com.damiandantas.daylighthabits.proto.AlertConfigProto
import com.damiandantas.daylighthabits.proto.AlertConfigRepositoryProto
import com.damiandantas.daylighthabits.utils.suspendRunCatching
import com.google.protobuf.InvalidProtocolBufferException
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import java.io.InputStream
import java.io.OutputStream
import java.time.Duration
import javax.inject.Inject

interface AlertConfigRepository {
    val configs: Flow<AlertConfig>
    suspend fun save(config: AlertConfig): Result<Unit> // TODO: Check for error
    suspend fun load(type: AlertType): Result<AlertConfig> // TODO: Check for error
}

@Module
@InstallIn(SingletonComponent::class)
private interface AlertConfigRepositoryModule {
    @Binds
    fun bindAlertConfigRepository(dataStore: AlertConfigDataStore): AlertConfigRepository
}

private class AlertConfigDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) : AlertConfigRepository {
    private val dataStore = context.alertRepositoryDataStore

    override val configs: Flow<AlertConfig> = flow {
        dataStore.data.collect { repositoryProto ->
            emit(repositoryProto.sunriseAlertConfig)
            emit(repositoryProto.sunsetAlertConfig)
        }
    }

    override suspend fun save(config: AlertConfig): Result<Unit> = suspendRunCatching {
        dataStore.updateData { repositoryProto ->
            val builder = repositoryProto.toBuilder()

            when (config.type) {
                AlertType.SUNRISE -> {
                    builder.hasSunrise = true
                    builder.sunrise = config.toAlertConfigProto()
                }

                AlertType.SUNSET -> {
                    builder.hasSunset = true
                    builder.sunset = config.toAlertConfigProto()
                }
            }

            builder.build()
        }
    }

    override suspend fun load(type: AlertType): Result<AlertConfig> = suspendRunCatching {
        val repositoryProto = dataStore.data.first()

        when (type) {
            AlertType.SUNRISE -> repositoryProto.sunriseAlertConfig
            AlertType.SUNSET -> repositoryProto.sunsetAlertConfig
        }
    }
}

private val AlertConfigRepositoryProto.sunriseAlertConfig: AlertConfig
    get() = if (hasSunrise) {
        sunrise.toAlertConfig(AlertType.SUNRISE)
    } else {
        AlertConfig(AlertType.SUNRISE)
    }

private val AlertConfigRepositoryProto.sunsetAlertConfig: AlertConfig
    get() = if (hasSunset) {
        sunset.toAlertConfig(AlertType.SUNSET)
    } else {
        AlertConfig(AlertType.SUNSET)
    }

private fun AlertConfigProto.toAlertConfig(type: AlertType): AlertConfig =
    AlertConfig(
        type = type,
        noticePeriod = Duration.ofMillis(noticePeriod),
        isEnabled = isEnabled
    )

private fun AlertConfig.toAlertConfigProto(): AlertConfigProto {
    val builder = AlertConfigProto.newBuilder()

    builder.noticePeriod = noticePeriod.toMillis()
    builder.isEnabled = isEnabled

    return builder.build()
}

private val Context.alertRepositoryDataStore: DataStore<AlertConfigRepositoryProto> by dataStore(
    fileName = "alert_config_repository.pb",
    serializer = AlertRepositorySerializer
)

private object AlertRepositorySerializer : Serializer<AlertConfigRepositoryProto> {
    override val defaultValue: AlertConfigRepositoryProto =
        AlertConfigRepositoryProto.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): AlertConfigRepositoryProto =
        try {
            AlertConfigRepositoryProto.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto", exception)
        }

    override suspend fun writeTo(t: AlertConfigRepositoryProto, output: OutputStream) {
        t.writeTo(output)
    }
}