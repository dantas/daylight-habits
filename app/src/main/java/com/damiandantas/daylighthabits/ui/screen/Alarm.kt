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
import com.damiandantas.daylighthabits.ui.theme.AppTheme
import java.time.LocalTime

@Composable
fun Alarm() {
    Column {
        SunriseCard()
    }
}

@Composable
fun SunriseCard() {
    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.End) {
            var enabled by rememberSaveable { mutableStateOf(false) }
            var showSleepDurationDialog by rememberSaveable { mutableStateOf(false) }
            var sleepDuration by rememberSaveable { mutableStateOf(LocalTime.now()) }

            LabeledTime("Sunrise time", LocalTime.of(10, 4))

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
                    checked = enabled,
                    onCheckedChange = {
                        enabled = it
                    })
            }

            if (enabled) {
                LabeledTime("Sleep time", sleepDuration)

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
                        sleepDuration = LocalTime.of(hour, minute)
                        showSleepDurationDialog = false
                    },
                    10, 10, true
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
            text = time.timeString,
            fontSize = 48.sp,
            modifier = Modifier.align(Alignment.End)
        )
    }
}

val LocalTime.timeString: String
    get() = String.format("%02d:%02d", hour, minute)

@Preview
@Composable
fun AlarmPreview() {
    AppTheme {
        Alarm()
    }
}
