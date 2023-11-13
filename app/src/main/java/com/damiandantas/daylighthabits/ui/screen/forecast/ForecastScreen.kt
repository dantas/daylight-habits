package com.damiandantas.daylighthabits.ui.screen.forecast

import LabeledTime
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.damiandantas.daylighthabits.R
import com.damiandantas.daylighthabits.modules.forecast.Forecast
import com.damiandantas.daylighthabits.ui.composable.AppCard
import com.damiandantas.daylighthabits.ui.composable.AppLazyColumn
import com.damiandantas.daylighthabits.ui.theme.AppTheme
import kotlinx.coroutines.flow.toCollection
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun ForecastScreen(viewModel: ForecastScreenViewModel) {
    val nextDaysForecast = remember { mutableStateListOf<Forecast>() }

    LaunchedEffect(null) {
        viewModel.nextDaysForecast.toCollection(nextDaysForecast)
    }

    ForecastScreenContent(nextDaysForecast)
}

@Composable
private fun ForecastScreenContent(nextDaysForecast: SnapshotStateList<Forecast>) {
    AppLazyColumn {
        items(
            items = nextDaysForecast,
            key = { f -> f.hashCode() }
        ) { forecast ->
            AppCard { padding ->
                CardContent(forecast, padding)
            }
        }
    }
}

@Composable
@Preview(showSystemUi = true)
private fun ForecastScreenPreview() {
    AppTheme {
        val list = SnapshotStateList<Forecast>()

        for (index in 0 until 10) {
            val time = ZonedDateTime.now().plusDays(index.toLong())
            list.add(Forecast(time, time))
        }

        ForecastScreenContent(list)
    }
}

@Composable
private fun CardContent(forecast: Forecast, spacedby: Dp) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacedby)
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
