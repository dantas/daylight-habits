package com.damiandantas.daylighthabits.ui.composable

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import com.damiandantas.daylighthabits.ui.theme.LocalSpacingOutsideCard

val AppScreenPadding
    @Composable get() = PaddingValues(
        top = LocalSpacingOutsideCard.current,
        start = LocalSpacingOutsideCard.current,
        end = LocalSpacingOutsideCard.current
    )

val ForecastScreenPadding
    @Composable get() = PaddingValues(bottom = LocalSpacingOutsideCard.current)