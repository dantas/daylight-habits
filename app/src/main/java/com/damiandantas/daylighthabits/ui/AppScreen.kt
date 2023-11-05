package com.damiandantas.daylighthabits.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.damiandantas.daylighthabits.R
import com.damiandantas.daylighthabits.ui.screen.alert.AlertScreen
import com.damiandantas.daylighthabits.ui.screen.forecast.ForecastScreen
import com.damiandantas.daylighthabits.ui.theme.AppTheme

private enum class Screen(
    val route: String,
    @DrawableRes val icon: Int,
    @StringRes val label: Int,
    val routeContent: @Composable (ViewModelStoreOwner) -> Unit
) {
    Alert(
        route = "alert",
        icon = R.drawable.alert,
        label = R.string.nav_bar_alert,
        routeContent = { AlertScreen(hiltViewModel(it)) }
    ),

    Forecast(
        route = "forecast",
        icon = R.drawable.routine,
        label = R.string.nav_bar_forecast,
        routeContent = { ForecastScreen(hiltViewModel(it)) }
    ),

    Settings(
        route = "settings",
        icon = R.drawable.instant_mix,
        label = R.string.nav_bar_settings,
        routeContent = { Text(text = "Settings") }
    )
}

private val startDestination = Screen.Alert

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScreen() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            val currentBackStack = navController.currentBackStackEntryAsState()

            AppNavigationBar(
                currentSelectedRoute = remember {
                    derivedStateOf {
                        currentBackStack.value?.destination?.route ?: startDestination.route
                    }
                },
                onSelected = { navController.navigate(it.route) }
            )
        },
    ) { paddingValues ->
        val viewModelStore =
            LocalViewModelStoreOwner.current!! // TODO: In which situations returns null?

        NavHost(
            navController = navController,
            startDestination = startDestination.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            for (screen in Screen.values()) {
                composable(screen.route) {
                    screen.routeContent(viewModelStore)
                }
            }
        }
    }
}

@Composable
private fun AppNavigationBar(currentSelectedRoute: State<String>, onSelected: (Screen) -> Unit) {
    NavigationBar {
        for (screen in Screen.values()) {
            val label = stringResource(screen.label)

            NavigationBarItem(
                selected = currentSelectedRoute.value == screen.route,
                onClick = { onSelected(screen) },
                icon = { Icon(painterResource(screen.icon), label) },
                label = { Text(label) }
            )
        }
    }
}

@Preview
@Composable
private fun AppNavigationBarPreview() {
    AppTheme {
        val currentSelectedRoute = remember { mutableStateOf(startDestination.route) }
        AppNavigationBar(
            currentSelectedRoute = currentSelectedRoute,
            onSelected = { currentSelectedRoute.value = it.route }
        )
    }
}
