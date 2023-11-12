package com.damiandantas.daylighthabits.utils

import com.damiandantas.daylighthabits.R
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.map
import java.util.concurrent.CancellationException

suspend inline fun <T> suspendRunCatching(crossinline block: suspend () -> T): Result<T> =
    try {
        Result.success(block())
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        Result.failure(e)
    }

inline fun <T, R> Flow<T>.parallelMap(crossinline transform: suspend (value: T) -> R): Flow<R> =
    channelFlow {
        coroutineScope {
            this@parallelMap.collect { value ->
                val deferred = async {
                    transform(value)
                }

                send(deferred)
            }
        }

        close()
    }.map { it.await() }
