package com.damiandantas.daylighthabits.ui.screen.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterEnd
import androidx.compose.ui.Alignment.Companion.CenterStart
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.damiandantas.daylighthabits.R
import com.damiandantas.daylighthabits.modules.alert.AlertSettings
import com.damiandantas.daylighthabits.ui.composable.Loading
import com.damiandantas.daylighthabits.ui.theme.AppTheme
import com.damiandantas.daylighthabits.ui.theme.LocalSpacingInsideCard
import com.damiandantas.daylighthabits.ui.utils.ViewModelError

@Composable
fun SettingsScreen(viewModel: SettingsViewModel, showErrorMessage: (String) -> Unit) {
    val settingsState = viewModel.settings.collectAsStateWithLifecycle()

    SettingsScreenContent(
        state = settingsState.value,
        onSetVibrate = viewModel::setVibrate,
        onSetSound = viewModel::setSound
    )

    HandleErrors(viewModel, showErrorMessage)
}

@Preview(showBackground = true)
@Composable
private fun SettingsScreenPreview() {
    var settings by remember { mutableStateOf(AlertSettings(vibrate = true, sound = false)) }

    AppTheme {
        SettingsScreenContent(
            state = SettingsViewModel.State.Loaded(settings),
            onSetVibrate = {
                settings = settings.copy(vibrate = it)
            },
            onSetSound = {
                settings = settings.copy(sound = it)
            }
        )
    }
}

@Composable
private fun HandleErrors(viewModel: SettingsViewModel, showErrorMessage: (String) -> Unit) {
    val error = viewModel.errors.collectAsStateWithLifecycle()
    val loadErrorMsg = stringResource(R.string.settings_screen_load_error)
    val updateErrorMsg = stringResource(R.string.settings_screen_update_error)

    error.value.consume {
        when (it) {
            ViewModelError.LOAD -> showErrorMessage(loadErrorMsg)
            ViewModelError.UPDATE -> showErrorMessage(updateErrorMsg)
        }
    }
}

@Composable
private fun SettingsScreenContent(
    state: SettingsViewModel.State,
    onSetVibrate: (Boolean) -> Unit,
    onSetSound: (Boolean) -> Unit
) {
    when (state) {
        is SettingsViewModel.State.Loaded ->
            LoadedState(
                settings = state.settings,
                onSetVibrate = onSetVibrate,
                onSetSound = onSetSound
            )

        SettingsViewModel.State.Loading ->
            LoadingState()
    }
}

@Composable
private fun LoadedState(
    settings: AlertSettings,
    onSetVibrate: (Boolean) -> Unit,
    onSetSound: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(LocalSpacingInsideCard.current)
    ) {
        Setting(
            text = stringResource(R.string.settings_screen_vibrate),
            enabled = settings.vibrate,
            onEnabled = onSetVibrate
        )
        Setting(
            text = stringResource(R.string.settings_screen_sound),
            enabled = settings.sound,
            onEnabled = onSetSound
        )
    }
}

@Composable
private fun LoadingState() {
    Box(modifier = Modifier.fillMaxSize()) {
        Loading()
    }
}

@Composable
private fun Setting(text: String, enabled: Boolean, onEnabled: (enabled: Boolean) -> Unit) {
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(modifier = Modifier.align(CenterStart), text = text)
        Switch(
            modifier = Modifier.align(CenterEnd),
            checked = enabled,
            onCheckedChange = onEnabled
        )
    }
}
