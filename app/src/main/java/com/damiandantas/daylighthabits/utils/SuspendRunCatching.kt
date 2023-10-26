package com.damiandantas.daylighthabits.utils

import java.util.concurrent.CancellationException

suspend fun <T> suspendRunCatching(block: suspend () -> T): Result<T> =
    try {
        Result.success(block())
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        Result.failure(e)
    }
