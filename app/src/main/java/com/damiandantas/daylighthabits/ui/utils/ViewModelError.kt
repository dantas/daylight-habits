package com.damiandantas.daylighthabits.ui.utils

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

enum class ViewModelError {
    LOAD, UPDATE;

    companion object
}

typealias EventError = ViewModelEvent<ViewModelError>
typealias MutableStateEventError = MutableStateFlow<EventError>
typealias StateEventError = StateFlow<ViewModelEvent<ViewModelError>>

fun ViewModelError.mutableStateEventError(): MutableStateEventError =
    MutableStateFlow(ViewModelEvent(this, false))

fun ViewModelError.eventError(): EventError = ViewModelEvent(this)
