package com.damiandantas.daylighthabits.ui.screen.alert

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.damiandantas.daylighthabits.modules.SunMoment
import com.damiandantas.daylighthabits.modules.SunMomentService
import com.damiandantas.daylighthabits.modules.alert.AlertType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Duration
import javax.inject.Inject

@HiltViewModel
class AlertScreenViewModel @Inject constructor(
    private val momentService: SunMomentService
) : ViewModel() {
    private val moments =
        momentService.moments.shareIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            replay = AlertType.values().size // Important so we have enough time to receive initial values
        )

    val sunrise: StateFlow<SunMoment?> =
        moments.filter { it.type == AlertType.SUNRISE }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    val sunset: StateFlow<SunMoment?> =
        moments.filter { it.type == AlertType.SUNSET }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    fun setSunriseEnabled(isEnabled: Boolean) {
        viewModelScope.launch {
            momentService.setEnabled(AlertType.SUNRISE, isEnabled)
        }
    }

    fun setSunsetEnabled(isEnabled: Boolean) {
        viewModelScope.launch {
            momentService.setEnabled(AlertType.SUNSET, isEnabled)
        }
    }

    fun setSunriseNoticePeriod(noticePeriod: Duration) {
        viewModelScope.launch {
            momentService.setNoticePeriod(AlertType.SUNRISE, noticePeriod)
        }
    }

    fun setSunsetNoticePeriod(noticePeriod: Duration) {
        viewModelScope.launch {
            momentService.setNoticePeriod(AlertType.SUNSET, noticePeriod)
        }
    }
}