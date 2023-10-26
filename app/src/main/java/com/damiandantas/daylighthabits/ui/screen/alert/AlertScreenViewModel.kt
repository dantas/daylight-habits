package com.damiandantas.daylighthabits.ui.screen.alert

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.damiandantas.daylighthabits.modules.SunMoment
import com.damiandantas.daylighthabits.modules.SunMomentService
import com.damiandantas.daylighthabits.modules.SunMomentType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Duration
import javax.inject.Inject

@HiltViewModel
class AlertScreenViewModel @Inject constructor(
    private val momentService: SunMomentService
) : ViewModel() {
    // TODO: momentService.moments it triggering multiple collections, use shareIn

    val sunrise: StateFlow<SunMoment?> =
        momentService.moments.filter { it.type == SunMomentType.SUNRISE }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    val sunset: StateFlow<SunMoment?> =
        momentService.moments.filter { it.type == SunMomentType.SUNSET }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    fun setSunriseEnabled(isEnabled: Boolean) {
        viewModelScope.launch {
            momentService.setEnabled(SunMomentType.SUNRISE, isEnabled)
        }
    }

    fun setSunsetEnabled(isEnabled: Boolean) {
        viewModelScope.launch {
            momentService.setEnabled(SunMomentType.SUNSET, isEnabled)
        }
    }

    fun setSunriseNoticePeriod(noticePeriod: Duration) {
        viewModelScope.launch {
            momentService.setNoticePeriod(SunMomentType.SUNRISE, noticePeriod)
        }
    }

    fun setSunsetNoticePeriod(noticePeriod: Duration) {
        viewModelScope.launch {
            momentService.setNoticePeriod(SunMomentType.SUNSET, noticePeriod)
        }
    }
}