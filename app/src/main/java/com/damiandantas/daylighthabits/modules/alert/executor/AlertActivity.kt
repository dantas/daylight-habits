package com.damiandantas.daylighthabits.modules.alert.executor

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.PendingIntentCompat
import androidx.lifecycle.lifecycleScope
import com.damiandantas.daylighthabits.R
import com.damiandantas.daylighthabits.modules.alert.AlertType
import com.damiandantas.daylighthabits.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AlertActivity : ComponentActivity() {
    @Inject
    lateinit var alertExecutor: AlertExecutor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val alertType = intent.alertType

        Log.i("wasd", "Got type $alertType")
        
        if (alertType == null) {
            // TODO: Track error?
            Log.i("wasd", "Finished")
            finish()
            return
        }

        lifecycleScope.launch {
            alertExecutor.execute()
        }

        setContent {
            AlertActivityScreen(alertType) {
                alertExecutor.stop()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        alertExecutor.stop()
        finish()
    }
}

fun scheduleAlertIntent(context: Context, type: AlertType): PendingIntent =
    getPendingIntent(context, type, false)!!

fun unscheduleAlertIntent(context: Context, type: AlertType): PendingIntent? =
    getPendingIntent(context, type, true)

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

/*
        Since scheduled alarms are lost when the app is reinstalled, we can use AlertType.name
    without worrying about refactoring that changes the enum names
 */
private fun getPendingIntent(context: Context, type: AlertType, noCreate: Boolean): PendingIntent? {
    val intent = Intent(context, AlertActivity::class.java).apply {
        action = type.name
    }

    return PendingIntentCompat.getActivity(
        context, 0, intent,
        if (noCreate) PendingIntent.FLAG_NO_CREATE else 0, false
    )
}

private val Intent.alertType: AlertType?
    get() = runCatching { AlertType.valueOf(action ?: "") }.getOrNull()
