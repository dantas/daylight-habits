package com.damiandantas.daylighthabits.ui.screen.alert

import DurationPicker
import LabeledDuration
import LabeledTime
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.damiandantas.daylighthabits.R
import com.damiandantas.daylighthabits.modules.SunMoment
import com.damiandantas.daylighthabits.modules.alert.AlertSchedule
import com.damiandantas.daylighthabits.modules.alert.AlertType
import com.damiandantas.daylighthabits.modules.forecast.Forecast
import com.damiandantas.daylighthabits.ui.composable.AppCard
import com.damiandantas.daylighthabits.ui.composable.Loading
import com.damiandantas.daylighthabits.ui.theme.AppTheme
import com.damiandantas.daylighthabits.ui.theme.LocalSpacingInsideCard
import com.damiandantas.daylighthabits.ui.theme.LocalSpacingOutsideCard
import com.damiandantas.daylighthabits.ui.utils.ViewModelError
import java.time.Duration
import java.time.ZonedDateTime

@Composable
fun AlertScreen(viewModel: AlertScreenViewModel, showErrorMessage: (String) -> Unit) {
    // TODO: Check how this composable is recomposed on state change

    val sunrise = viewModel.sunrise.collectAsStateWithLifecycle()
    val sunset = viewModel.sunset.collectAsStateWithLifecycle()

    ScreenContent(
        sunriseState = sunrise.value,
        sunsetState = sunset.value,
        onSunriseSetEnable = viewModel::setSunriseEnabled,
        onSunriseSetNoticePeriod = viewModel::setSunriseNoticePeriod,
        onSunsetSetEnable = viewModel::setSunsetEnabled,
        onSunsetSetNoticePeriod = viewModel::setSunsetNoticePeriod,
    )

    HandleErrors(viewModel, showErrorMessage)
}

@Composable
@Preview(showSystemUi = true)
private fun AlertScreenPreview() {
    val forecast = Forecast(
        sunrise = ZonedDateTime.now(),
        sunset = ZonedDateTime.now().plusHours(8)
    )

    var sunriseEnabled by remember { mutableStateOf(true) }
    var sunsetEnabled by remember { mutableStateOf(true) }

    val sunriseSchedule = AlertSchedule(
        type = AlertType.SUNRISE,
        noticePeriod = Duration.ofMinutes(8),
        isEnabled = sunriseEnabled
    )

    val sunsetSchedule = AlertSchedule(
        type = AlertType.SUNSET,
        noticePeriod = Duration.ofMinutes(15),
        isEnabled = sunsetEnabled
    )

    AppTheme {
        ScreenContent(
            sunriseState = AlertScreenViewModel.State.Loaded(
                SunMoment(
                    type = AlertType.SUNRISE,
                    sunTime = forecast.sunrise,
                    alertTime = forecast.sunrise.minus(sunriseSchedule.noticePeriod),
                    alertSchedule = sunriseSchedule
                )
            ),
            sunsetState = AlertScreenViewModel.State.Loaded(
                SunMoment(
                    type = AlertType.SUNSET,
                    sunTime = forecast.sunset,
                    alertTime = forecast.sunset.minus(sunsetSchedule.noticePeriod),
                    alertSchedule = sunsetSchedule
                )
            ),
            onSunriseSetEnable = { sunriseEnabled = it },
            onSunriseSetNoticePeriod = { _ -> },
            onSunsetSetEnable = { sunsetEnabled = it },
            onSunsetSetNoticePeriod = { _ -> },
        )
    }
}

@Composable
private fun HandleErrors(viewModel: AlertScreenViewModel, showErrorMessage: (String) -> Unit) {
    val errors = viewModel.errors.collectAsStateWithLifecycle()
    val loadErrorMsg = stringResource(R.string.alert_screen_load_error)
    val updateErrorMsg = stringResource(R.string.alert_screen_update_error)

    errors.value.consume { error ->
        val errorMsg = when (error) {
            ViewModelError.LOAD -> loadErrorMsg
            ViewModelError.UPDATE -> updateErrorMsg
        }

        showErrorMessage(errorMsg)
    }
}

@Composable
private fun ScreenContent(
    sunriseState: AlertScreenViewModel.State,
    sunsetState: AlertScreenViewModel.State,
    onSunriseSetEnable: (Boolean) -> Unit,
    onSunriseSetNoticePeriod: (Duration) -> Unit,
    onSunsetSetEnable: (Boolean) -> Unit,
    onSunsetSetNoticePeriod: (Duration) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(LocalSpacingOutsideCard.current)
    ) {
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
            state = sunriseState,
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
            state = sunsetState,
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
    state: AlertScreenViewModel.State,
    onSetEnable: (enabled: Boolean) -> Unit,
    onSetNoticePeriod: (Duration) -> Unit
) {
    AppCard(modifier = Modifier.fillMaxWidth()) {
        when (state) {
            is AlertScreenViewModel.State.Loaded ->
                Loaded(
                    cardResources = cardResources,
                    moment = state.moment,
                    onSetEnable = onSetEnable,
                    onSetNoticePeriod = onSetNoticePeriod
                )

            AlertScreenViewModel.State.Loading ->
                Loading()
        }
    }
}

@Composable
private fun BoxScope.Loaded(
    cardResources: CardRes,
    moment: SunMoment,
    onSetEnable: (enabled: Boolean) -> Unit,
    onSetNoticePeriod: (Duration) -> Unit
) {
    var showNoticePeriodDialog by rememberSaveable { mutableStateOf(false) }

    FilledIconToggleButton(
        checked = moment.alertSchedule.isEnabled,
        onCheckedChange = onSetEnable,
        modifier = Modifier
            .align(Alignment.CenterEnd)
            .wrapContentSize()
    ) {
        val icon = if (moment.alertSchedule.isEnabled) R.drawable.alert_on else R.drawable.alert_off
        Icon(painterResource(id = icon), null)
    }

    Column(
        modifier = Modifier
            .align(Alignment.TopStart)
            .cardAnimation()
    ) {
        LabeledTime(
            title = stringResource(cardResources.sunTime),
            time = moment.sunTime
        )

        ExpandableCardContent(
            visible = moment.alertSchedule.isEnabled
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(LocalSpacingInsideCard.current)) {
                LabeledTime(
                    title = stringResource(cardResources.alertTime),
                    time = moment.alertTime
                )

                LabeledDuration(
                    title = stringResource(cardResources.noticePeriod),
                    duration = moment.alertSchedule.noticePeriod
                )

                Button(
                    onClick = {
                        showNoticePeriodDialog = true
                    }
                ) {
                    Text(stringResource(cardResources.setNoticePeriod))
                }
            }
        }
    }

    if (showNoticePeriodDialog) {
        DurationPicker(
            initialValue = moment.alertSchedule.noticePeriod,
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

private fun Modifier.cardAnimation(): Modifier =
    animateContentSize(
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMedium
        )
    )

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun ExpandableCardContent(
    visible: Boolean,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    val springSpec =
        spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessVeryLow,
            visibilityThreshold = IntSize.VisibilityThreshold
        )

    val fadeSpec = spring<Float>(stiffness = Spring.StiffnessVeryLow)

    val transition = updateTransition(visible, "card expansion")

    val animatedPaddingTop = transition.animateDp(label = "dp animation") {
        if (it) LocalSpacingInsideCard.current else 0.dp
    }

    transition.AnimatedVisibility(
        visible = { it },
        modifier = Modifier.padding(top = animatedPaddingTop.value),
        enter = expandVertically(springSpec) + fadeIn(fadeSpec),
        exit = shrinkVertically(springSpec) + fadeOut(fadeSpec),
        content = content
    )
}