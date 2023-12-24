package com.damiandantas.daylighthabits.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.damiandantas.daylighthabits.R
import com.damiandantas.daylighthabits.ui.composable.ScreenPadding
import com.damiandantas.daylighthabits.ui.screen.alert.AlertScreen
import com.damiandantas.daylighthabits.ui.screen.forecast.ForecastScreen
import com.damiandantas.daylighthabits.ui.screen.settings.SettingsScreen
import com.damiandantas.daylighthabits.ui.theme.AppTheme
import kotlinx.coroutines.launch

// ----
// Screen definitions, everything else works based on the information defined here
private enum class Screen(
    val route: String,
    @DrawableRes val icon: Int,
    @StringRes val label: Int,
    val routeContent: @Composable (ViewModelStoreOwner, ShowErrorMessage) -> Unit
) {
    Alert(
        route = "alert",
        icon = R.drawable.alert,
        label = R.string.nav_bar_alert,
        routeContent = { storeOwner, showErrorMessage ->
            AlertScreen(hiltViewModel(storeOwner), showErrorMessage)
        }
    ),

    Forecast(
        route = "forecast",
        icon = R.drawable.routine,
        label = R.string.nav_bar_forecast,
        routeContent = { storeOwner, _ ->
            ForecastScreen(hiltViewModel(storeOwner))
        }
    ),

    Settings(
        route = "settings",
        icon = R.drawable.instant_mix,
        label = R.string.nav_bar_settings,
        routeContent = { _, _ ->
            Text(
                modifier = Modifier.fillMaxSize(),
                text = "Settings"
            )
        }
    )
}

private val startDestination = Screen.Alert
// ====

@Composable
fun AppScreen() {
    val navController = rememberNavController()
    lateinit var showErrorMessage: ShowErrorMessage

    Scaffold(
        snackbarHost = { showErrorMessage = showErrorMessage() },
        bottomBar = {
            val currentBackStack = navController.currentBackStackEntryAsState()

            AppNavigationBar(
                currentSelectedRoute = remember {
                    derivedStateOf {
                        currentBackStack.value?.destination?.route ?: startDestination.route
                    }
                },
                onSelected = { screen ->
                    navController.navigate(screen.route) {
                        launchSingleTop = true
                        restoreState = true
                        popUpTo(startDestination.route) {
                            saveState = true
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        val screenViewModelStore = LocalViewModelStoreOwner.current!!

        NavHost(
            navController = navController,
            startDestination = startDestination.route,
            modifier = Modifier
                .padding(paddingValues)
                .padding(ScreenPadding)
        ) {
            for (screen in Screen.values()) {
                composable(
                    route = screen.route,
                    enterTransition = { enterTransition() },
                    exitTransition = { exitTransition() },
                    content = { screen.routeContent(screenViewModelStore, showErrorMessage) }
                )
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

// ----
// Animation between screens
private fun AnimatedContentTransitionScope<NavBackStackEntry>.enterTransition(): EnterTransition =
    slideIntoContainer(
        towards = animationDirection,
        animationSpec = navigationAnimation
    )

private fun AnimatedContentTransitionScope<NavBackStackEntry>.exitTransition(): ExitTransition =
    slideOutOfContainer(
        towards = animationDirection,
        animationSpec = navigationAnimation
    )

private val navigationAnimation = tween<IntOffset>()
private val routeToPosition = Screen.values().associate { it.route to it.ordinal }

private val AnimatedContentTransitionScope<NavBackStackEntry>.animationDirection: AnimatedContentTransitionScope.SlideDirection
    get() {
        val initialPosition = routeToPosition[initialState.destination.route!!]!!
        val destinationPosition = routeToPosition[targetState.destination.route!!]!!

        return if (initialPosition < destinationPosition) {
            AnimatedContentTransitionScope.SlideDirection.Left
        } else {
            AnimatedContentTransitionScope.SlideDirection.Right
        }
    }
// ====

// ----
// Show error message in a snackbar
// Ok we are really stretching what is an acceptable composable design,
// but this is an attempt to hide CoroutineScope and SnackbarHostState inside this function
// and prevent them from polluting other scopes
private typealias ShowErrorMessage = (String) -> Unit

@Composable
private fun showErrorMessage(): ShowErrorMessage {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    SnackbarHost(snackbarHostState)

    return { message ->
        scope.launch {
            snackbarHostState.showSnackbar(message)
        }
    }
}
// ====
