package com.damiandantas.daylighthabits.ui.composable

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
@Preview(showBackground = true, backgroundColor = 0xFFFFFF)
fun BoxScope.Loading() {
    CircularProgressIndicator(
        modifier = Modifier.align(Alignment.Center)
    )
}