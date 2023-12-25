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

fun mutableStateEventError(): MutableStateEventError =
    MutableStateFlow(ViewModelEvent(ViewModelError.LOAD, true))

fun ViewModelError.eventError(): EventError = ViewModelEvent(this)
