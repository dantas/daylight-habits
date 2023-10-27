package com.damiandantas.daylighthabits.modules.alert.system

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.damiandantas.daylighthabits.modules.AlertConfig
import com.damiandantas.daylighthabits.modules.SunMomentType
import com.damiandantas.daylighthabits.modules.alert.domain.AlertConfigRepository
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

class AlertConfigDataStore @Inject constructor(
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
                SunMomentType.SUNRISE -> {
                    builder.hasSunrise = true
                    builder.sunrise = config.toAlertConfigProto()
                }

                SunMomentType.SUNSET -> {
                    builder.hasSunset = true
                    builder.sunset = config.toAlertConfigProto()
                }
            }

            builder.build()
        }
    }

    override suspend fun load(type: SunMomentType): Result<AlertConfig> = suspendRunCatching {
        val repositoryProto = dataStore.data.first()

        when (type) {
            SunMomentType.SUNRISE -> repositoryProto.sunriseAlertConfig
            SunMomentType.SUNSET -> repositoryProto.sunsetAlertConfig
        }
    }
}

private val AlertConfigRepositoryProto.sunriseAlertConfig: AlertConfig
    get() = if (hasSunrise) {
        sunrise.toAlertConfig(SunMomentType.SUNRISE)
    } else {
        AlertConfig(SunMomentType.SUNRISE)
    }

private val AlertConfigRepositoryProto.sunsetAlertConfig: AlertConfig
    get() = if (hasSunset) {
        sunset.toAlertConfig(SunMomentType.SUNSET)
    } else {
        AlertConfig(SunMomentType.SUNSET)
    }

private fun AlertConfigProto.toAlertConfig(type: SunMomentType): AlertConfig =
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

@Module
@InstallIn(SingletonComponent::class)
interface AlertConfigRepositoryModule {
    @Binds
    fun bindAlertConfigRepository(dataStore: AlertConfigDataStore): AlertConfigRepository
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