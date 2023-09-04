@file:OptIn(ExperimentalMaterial3Api::class)

package com.damiandantas.daylighthabits.ui.screen

import android.app.TimePickerDialog
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.damiandantas.daylighthabits.R
import com.damiandantas.daylighthabits.presentation.AlarmScreenViewModel
import com.damiandantas.daylighthabits.ui.theme.AppTheme
import java.time.Duration
import java.time.ZonedDateTime

@Composable
fun AlarmScreen() {
    val viewModel: AlarmScreenViewModel = hiltViewModel()

    AlarmScreenContent(
        sunriseAlarm = viewModel.sunriseAlarmState.value,
        sunsetAlarm = viewModel.sunsetAlarmState.value,
        onSetSunriseAlarm = viewModel::onSetSunriseAlarm,
        onSetSunriseAlarmDuration = viewModel::onSetSunriseAlarmDuration,
        onSetSunsetAlarm = viewModel::onSetSunsetAlarm,
        onSetSunsetAlarmDuration = viewModel::onSetSunsetAlarmDuration,
    )
}

@Composable
@Preview(showSystemUi = true)
fun AlarmScreenPreview() {
    AppTheme {
        AlarmScreenContent(
            sunriseAlarm = AlarmScreenViewModel.SunAlarm(
                ZonedDateTime.now(),
                ZonedDateTime.now().minusHours(8),
                true,
                Duration.ofHours(8)
            ),
            sunsetAlarm = AlarmScreenViewModel.SunAlarm(
                ZonedDateTime.now(),
                ZonedDateTime.now().minusMinutes(15),
                true,
                Duration.ofMinutes(15)
            ),
            onSetSunriseAlarm = {},
            onSetSunriseAlarmDuration = { _ -> },
            onSetSunsetAlarm = {},
            onSetSunsetAlarmDuration = { _ -> },
        )
    }
}

@Composable
private fun AlarmScreenContent(
    sunriseAlarm: AlarmScreenViewModel.SunAlarm,
    sunsetAlarm: AlarmScreenViewModel.SunAlarm,
    onSetSunriseAlarm: (Boolean) -> Unit,
    onSetSunriseAlarmDuration: (Duration) -> Unit,
    onSetSunsetAlarm: (Boolean) -> Unit,
    onSetSunsetAlarmDuration: (Duration) -> Unit,
) {
    Column {
        AlarmScreenCard(
            cardResources = remember {
                AlarmScreenRes(
                    title = R.string.sunrise_card_title,
                    sunTime = R.string.sunrise_card_sun_time,
                    alarm = R.string.sunrise_card_alarm,
                    duration = R.string.sunrise_card_duration,
                    setDuration = R.string.sunrise_card_set_duration
                )
            },
            sunAlarm = sunriseAlarm,
            onSetAlarm = onSetSunriseAlarm,
            onSetAlarmDuration = onSetSunriseAlarmDuration
        )

        AlarmScreenCard(
            cardResources = remember {
                AlarmScreenRes(
                    title = R.string.sunset_card_title,
                    sunTime = R.string.sunset_card_sun_time,
                    alarm = R.string.sunset_card_alarm,
                    duration = R.string.sunset_card_duration,
                    setDuration = R.string.sunset_card_set_duration
                )
            },
            sunAlarm = sunsetAlarm,
            onSetAlarm = onSetSunsetAlarm,
            onSetAlarmDuration = onSetSunsetAlarmDuration
        )
    }
}

@Composable
private fun AlarmScreenCard(
    cardResources: AlarmScreenRes,
    sunAlarm: AlarmScreenViewModel.SunAlarm,
    onSetAlarm: (enabled: Boolean) -> Unit,
    onSetAlarmDuration: (Duration) -> Unit
) {
    ElevatedCard(
        modifier = Modifier.padding(top = cardMargin, start = cardMargin, end = cardMargin)
    ) {
        var showSleepDurationDialog by rememberSaveable { mutableStateOf(false) }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = cardPadding)
        ) {
            FilledIconToggleButton(
                checked = sunAlarm.notificationEnabled,
                onCheckedChange = onSetAlarm,
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                val icon =
                    if (sunAlarm.notificationEnabled) R.drawable.alarm_on else R.drawable.alarm_off
                Icon(painterResource(id = icon), null)
            }

            Column(
                modifier = Modifier.align(Alignment.TopStart)
            ) {
                LabeledTime(
                    title = stringResource(cardResources.sunTime),
                    hour = sunAlarm.time.hour,
                    minute = sunAlarm.time.minute
                )

                if (sunAlarm.notificationEnabled) {
                    LabeledTime(
                        title = stringResource(cardResources.alarm),
                        hour = sunAlarm.notificationTime.hour,
                        minute = sunAlarm.notificationTime.minute,
                        modifier = Modifier.padding(top = cardPadding)
                    )

                    LabeledTime(
                        title = stringResource(cardResources.duration),
                        hour = sunAlarm.notificationDuration.hours,
                        minute = sunAlarm.notificationDuration.minutes,
                        modifier = Modifier.padding(top = cardPadding)
                    )

                    Button(
                        onClick = {
                            showSleepDurationDialog = true
                        },
                        modifier = Modifier.padding(top = cardPadding)
                    ) {
                        Text(stringResource(cardResources.setDuration))
                    }
                }

                if (showSleepDurationDialog) {
                    val dialog = TimePickerDialog(
                        LocalContext.current,
                        { _, hour: Int, minute: Int ->
                            val duration =
                                Duration.ofHours(hour.toLong()).plusMinutes(minute.toLong())
                            onSetAlarmDuration(duration)
                            showSleepDurationDialog = false
                        },
                        sunAlarm.notificationDuration.hours,
                        sunAlarm.notificationDuration.minutes,
                        true
                    )
                    dialog.setOnCancelListener { showSleepDurationDialog = false }
                    dialog.show()
                }
            }
        }
    }
}

private val cardMargin = 16.dp
private val cardPadding = 10.dp

private inline val titleStyle: TextStyle
    @Composable get() = MaterialTheme.typography.titleLarge

private val Duration.hours
    get() = toHours().toInt()

private val Duration.minutes
    get() = (toMinutes() % 60).toInt()

private data class AlarmScreenRes(
    @StringRes val title: Int,
    @StringRes val sunTime: Int,
    @StringRes val alarm: Int,
    @StringRes val duration: Int,
    @StringRes val setDuration: Int,
)

@Composable
private fun LabeledTime(title: String, hour: Int, minute: Int, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = title,
            style = titleStyle,
        )
        Text(
            text = String.format("%02d:%02d", hour, minute),
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}
