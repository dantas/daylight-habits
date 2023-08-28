@file:OptIn(ExperimentalMaterial3Api::class)

package com.damiandantas.daylighthabits.ui.screen

import android.app.TimePickerDialog
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Switch
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.damiandantas.daylighthabits.R
import com.damiandantas.daylighthabits.presentation.AlarmScreenViewModel
import com.damiandantas.daylighthabits.ui.theme.AppTheme
import java.time.Duration
import java.time.ZonedDateTime

@Composable
fun AlarmScreen() {
    val viewModel: AlarmScreenViewModel = viewModel()

    AlarmScreenContent(
        sunrise = viewModel.sunriseEvent.value,
        sunset = viewModel.sunsetEvent.value,
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
            sunrise = AlarmScreenViewModel.Event(ZonedDateTime.now(), true, Duration.ZERO),
            sunset = AlarmScreenViewModel.Event(ZonedDateTime.now(), true, Duration.ZERO),
            onSetSunriseAlarm = {},
            onSetSunriseAlarmDuration = { _, _ -> },
            onSetSunsetAlarm = {},
            onSetSunsetAlarmDuration = { _, _ -> },
        )
    }
}

@Composable
private fun AlarmScreenContent(
    sunrise: AlarmScreenViewModel.Event,
    sunset: AlarmScreenViewModel.Event,
    onSetSunriseAlarm: (Boolean) -> Unit,
    onSetSunriseAlarmDuration: (hour: Int, minute: Int) -> Unit,
    onSetSunsetAlarm: (Boolean) -> Unit,
    onSetSunsetAlarmDuration: (hour: Int, minute: Int) -> Unit,
) {
    Column(verticalArrangement = Arrangement.Top) {
        AlarmScreenCard(
            cardResources = remember {
                AlarmScreenRes(
                    sunriseTime = R.string.sunrise_card_time,
                    sleepTimeAlarm = R.string.sunrise_card_alarm,
                    sleepTime = R.string.sunrise_card_alarm_time,
                    setSleepTimeDuration = R.string.sunrise_card_set_duration
                )
            },
            event = sunrise,
            onSetAlarm = onSetSunriseAlarm,
            onSetAlarmDuration = onSetSunriseAlarmDuration
        )

        AlarmScreenCard(
            cardResources = remember {
                AlarmScreenRes(
                    sunriseTime = R.string.sunset_card_time,
                    sleepTimeAlarm = R.string.sunset_card_alarm,
                    sleepTime = R.string.sunset_card_alarm_time,
                    setSleepTimeDuration = R.string.sunset_card_set_duration
                )
            },
            event = sunset,
            onSetAlarm = onSetSunsetAlarm,
            onSetAlarmDuration = onSetSunsetAlarmDuration
        )
    }
}

@Composable
private fun AlarmScreenCard(
    cardResources: AlarmScreenRes,
    event: AlarmScreenViewModel.Event,
    onSetAlarm: (enabled: Boolean) -> Unit,
    onSetAlarmDuration: (hour: Int, minute: Int) -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .padding(top = 16.dp, start = 16.dp, end = 16.dp)
            .fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.End) {
            var showSleepDurationDialog by rememberSaveable { mutableStateOf(false) }

            LabeledTime(
                title = stringResource(cardResources.sunriseTime),
                hour = event.time.hour,
                minute = event.time.minute
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(cardResources.sleepTimeAlarm),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Switch(
                    checked = event.notificationEnabled,
                    onCheckedChange = onSetAlarm
                )
            }

            if (event.notificationEnabled) {
                LabeledTime(
                    title = stringResource(cardResources.sleepTime),
                    hour = event.notificationDuration.hours,
                    minute = event.notificationDuration.minutes
                )

                Button(
                    onClick = {
                        showSleepDurationDialog = true
                    }
                ) {
                    Text(stringResource(cardResources.setSleepTimeDuration))
                }
            }

            if (showSleepDurationDialog) {
                val dialog = TimePickerDialog(
                    LocalContext.current,
                    { _, hour: Int, minute: Int ->
                        onSetAlarmDuration(hour, minute)
                        showSleepDurationDialog = false
                    },
                    event.notificationDuration.hours,
                    event.notificationDuration.minutes,
                    true
                )
                dialog.setOnCancelListener { showSleepDurationDialog = false }
                dialog.show()
            }
        }
    }
}

private val Duration.hours
    get() = toHours().toInt()

private val Duration.minutes
    get() = (toMinutes() % 60).toInt()

private data class AlarmScreenRes(
    @StringRes val sunriseTime: Int,
    @StringRes val sleepTimeAlarm: Int,
    @StringRes val sleepTime: Int,
    @StringRes val setSleepTimeDuration: Int,
)

@Composable
private fun LabeledTime(title: String, hour: Int, minute: Int) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Start)
        )
        Text(
            text = String.format("%02d:%02d", hour, minute),
            fontSize = 48.sp,
            modifier = Modifier.align(Alignment.End)
        )
    }
}
