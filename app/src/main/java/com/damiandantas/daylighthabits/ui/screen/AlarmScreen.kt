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
import androidx.compose.material3.Card
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
import com.damiandantas.daylighthabits.domain.SunTime
import com.damiandantas.daylighthabits.presentation.AlarmScreenViewModel
import com.damiandantas.daylighthabits.ui.theme.AppTheme
import java.time.LocalTime
import java.time.ZonedDateTime

@Composable
fun AlarmScreen() {
    val viewModel: AlarmScreenViewModel = viewModel()

    AlarmScreenContent(
        suntime = viewModel.sunTime.value,
        sunriseAlarm = viewModel.sunriseAlarm.value,
        sunsetAlarm = viewModel.sunsetAlarm.value,
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
            suntime = SunTime(ZonedDateTime.now(), ZonedDateTime.now()),
            sunriseAlarm = AlarmScreenViewModel.Alarm(true, LocalTime.now()),
            sunsetAlarm = AlarmScreenViewModel.Alarm(true, LocalTime.now()),
            onSetSunriseAlarm = {},
            onSetSunriseAlarmDuration = {_, _ -> },
            onSetSunsetAlarm = {},
            onSetSunsetAlarmDuration = {_, _ -> },
        )
    }
}

@Composable
private fun AlarmScreenContent(
    suntime: SunTime,
    sunriseAlarm: AlarmScreenViewModel.Alarm,
    sunsetAlarm: AlarmScreenViewModel.Alarm,
    onSetSunriseAlarm: (Boolean) -> Unit,
    onSetSunriseAlarmDuration: (hour: Int, minute: Int) -> Unit,
    onSetSunsetAlarm: (Boolean) -> Unit,
    onSetSunsetAlarmDuration: (hour: Int, minute: Int) -> Unit,
) {
    val sunriseCardRes = remember {
        AlarmScreenRes(
            sunriseTime = R.string.sunrise_time,
            sleepTimeAlarm = R.string.sleep_time_alarm,
            sleepTime = R.string.sleep_time,
            setSleepTimeDuration = R.string.set_sleep_time_duration
        )
    }

    val sunsetCardRes = remember {
        AlarmScreenRes(
            sunriseTime = R.string.sunset_card_time,
            sleepTimeAlarm = R.string.sunset_card_enable_alarm,
            sleepTime = R.string.sunset_card_alarm_time,
            setSleepTimeDuration = R.string.set_sleep_time_duration
        )
    }

    Column(verticalArrangement = Arrangement.Top) {
        AlarmScreenCard(
            time = suntime.sunrise.toLocalTime(),
            cardResources = sunriseCardRes,
            alarm = sunriseAlarm,
            onSetAlarm = onSetSunriseAlarm,
            onSetAlarmDuration = onSetSunriseAlarmDuration
        )

        AlarmScreenCard(
            time = suntime.sunset.toLocalTime(),
            cardResources = sunsetCardRes,
            alarm = sunsetAlarm,
            onSetAlarm = onSetSunsetAlarm,
            onSetAlarmDuration = onSetSunsetAlarmDuration
        )
    }
}

@Composable
private fun AlarmScreenCard(
    time: LocalTime,
    cardResources: AlarmScreenRes,
    alarm: AlarmScreenViewModel.Alarm,
    onSetAlarm: (enabled: Boolean) -> Unit,
    onSetAlarmDuration: (hour: Int, minute: Int) -> Unit
) {
    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.End) {
            var showSleepDurationDialog by rememberSaveable { mutableStateOf(false) }

            LabeledTime(stringResource(cardResources.sunriseTime), time)

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
                    checked = alarm.isEnabled,
                    onCheckedChange = onSetAlarm
                )
            }

            if (alarm.isEnabled) {
                LabeledTime(stringResource(cardResources.sleepTime), alarm.duration)

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
                    alarm.duration.hour, alarm.duration.minute, true
                )
                dialog.setOnCancelListener { showSleepDurationDialog = false }
                dialog.show()
            }
        }
    }
}

private data class AlarmScreenRes(
    @StringRes val sunriseTime: Int,
    @StringRes val sleepTimeAlarm: Int,
    @StringRes val sleepTime: Int,
    @StringRes val setSleepTimeDuration: Int,
)

@Composable
private fun LabeledTime(title: String, time: LocalTime) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Start)
        )
        Text(
            text = String.format("%02d:%02d", time.hour, time.minute),
            fontSize = 48.sp,
            modifier = Modifier.align(Alignment.End)
        )
    }
}
