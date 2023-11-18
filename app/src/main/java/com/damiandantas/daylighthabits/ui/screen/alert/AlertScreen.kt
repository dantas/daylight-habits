package com.damiandantas.daylighthabits.ui.screen.alert

import DurationPicker
import LabeledDuration
import LabeledTime
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.damiandantas.daylighthabits.R
import com.damiandantas.daylighthabits.modules.SunMoment
import com.damiandantas.daylighthabits.modules.alert.Alert
import com.damiandantas.daylighthabits.modules.alert.AlertSchedule
import com.damiandantas.daylighthabits.modules.alert.AlertType
import com.damiandantas.daylighthabits.ui.composable.AppCard
import com.damiandantas.daylighthabits.ui.composable.AppColumn
import com.damiandantas.daylighthabits.ui.composable.Loading
import com.damiandantas.daylighthabits.ui.composable.cardSpring
import com.damiandantas.daylighthabits.ui.theme.AppTheme
import java.time.Duration
import java.time.ZonedDateTime

@Composable
fun AlertScreen(viewModel: AlertScreenViewModel) {
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
                type = AlertType.SUNRISE,
                time = ZonedDateTime.now(),
                alert = Alert(
                    time = ZonedDateTime.now().minusMinutes(8),
                    schedule = AlertSchedule(
                        type = AlertType.SUNRISE,
                        noticePeriod = Duration.ofMinutes(8),
                        isEnabled = true
                    )
                )
            ),
            sunset = SunMoment(
                type = AlertType.SUNSET,
                time = ZonedDateTime.now(),
                alert = Alert(
                    time = ZonedDateTime.now().minusMinutes(15),
                    schedule = AlertSchedule(
                        type = AlertType.SUNSET,
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
    AppColumn {
        Card(
            cardResources = remember {
                CardRes(
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
                CardRes(
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

private data class CardRes(
    @StringRes val title: Int,
    @StringRes val sunTime: Int,
    @StringRes val alertTime: Int,
    @StringRes val noticePeriod: Int,
    @StringRes val setNoticePeriod: Int,
)

@Composable
private fun Card(
    cardResources: CardRes,
    sunMoment: SunMoment?,
    onSetEnable: (enabled: Boolean) -> Unit,
    onSetNoticePeriod: (Duration) -> Unit
) {
    AppCard(modifier = Modifier.fillMaxWidth()) { padding ->
        if (sunMoment == null) {
            Loading()
        } else {
            SunMoment(
                cardResources = cardResources,
                sunMoment = sunMoment,
                onSetEnable = onSetEnable,
                onSetNoticePeriod = onSetNoticePeriod,
                padding
            )
        }
    }
}

@Composable
private fun BoxScope.SunMoment(
    cardResources: CardRes,
    sunMoment: SunMoment,
    onSetEnable: (enabled: Boolean) -> Unit,
    onSetNoticePeriod: (Duration) -> Unit,
    spacedBy: Dp
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
        verticalArrangement = Arrangement.spacedBy(spacedBy),
        modifier = Modifier
            .cardSpring()
            .align(Alignment.TopStart),
    ) {
        LabeledTime(
            title = stringResource(cardResources.sunTime),
            time = sunMoment.time
        )

        if (sunMoment.alert != null) {
            LabeledTime(
                title = stringResource(cardResources.alertTime),
                time = sunMoment.alert.time
            )

            LabeledDuration(
                title = stringResource(cardResources.noticePeriod),
                duration = sunMoment.alert.schedule.noticePeriod
            )

            Button(
                onClick = {
                    showNoticePeriodDialog = true
                }
            ) {
                Text(stringResource(cardResources.setNoticePeriod))
            }
        }

        if (showNoticePeriodDialog) {
            DurationPicker(
                initialValue = sunMoment.alert?.schedule?.noticePeriod ?: Duration.ZERO,
                onPick = {
                    onSetNoticePeriod(it)
                    showNoticePeriodDialog = false
                },
                onDismiss = {
                    showNoticePeriodDialog = false
                }
            )
        }
    }
}
