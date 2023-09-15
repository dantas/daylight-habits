@file:OptIn(ExperimentalMaterial3Api::class)

package com.damiandantas.daylighthabits.ui.screen

import android.app.TimePickerDialog
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
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
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
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
        sunriseMoment = viewModel.sunrise.value,
        sunsetMoment = viewModel.sunset.value,
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
            sunriseMoment = AlarmScreenViewModel.SunMoment(
                ZonedDateTime.now(),
                true,
                AlarmScreenViewModel.SunMomentAlarm(
                    ZonedDateTime.now().minusMinutes(8),
                    Duration.ofMinutes(8)
                )
            ),
            sunsetMoment = AlarmScreenViewModel.SunMoment(
                ZonedDateTime.now(),
                true,
                AlarmScreenViewModel.SunMomentAlarm(
                    ZonedDateTime.now().minusMinutes(15),
                    Duration.ofMinutes(15)
                )
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
    sunriseMoment: AlarmScreenViewModel.SunMoment,
    sunsetMoment: AlarmScreenViewModel.SunMoment,
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
            sunMoment = sunriseMoment,
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
            sunMoment = sunsetMoment,
            onSetAlarm = onSetSunsetAlarm,
            onSetAlarmDuration = onSetSunsetAlarmDuration
        )
    }
}

@Composable
private fun AlarmScreenCard(
    cardResources: AlarmScreenRes,
    sunMoment: AlarmScreenViewModel.SunMoment,
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
                checked = sunMoment.isAlarmEnabled,
                onCheckedChange = onSetAlarm,
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                val icon =
                    if (sunMoment.isAlarmEnabled) R.drawable.alarm_on else R.drawable.alarm_off
                Icon(painterResource(id = icon), null)
            }

            Column(
                modifier = Modifier.align(Alignment.TopStart)
            ) {
                LabeledTime(
                    title = stringResource(cardResources.sunTime),
                    hour = sunMoment.time.hour,
                    minute = sunMoment.time.minute
                )

                if (sunMoment.isAlarmEnabled) {
                    sunMoment.alarm?.let { alarm ->
                        LabeledTime(
                            title = stringResource(cardResources.alarm),
                            hour = alarm.time.hour,
                            minute = alarm.time.minute,
                            modifier = Modifier.padding(top = cardPadding)
                        )

                        LabeledDuration(
                            title = stringResource(cardResources.duration),
                            hour = alarm.duration.hours,
                            minute = alarm.duration.minutes,
                            modifier = Modifier.padding(top = cardPadding)
                        )
                    }

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
                        sunMoment.alarm?.duration?.hours ?: 0,
                        sunMoment.alarm?.duration?.minutes ?: 0,
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
    @Composable get() = MaterialTheme.typography.bodyLarge

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

@Composable
private fun LabeledDuration(title: String, hour: Int, minute: Int, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(text = title, style = titleStyle)
        Row {
            LabeledDurationTimeComponent(hour)
            LabeledDurationTimeComponentDescription(pluralRes = R.plurals.hour, count = hour)
            LabeledDurationTimeComponent(minute)
            LabeledDurationTimeComponentDescription(pluralRes = R.plurals.minute, count = minute)
        }
    }
}

@Composable
private fun LabeledDurationTimeComponent(timeComponent: Int) {
    Text(
        text = String.format("%d", timeComponent),
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun RowScope.LabeledDurationTimeComponentDescription(
    @PluralsRes pluralRes: Int,
    count: Int
) {
    Text(
        text = pluralStringResource(id = pluralRes, count = count),
        style = MaterialTheme.typography.bodySmall,
        modifier = Modifier
            .align(Alignment.Bottom)
            .padding(horizontal = 4.dp)
    )
}