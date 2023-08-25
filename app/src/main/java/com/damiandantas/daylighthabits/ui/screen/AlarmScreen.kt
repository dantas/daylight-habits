@file:OptIn(ExperimentalMaterial3Api::class)

package com.damiandantas.daylighthabits.ui.screen

import android.app.TimePickerDialog
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.damiandantas.daylighthabits.presentation.AlarmScreenViewModel
import com.damiandantas.daylighthabits.ui.theme.AppTheme
import java.time.LocalTime

@Composable
fun AlarmScreen() {
    val viewModel: AlarmScreenViewModel = viewModel()

    Column {
        SunriseCard(
            sunrise = viewModel.sunrise,
            sleepTime = viewModel.sleepTime.value,
            onSetSleepTimeAlarm = viewModel::onSetSleepTimeAlarm,
            onSetSleepTimeDuration = viewModel::onSetSleepTimeDuration,
        )
    }
}

@Composable
fun SunriseCard(
    sunrise: LocalTime,
    sleepTime: AlarmScreenViewModel.SleepTime,
    onSetSleepTimeAlarm: (enabled: Boolean) -> Unit,
    onSetSleepTimeDuration: (hour: Int, minute: Int) -> Unit
) {
    Card(modifier = Modifier
        .padding(16.dp)
        .fillMaxWidth()) {
        Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.End) {
            var showSleepDurationDialog by rememberSaveable { mutableStateOf(false) }

            LabeledTime("Sunrise time", sunrise)

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Sleep time alarm",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Switch(
                    checked = sleepTime.isEnabled,
                    onCheckedChange = onSetSleepTimeAlarm
                )
            }

            if (sleepTime.isEnabled) {
                LabeledTime("Sleep time", sleepTime.duration)

                Button(
                    onClick = {
                        showSleepDurationDialog = true
                    }
                ) {
                    Text("Set sleep duration")
                }
            }

            if (showSleepDurationDialog) {
                val dialog = TimePickerDialog(
                    LocalContext.current,
                    { _, hour: Int, minute: Int ->
                        onSetSleepTimeDuration(hour, minute)
                        showSleepDurationDialog = false
                    },
                    sleepTime.duration.hour, sleepTime.duration.minute, true
                )
                dialog.setOnCancelListener { showSleepDurationDialog = false }
                dialog.show()
            }
        }
    }
}

@Composable
fun LabeledTime(title: String, time: LocalTime) {
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

@Preview
@Composable
fun SunriseCardPreview() {
    AppTheme {
        SunriseCard(
            sunrise = LocalTime.now(),
            sleepTime = AlarmScreenViewModel.SleepTime(true, LocalTime.of(14, 23)),
            onSetSleepTimeAlarm = {},
            onSetSleepTimeDuration = { _, _ -> },
        )
    }
}
