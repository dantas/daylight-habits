package com.damiandantas.daylighthabits.data

import androidx.datastore.core.DataStore
import com.damiandantas.daylighthabits.domain.AlarmStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import java.time.Duration

class AlarmStorageDataStore(
    private val alarmStorageDataStore: DataStore<AlarmProtoStorage>
) : AlarmStorage {
    override suspend fun enable() {
        alarmStorageDataStore.updateData {
            it.toBuilder().setIsEnabled(true).build()
        }
    }

    override suspend fun disable() {
        alarmStorageDataStore.updateData {
            it.toBuilder().setIsEnabled(false).build()
        }
    }

    override suspend fun isEnabled(): Boolean =
        alarmStorageDataStore.data.map {
            it.isEnabled
        }.first()

    override suspend fun sleepDuration(): Duration? =
        alarmStorageDataStore.data.map {
            if (it.duration == 0L) null else Duration.ofMillis(it.duration)
        }.first()

    override suspend fun setSleepDuration(duration: Duration) {
        alarmStorageDataStore.updateData {
            it.toBuilder().setDuration(duration.toMillis()).build()
        }
    }
}