package com.damiandantas.daylighthabits.ui.dialog

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.damiandantas.daylighthabits.R
import com.damiandantas.daylighthabits.ui.theme.AppThemePreview

private const val button = "Proceed"

private val explanation = """
    We require your coarse location to calculate the sunrise and sunset
    This location does not leave your device
    Clicking on $button will take you to another screen to authorize it
""".trimIndent()

@Composable
fun LocationPermissionDialog(onDismissDialog: () -> Unit, onClickButton: () -> Unit) {
    AppDialog(onDismissRequest = onDismissDialog) { paddingValues ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(paddingValues)
        ) {
            Image(
                painter = painterResource(R.drawable.location_on),
                null,
                modifier = Modifier.size(160.dp)
            )
            Text(
                "Coarse location required",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )
            Text(explanation, textAlign = TextAlign.Justify)
            Button(onClick = onClickButton) {
                Text(button)
            }
        }
    }
}

@Composable
@Preview(showSystemUi = true)
private fun LocationPermissionDialogPreview() {
    AppThemePreview {
        LocationPermissionDialog({}, {})
    }
}
