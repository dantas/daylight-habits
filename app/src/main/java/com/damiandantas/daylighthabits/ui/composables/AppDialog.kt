package com.damiandantas.daylighthabits.ui.composables

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun AppDialog(onDismissRequest: () -> Unit, content: @Composable (modifier: Modifier) -> Unit) {
    Dialog(onDismissRequest = onDismissRequest) {
        Surface(shape = MaterialTheme.shapes.extraLarge) {
            content(modifier = Modifier.padding(24.dp))
        }
    }
}