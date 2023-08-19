package com.damiandantas.daylighthabits.ui.app

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.damiandantas.daylighthabits.R
import com.damiandantas.daylighthabits.ui.theme.AppTheme

@Composable
fun AppNavigationBar(selected: Int, onClick: (index: Int) -> Unit) {
    val selected = remember {
        mutableStateOf(
            arrayOf(false, false, false).apply {
                this[selected] = true
            }
        )
    }

    NavigationBar {
        NavigationBarItem(selected = selected.value[0], onClick = {
            selected.value = arrayOf(true, false, false)
            onClick(0)
        }, icon = {
            Icon(
                painterResource(R.drawable.alarm), "Alarm",
            )
        }, label = { Text("Alarm") })

        NavigationBarItem(selected = selected.value[1], onClick = {
            selected.value = arrayOf(false, true, false)
            onClick(1)
        }, icon = {
            Icon(
                painterResource(R.drawable.routine), "Forecast",
            )
        }, label = { Text("Forecast") })

        NavigationBarItem(selected = selected.value[2], onClick = {
            selected.value = arrayOf(false, false, true)
            onClick(2)
        }, icon = {
            Icon(
                painterResource(R.drawable.instant_mix), "Alarm Settings",
            )
        }, label = { Text("Alarm Settings") })
    }
}

@Composable
@Preview
private fun NavigationBarPreview() {
    AppTheme {
        AppNavigationBar(0) {}
    }
}