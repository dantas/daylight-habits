package com.damiandantas.daylighthabits.ui.composables

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun AppDialog(
    onDismissRequest: () -> Unit,
    content: @Composable (paddingValues: PaddingValues) -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Surface(shape = MaterialTheme.shapes.extraLarge) {
            content(paddingValues = PaddingValues(24.dp))
        }
    }
}