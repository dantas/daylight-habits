package com.damiandantas.daylighthabits.modules.alert.notifier

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import com.damiandantas.daylighthabits.R
import com.damiandantas.daylighthabits.modules.alert.AlertType
import com.damiandantas.daylighthabits.ui.theme.AppTheme
import com.damiandantas.daylighthabits.utils.alertType
import com.damiandantas.daylighthabits.utils.put
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AlertNotifierActivity : ComponentActivity() {
    @Inject
    lateinit var notifier: AlertNotifier

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val alertType = intent.alertType

        if (alertType == null) {
            // TODO: Track this?
            finish()
            return
        }

        startAlert()

        setContent {
            AlertActivityScreen(alertType, ::stopAlert)
        }
    }

    override fun onStop() {
        super.onStop()
        stopAlert()
    }

    private fun startAlert() {
        lifecycleScope.launch {
            notifier.notify()
        }
    }

    private fun stopAlert() {
        notifier.stop()
        finish()
    }

    companion object {
        fun intent(context: Context, type: AlertType): Intent =
            Intent(context, AlertNotifierActivity::class.java).put(type)
    }
}

@Composable
private fun AlertActivityScreen(type: AlertType, onStop: () -> Unit) {
    val messageId = when (type) {
        AlertType.SUNRISE -> R.string.alert_activity_message_sunrise
        AlertType.SUNSET -> R.string.alert_activity_message_sunset
    }

    AppTheme {
        Surface {
            Box(modifier = Modifier.fillMaxSize()) {
                Text(
                    stringResource(messageId),
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.align(BiasAlignment(0f, -0.2f))
                )
                Button(
                    modifier = Modifier.align(BiasAlignment(0f, 0.2f)),
                    onClick = onStop
                ) {
                    Text(stringResource(R.string.alert_activity_button))
                }
            }
        }
    }
}

@Preview
@Composable
private fun AlertActivityPreview() {
    AlertActivityScreen(AlertType.SUNSET) {}
}
