package com.damiandantas.daylighthabits.ui.screen.alert

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.damiandantas.daylighthabits.modules.SunMoment
import com.damiandantas.daylighthabits.modules.SunMomentService
import com.damiandantas.daylighthabits.modules.alert.AlertType
import com.damiandantas.daylighthabits.ui.utils.StateEventError
import com.damiandantas.daylighthabits.ui.utils.ViewModelError
import com.damiandantas.daylighthabits.ui.utils.eventError
import com.damiandantas.daylighthabits.ui.utils.flowSharingPolicy
import com.damiandantas.daylighthabits.ui.utils.mutableStateEventError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Duration
import javax.inject.Inject

@HiltViewModel
class AlertScreenViewModel @Inject constructor(
    private val momentService: SunMomentService
) : ViewModel() {
    sealed class State {
        object Loading : State()

        @Immutable
        data class Loaded(val moment: SunMoment) : State()
    }

    private val _errors = mutableStateEventError()
    val errors: StateEventError = _errors

    private val loadedFlow =
        momentService.moments.mapNotNull { result ->
            result.onSuccess {
                return@mapNotNull State.Loaded(it)
            }

            _errors.value = ViewModelError.LOAD.eventError()
            null
        }.shareIn(
            viewModelScope,
            flowSharingPolicy,
            AlertType.values().size // Ensure all initial Loaded events are properly stored
        )

    val sunrise = filterAsStateFlow(AlertType.SUNRISE)
    val sunset = filterAsStateFlow(AlertType.SUNSET)

    fun setSunriseEnabled(isEnabled: Boolean) =
        launchFallibleOperation { momentService.setEnabled(AlertType.SUNRISE, isEnabled) }

    fun setSunsetEnabled(isEnabled: Boolean) =
        launchFallibleOperation { momentService.setEnabled(AlertType.SUNSET, isEnabled) }

    fun setSunriseNoticePeriod(noticePeriod: Duration) =
        launchFallibleOperation { momentService.setNoticePeriod(AlertType.SUNRISE, noticePeriod) }

    fun setSunsetNoticePeriod(noticePeriod: Duration) =
        launchFallibleOperation { momentService.setNoticePeriod(AlertType.SUNSET, noticePeriod) }

    private fun launchFallibleOperation(operation: suspend () -> Boolean) {
        viewModelScope.launch {
            if (!operation()) _errors.value = ViewModelError.UPDATE.eventError()
        }
    }

    private fun filterAsStateFlow(type: AlertType): StateFlow<State> =
        loadedFlow
            .filter {
                it.moment.type == type
            }.stateIn(viewModelScope, flowSharingPolicy, State.Loading)
}
