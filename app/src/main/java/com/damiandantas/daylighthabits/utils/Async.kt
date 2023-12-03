package com.damiandantas.daylighthabits.utils

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.map

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
