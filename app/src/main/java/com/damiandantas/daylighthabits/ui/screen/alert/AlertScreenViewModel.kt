package com.damiandantas.daylighthabits.ui.screen.alert

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.damiandantas.daylighthabits.modules.SunMoment
import com.damiandantas.daylighthabits.modules.SunMomentService
import com.damiandantas.daylighthabits.modules.alert.AlertType
import com.damiandantas.daylighthabits.ui.utils.ViewModelEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Duration
import javax.inject.Inject

// TODO: Define custom error type ?

@HiltViewModel
class AlertScreenViewModel @Inject constructor(
    private val momentService: SunMomentService
) : ViewModel() {
    private val _errors = MutableStateFlow(ViewModelEvent(Unit, true))
    val errors: StateFlow<ViewModelEvent<Unit>> = _errors

    // Ensure momentService.moments is collected only once
    private val moments =
        momentService.moments.shareIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            replay = AlertType.values().size // Important so we have enough capacity to receive initial values
        )

    val sunrise: StateFlow<SunMoment?> = stateFilterMoments(AlertType.SUNRISE)

    val sunset: StateFlow<SunMoment?> = stateFilterMoments(AlertType.SUNSET)

    fun setSunriseEnabled(isEnabled: Boolean) =
        launchFallibleOperation { momentService.setEnabled(AlertType.SUNRISE, isEnabled) }

    fun setSunsetEnabled(isEnabled: Boolean) =
        launchFallibleOperation { momentService.setEnabled(AlertType.SUNSET, isEnabled) }

    fun setSunriseNoticePeriod(noticePeriod: Duration) =
        launchFallibleOperation { momentService.setNoticePeriod(AlertType.SUNRISE, noticePeriod) }

    fun setSunsetNoticePeriod(noticePeriod: Duration) =
        launchFallibleOperation { momentService.setNoticePeriod(AlertType.SUNSET, noticePeriod) }

    private fun stateFilterMoments(type: AlertType): StateFlow<SunMoment?> =
        moments.filter { it.type == type }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private fun launchFallibleOperation(operation: suspend () -> Boolean) {
        viewModelScope.launch {
            if (!operation()) _errors.value = ViewModelEvent(Unit) // TODO: Define custom error
        }
    }
}