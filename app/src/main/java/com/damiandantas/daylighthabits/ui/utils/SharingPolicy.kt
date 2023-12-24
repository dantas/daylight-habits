package com.damiandantas.daylighthabits.ui.utils

import kotlinx.coroutines.flow.SharingStarted

val flowSharingPolicy get() = SharingStarted.WhileSubscribed(5_000)
