package com.damiandantas.daylighthabits.ui.utils

import androidx.compose.runtime.Stable

@Stable
data class ViewModelEvent<T>(
    private val value: T,
    private var isConsumed: Boolean = false
) {
    fun consume(block: (T) -> Unit) {
        if (!isConsumed) {
            isConsumed = true
            block(value)
        }
    }
}