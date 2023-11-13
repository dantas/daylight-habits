package com.damiandantas.daylighthabits.ui.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.damiandantas.daylighthabits.ui.theme.AppTheme

private val contentPadding = 10.dp

@Composable
fun AppCard(
    contentModifier: Modifier = Modifier,
    content: @Composable BoxScope.(padding: Dp) -> Unit
) {
    ElevatedCard {
        Box(modifier = contentModifier.padding(contentPadding)) {
            content(contentPadding)
        }
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