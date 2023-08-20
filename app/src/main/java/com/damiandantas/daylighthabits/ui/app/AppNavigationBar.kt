package com.damiandantas.daylighthabits.ui.app

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.damiandantas.daylighthabits.R
import com.damiandantas.daylighthabits.ui.theme.AppTheme

@Composable
fun AppNavigationBar(selectedItem: Int, onClick: (index: Int) -> Unit) {
    val selected = rememberSaveable { mutableStateOf(selectedItem) }

    NavigationBar {
        AppNavigationBarItem(
            index = 0, icon = R.drawable.alarm, label = "Alarm",
            selected = selected, onClick = onClick
        )

        AppNavigationBarItem(
            index = 1, icon = R.drawable.routine, label = "Forecast",
            selected = selected, onClick = onClick
        )

        AppNavigationBarItem(
            index = 2, icon = R.drawable.instant_mix, label = "Alarm Settings",
            selected = selected, onClick = onClick
        )
    }
}

@Composable
fun RowScope.AppNavigationBarItem(
    index: Int,
    @DrawableRes icon: Int,
    label: String,
    selected: MutableState<Int>,
    onClick: (index: Int) -> Unit,
) {
    NavigationBarItem(selected = selected.value == index, onClick = {
        selected.value = index
        onClick(index)
    }, icon = {
        Icon(
            painterResource(icon), label,
        )
    }, label = { Text(label) })
}

@Composable
@Preview
private fun NavigationBarPreview() {
    AppTheme {
        AppNavigationBar(0) {}
    }
}