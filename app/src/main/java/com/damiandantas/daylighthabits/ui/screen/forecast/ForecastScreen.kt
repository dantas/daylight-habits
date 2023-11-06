package com.damiandantas.daylighthabits.ui.screen.forecast

import LabeledTime
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.damiandantas.daylighthabits.R
import com.damiandantas.daylighthabits.modules.Forecast
import com.damiandantas.daylighthabits.ui.composable.AppCard
import com.damiandantas.daylighthabits.ui.theme.AppTheme
import kotlinx.coroutines.flow.toCollection
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun ForecastScreen(viewModel: ForecastScreenViewModel) {
    val nextDaysForecast = remember { mutableStateListOf<Forecast>() }

    LaunchedEffect(viewModel) {
        viewModel.nextDaysForecast.toCollection(nextDaysForecast)
    }

    ForecastScreenContent(nextDaysForecast)
}

@Composable
private fun ForecastScreenContent(nextDaysForecast: List<Forecast>) {
    LazyColumn {
        itemsIndexed(
            items = nextDaysForecast,
            key = { _, f -> f.hashCode() }
        ) { index, forecast ->
            AppCard(
                useBottomMargin = index == nextDaysForecast.lastIndex
            ) {
                CardContent(forecast)
            }
        }
    }
}

@Composable
@Preview(showSystemUi = true)
private fun ForecastScreenPreview() {
    AppTheme {
        val list = List(10) { index ->
            val time = ZonedDateTime.now().plusDays(index.toLong())
            Forecast(time, time)
        }

        ForecastScreenContent(list)
    }
}

@Composable
private fun CardContent(forecast: Forecast) {
    // TODO: Extract 10.dp to somewhere
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).format(forecast.sunrise),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        LabeledTime(
            title = stringResource(R.string.sunrise_card_title),
            time = forecast.sunrise
        )

        LabeledTime(
            title = stringResource(R.string.sunset_card_title),
            time = forecast.sunset
        )
    }
}
