package com.damiandantas.daylighthabits.ui.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.damiandantas.daylighthabits.ui.theme.AppTheme
import com.damiandantas.daylighthabits.ui.theme.LocalSpacingInsideCard

@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    ElevatedCard {
        Box(modifier = modifier.padding(LocalSpacingInsideCard.current), content = content)
    }
}

@Preview
@Composable
fun AppCardPreview() {
    AppTheme {
        AppCard {
            Text("Hello world")
        }
    }
}