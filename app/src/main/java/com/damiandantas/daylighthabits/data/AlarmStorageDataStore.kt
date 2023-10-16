package com.damiandantas.daylighthabits.data

import androidx.datastore.core.DataStore
import com.damiandantas.daylighthabits.domain.AlarmStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Duration
import java.util.concurrent.CancellationException

class AlarmStorageDataStore(
    private val alarmStorageDataStore: DataStore<AlarmProtoStorage>
) : AlarmStorage {
    override val isEnabled: Flow<Boolean> =
        alarmStorageDataStore.data.map { it.isEnabled }

    override suspend fun setEnabled(enabled: Boolean): Result<Unit> = suspendRunCatching {
        alarmStorageDataStore.updateData {
            it.toBuilder().setIsEnabled(enabled).build()
        }
    }

    override val timerDuration: Flow<Duration> =
        alarmStorageDataStore.data.map { Duration.ofMillis(it.duration) }

    override suspend fun setTimerDuration(duration: Duration): Result<Unit> = suspendRunCatching {
        alarmStorageDataStore.updateData {
            it.toBuilder().setDuration(duration.toMillis()).build()
        }
    }
}

private suspend fun <T> suspendRunCatching(block: suspend () -> T): Result<T> =
    try {
        Result.success(block())
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        Result.failure(e)
    }
