package com.damiandantas.daylighthabits.ui.screen.settings

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.damiandantas.daylighthabits.modules.alert.AlertSettings
import com.damiandantas.daylighthabits.modules.alert.settings.AlertSettingsService
import com.damiandantas.daylighthabits.ui.utils.StateEventError
import com.damiandantas.daylighthabits.ui.utils.ViewModelError
import com.damiandantas.daylighthabits.ui.utils.eventError
import com.damiandantas.daylighthabits.ui.utils.flowSharingPolicy
import com.damiandantas.daylighthabits.ui.utils.mutableStateEventError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsService: AlertSettingsService
) : ViewModel() {
    sealed class State {
        object Loading : State()

        @Immutable
        data class Loaded(val settings: AlertSettings) : State()
    }

    val settings = settingsService.settings.mapNotNull { result ->
        val setting = result.getOrElse {
            _errors.value = ViewModelError.LOAD.eventError()
            return@mapNotNull null
        }

        State.Loaded(setting)
    }.stateIn(viewModelScope, flowSharingPolicy, State.Loading)

    private val _errors = mutableStateEventError()
    val errors: StateEventError = _errors

    fun setVibrate(isEnabled: Boolean) =
        launchFallibleOperation { settingsService.setVibrate(isEnabled) }

    fun setSound(isEnabled: Boolean) =
        launchFallibleOperation { settingsService.setSound(isEnabled) }

    private fun launchFallibleOperation(operation: suspend () -> Boolean) {
        viewModelScope.launch {
            if (!operation()) {
                _errors.value = ViewModelError.UPDATE.eventError()
            }
        }
    }
}