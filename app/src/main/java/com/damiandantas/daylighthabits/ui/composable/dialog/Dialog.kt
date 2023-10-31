package com.damiandantas.daylighthabits.ui.composable.dialog

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.damiandantas.daylighthabits.ui.theme.AppThemePreview

private val contentPadding = PaddingValues(24.dp)

@Composable
fun AppDialog(
    onDismissRequest: () -> Unit,
    content: @Composable (paddingValues: PaddingValues) -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            shape = AlertDialogDefaults.shape,
            tonalElevation = AlertDialogDefaults.TonalElevation,
            color = AlertDialogDefaults.containerColor,
        ) {
            content(paddingValues = contentPadding)
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun AppDialogPreview() {
    AppThemePreview {
        AppDialog(onDismissRequest = {}) { paddingValues ->
            Text("Hello Dialog", modifier = Modifier.padding(paddingValues))
        }
    }
}
