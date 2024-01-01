package com.damiandantas.daylighthabits.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.damiandantas.daylighthabits.modules.alert.schedule.AlertRescheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppScreenViewModel @Inject constructor(
    private val rescheduler: AlertRescheduler
) : ViewModel() {
    init {
        viewModelScope.launch {
            rescheduler.reschedule()
        }
    }
}