package com.damiandantas.daylighthabits.ui.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.damiandantas.daylighthabits.ui.theme.AppTheme

private val margin = 16.dp
private val padding = 10.dp

@Composable
fun AppCard(
    useBottomMargin: Boolean = false,
    contentModifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val bottomMargin = if (useBottomMargin) margin else 0.dp

    ElevatedCard(
        modifier = Modifier.padding(
            start = margin,
            end = margin,
            top = margin,
            bottom = bottomMargin
        )
    ) {
        Box(modifier = contentModifier.padding(all = padding)) {
            content()
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun AppCardPreview() {
    AppTheme {
        AppCard(
            useBottomMargin = true,
            contentModifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Text("Hello world")
        }
    }
}