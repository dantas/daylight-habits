@file:OptIn(ExperimentalMaterial3Api::class)

package com.damiandantas.daylighthabits.ui.screen.alert

import android.app.TimePickerDialog
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.damiandantas.daylighthabits.R
import com.damiandantas.daylighthabits.alert.domain.Alert
import com.damiandantas.daylighthabits.alert.domain.AlertConfig
import com.damiandantas.daylighthabits.alert.domain.SunMoment
import com.damiandantas.daylighthabits.alert.domain.SunMomentType
import com.damiandantas.daylighthabits.ui.theme.AppTheme
import java.time.Duration
import java.time.ZonedDateTime

@Composable
fun AlertScreen() {
    val viewModel: AlertScreenViewModel = hiltViewModel()

    // TODO: Check how this composable is recomposed on state change

    val sunrise: State<SunMoment?> = viewModel.sunrise.collectAsStateWithLifecycle()
    val sunset: State<SunMoment?> = viewModel.sunset.collectAsStateWithLifecycle()

    ScreenContent(
        sunrise = sunrise.value,
        sunset = sunset.value,
        onSunriseSetEnable = viewModel::setSunriseEnabled,
        onSunriseSetNoticePeriod = viewModel::setSunriseNoticePeriod,
        onSunsetSetEnable = viewModel::setSunsetEnabled,
        onSunsetSetNoticePeriod = viewModel::setSunsetNoticePeriod,
    )
}

@Composable
@Preview(showSystemUi = true)
fun AlertScreenPreview() {
    AppTheme {
        ScreenContent(
            sunrise = SunMoment(
                type = SunMomentType.SUNRISE,
                time = ZonedDateTime.now(),
                alert = Alert(
                    time = ZonedDateTime.now().minusMinutes(8),
                    config = AlertConfig(
                        type = SunMomentType.SUNRISE,
                        noticePeriod = Duration.ofMinutes(8),
                        isEnabled = true
                    )
                )
            ),
            sunset = SunMoment(
                type = SunMomentType.SUNSET,
                time = ZonedDateTime.now(),
                alert = Alert(
                    time = ZonedDateTime.now().minusMinutes(15),
                    config = AlertConfig(
                        type = SunMomentType.SUNSET,
                        noticePeriod = Duration.ofMinutes(15),
                        isEnabled = true
                    )
                )
            ),
            onSunriseSetEnable = {},
            onSunriseSetNoticePeriod = { _ -> },
            onSunsetSetEnable = {},
            onSunsetSetNoticePeriod = { _ -> },
        )
    }
}

@Composable
private fun ScreenContent(
    sunrise: SunMoment?,
    sunset: SunMoment?,
    onSunriseSetEnable: (Boolean) -> Unit,
    onSunriseSetNoticePeriod: (Duration) -> Unit,
    onSunsetSetEnable: (Boolean) -> Unit,
    onSunsetSetNoticePeriod: (Duration) -> Unit,
) {
    Column {
        Card(
            cardResources = remember {
                AlertScreenCardRes(
                    title = R.string.sunrise_card_title,
                    sunTime = R.string.sunrise_card_sun_time,
                    alertTime = R.string.sunrise_card_alert_time,
                    noticePeriod = R.string.sunrise_card_notice_time,
                    setNoticePeriod = R.string.sunrise_card_set_notice_time
                )
            },
            sunMoment = sunrise,
            onSetEnable = onSunriseSetEnable,
            onSetNoticePeriod = onSunriseSetNoticePeriod
        )

        Card(
            cardResources = remember {
                AlertScreenCardRes(
                    title = R.string.sunset_card_title,
                    sunTime = R.string.sunset_card_sun_time,
                    alertTime = R.string.sunset_card_alert_time,
                    noticePeriod = R.string.sunset_card_notice_time,
                    setNoticePeriod = R.string.sunset_card_set_notice_time
                )
            },
            sunMoment = sunset,
            onSetEnable = onSunsetSetEnable,
            onSetNoticePeriod = onSunsetSetNoticePeriod
        )
    }
}

@Composable
private fun Card(
    cardResources: AlertScreenCardRes,
    sunMoment: SunMoment?,
    onSetEnable: (enabled: Boolean) -> Unit,
    onSetNoticePeriod: (Duration) -> Unit
) {
    ElevatedCard(
        modifier = Modifier.padding(top = cardMargin, start = cardMargin, end = cardMargin)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = cardPadding)
        ) {
            if (sunMoment == null) {
                CardWithoutState()
            } else {
                CardWithState(
                    cardResources = cardResources,
                    sunMoment = sunMoment,
                    onSetEnable = onSetEnable,
                    onSetNoticePeriod = onSetNoticePeriod,
                )
            }
        }
    }
}

@Composable
private fun BoxScope.CardWithoutState() {
    CircularProgressIndicator(
        modifier = Modifier.align(Alignment.Center)
    )
}

@Composable
private fun BoxScope.CardWithState(
    cardResources: AlertScreenCardRes,
    sunMoment: SunMoment,
    onSetEnable: (enabled: Boolean) -> Unit,
    onSetNoticePeriod: (Duration) -> Unit
) {
    var showNoticePeriodDialog by rememberSaveable { mutableStateOf(false) }

    val hasAlert = sunMoment.alert != null

    FilledIconToggleButton(
        checked = hasAlert,
        onCheckedChange = onSetEnable,
        modifier = Modifier.align(Alignment.CenterEnd)
    ) {
        val icon = if (hasAlert) R.drawable.alert_on else R.drawable.alert_off
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

        if (sunMoment.alert != null) {
            val alert = sunMoment.alert

            LabeledTime(
                title = stringResource(cardResources.alertTime),
                hour = alert.time.hour,
                minute = alert.time.minute,
                modifier = Modifier.padding(top = cardPadding)
            )

            LabeledDuration(
                title = stringResource(cardResources.noticePeriod),
                hour = alert.config.noticePeriod.hours,
                minute = alert.config.noticePeriod.minutes,
                modifier = Modifier.padding(top = cardPadding)
            )

            Button(
                onClick = {
                    showNoticePeriodDialog = true
                },
                modifier = Modifier.padding(top = cardPadding)
            ) {
                Text(stringResource(cardResources.setNoticePeriod))
            }
        }

        if (showNoticePeriodDialog) {
            val noticePeriod = sunMoment.alert?.config?.noticePeriod ?: Duration.ZERO

            val dialog = TimePickerDialog(
                LocalContext.current,
                { _, hour: Int, minute: Int ->
                    val duration =
                        Duration.ofHours(hour.toLong()).plusMinutes(minute.toLong())
                    onSetNoticePeriod(duration)
                    showNoticePeriodDialog = false
                },
                noticePeriod.hours,
                noticePeriod.minutes,
                true
            )
            dialog.setOnCancelListener { showNoticePeriodDialog = false }
            dialog.show()
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

private data class AlertScreenCardRes(
    @StringRes val title: Int,
    @StringRes val sunTime: Int,
    @StringRes val alertTime: Int,
    @StringRes val noticePeriod: Int,
    @StringRes val setNoticePeriod: Int,
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