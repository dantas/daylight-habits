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
import com.damiandantas.daylighthabits.ui.theme.AppTheme
import java.time.Duration
import java.time.ZonedDateTime

@Composable
fun AlertScreen() {
    val sunriseViewModel: AlertScreenSunriseViewModel = hiltViewModel()
    val sunsetViewModel: AlertScreenSunsetViewModel = hiltViewModel()

    // TODO: Check how this composable is recomposed on state change

    val sunriseCardState = sunriseViewModel.cardState.collectAsStateWithLifecycle().value
    val sunsetCardState = sunsetViewModel.cardState.collectAsStateWithLifecycle().value

    ScreenContent(
        sunriseCardState = sunriseCardState,
        sunsetCardState = sunsetCardState,
        onSunriseSetEnable = sunriseViewModel::setEnabled,
        onSunriseSetNoticeTime = sunriseViewModel::setNoticeTime,
        onSunsetSetEnable = sunsetViewModel::setEnabled,
        onSunsetSetNoticeTime = sunsetViewModel::setNoticeTime,
    )
}

@Composable
@Preview(showSystemUi = true)
fun AlertScreenPreview() {
    AppTheme {
        ScreenContent(
            sunriseCardState = AlertScreenCardState(
                ZonedDateTime.now(),
                true,
                Alert(
                    ZonedDateTime.now().minusMinutes(8),
                    Duration.ofMinutes(8)
                )
            ),
            sunsetCardState = AlertScreenCardState(
                ZonedDateTime.now(),
                true,
                Alert(
                    ZonedDateTime.now().minusMinutes(15),
                    Duration.ofMinutes(15)
                )
            ),
            onSunriseSetEnable = {},
            onSunriseSetNoticeTime = { _ -> },
            onSunsetSetEnable = {},
            onSunsetSetNoticeTime = { _ -> },
        )
    }
}

@Composable
private fun ScreenContent(
    sunriseCardState: AlertScreenCardState?,
    sunsetCardState: AlertScreenCardState?,
    onSunriseSetEnable: (Boolean) -> Unit,
    onSunriseSetNoticeTime: (Duration) -> Unit,
    onSunsetSetEnable: (Boolean) -> Unit,
    onSunsetSetNoticeTime: (Duration) -> Unit,
) {
    Column {
        Card(
            cardResources = remember {
                AlertScreenRes(
                    title = R.string.sunrise_card_title,
                    sunTime = R.string.sunrise_card_sun_time,
                    alertTime = R.string.sunrise_card_alert_time,
                    noticeTime = R.string.sunrise_card_notice_time,
                    setNoticeTime = R.string.sunrise_card_set_notice_time
                )
            },
            cardState = sunriseCardState,
            onSetEnable = onSunriseSetEnable,
            onSetNoticeTime = onSunriseSetNoticeTime
        )

        Card(
            cardResources = remember {
                AlertScreenRes(
                    title = R.string.sunset_card_title,
                    sunTime = R.string.sunset_card_sun_time,
                    alertTime = R.string.sunset_card_alert_time,
                    noticeTime = R.string.sunset_card_notice_time,
                    setNoticeTime = R.string.sunset_card_set_notice_time
                )
            },
            cardState = sunsetCardState,
            onSetEnable = onSunsetSetEnable,
            onSetNoticeTime = onSunsetSetNoticeTime
        )
    }
}

@Composable
private fun Card(
    cardResources: AlertScreenRes,
    cardState: AlertScreenCardState?,
    onSetEnable: (enabled: Boolean) -> Unit,
    onSetNoticeTime: (Duration) -> Unit
) {
    ElevatedCard(
        modifier = Modifier.padding(top = cardMargin, start = cardMargin, end = cardMargin)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = cardPadding)
        ) {
            if (cardState == null) {
                CardWithoutState()
            } else {
                CardWithState(
                    cardResources = cardResources,
                    cardState = cardState,
                    onSetEnable = onSetEnable,
                    onSetNoticeTime = onSetNoticeTime,
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
    cardResources: AlertScreenRes,
    cardState: AlertScreenCardState,
    onSetEnable: (enabled: Boolean) -> Unit,
    onSetNoticeTime: (Duration) -> Unit
) {
    var showSleepDurationDialog by rememberSaveable { mutableStateOf(false) }

    FilledIconToggleButton(
        checked = cardState.isEnabled,
        onCheckedChange = onSetEnable,
        modifier = Modifier.align(Alignment.CenterEnd)
    ) {
        val icon =
            if (cardState.isEnabled) R.drawable.alert_on else R.drawable.alert_off
        Icon(painterResource(id = icon), null)
    }

    Column(
        modifier = Modifier.align(Alignment.TopStart)
    ) {
        LabeledTime(
            title = stringResource(cardResources.sunTime),
            hour = cardState.sunTime.hour,
            minute = cardState.sunTime.minute
        )

        if (cardState.isEnabled) {
            cardState.alert?.let { alert ->
                LabeledTime(
                    title = stringResource(cardResources.alertTime),
                    hour = alert.time.hour,
                    minute = alert.time.minute,
                    modifier = Modifier.padding(top = cardPadding)
                )

                LabeledDuration(
                    title = stringResource(cardResources.noticeTime),
                    hour = alert.noticeTime.hours,
                    minute = alert.noticeTime.minutes,
                    modifier = Modifier.padding(top = cardPadding)
                )
            }

            Button(
                onClick = {
                    showSleepDurationDialog = true
                },
                modifier = Modifier.padding(top = cardPadding)
            ) {
                Text(stringResource(cardResources.setNoticeTime))
            }
        }

        if (showSleepDurationDialog) {
            val dialog = TimePickerDialog(
                LocalContext.current,
                { _, hour: Int, minute: Int ->
                    val duration =
                        Duration.ofHours(hour.toLong()).plusMinutes(minute.toLong())
                    onSetNoticeTime(duration)
                    showSleepDurationDialog = false
                },
                cardState.alert?.noticeTime?.hours ?: 0,
                cardState.alert?.noticeTime?.minutes ?: 0,
                true
            )
            dialog.setOnCancelListener { showSleepDurationDialog = false }
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

private data class AlertScreenRes(
    @StringRes val title: Int,
    @StringRes val sunTime: Int,
    @StringRes val alertTime: Int,
    @StringRes val noticeTime: Int,
    @StringRes val setNoticeTime: Int,
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