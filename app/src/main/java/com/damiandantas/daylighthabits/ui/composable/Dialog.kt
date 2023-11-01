package com.damiandantas.daylighthabits.ui.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.damiandantas.daylighthabits.R
import com.damiandantas.daylighthabits.ui.theme.AppTheme

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
            content(paddingValues = PaddingValues(24.dp))
        }
    }
}

@Composable
fun LocationPermissionDialog(onDismissDialog: () -> Unit, onClickButton: () -> Unit) {
    AppDialog(onDismissRequest = onDismissDialog) { paddingValues ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(paddingValues)
        ) {
            Image(
                painter = painterResource(R.drawable.location_on),
                null,
                modifier = Modifier.size(160.dp)
            )
            Text(
                stringResource(R.string.location_permission_dialog_title),
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )
            Text(
                stringResource(R.string.location_permission_dialog_explanation),
                textAlign = TextAlign.Justify
            )
            Button(onClick = onClickButton) {
                Text(stringResource(R.string.location_permission_dialog_button))
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun AppDialogPreview() {
    AppTheme {
        AppDialog(onDismissRequest = {}) { paddingValues ->
            Text("Hello Dialog", modifier = Modifier.padding(paddingValues))
        }
    }
}

@Composable
@Preview(showSystemUi = true)
private fun LocationPermissionDialogPreview() {
    AppTheme {
        LocationPermissionDialog(onDismissDialog = {}, onClickButton = {})
    }
}