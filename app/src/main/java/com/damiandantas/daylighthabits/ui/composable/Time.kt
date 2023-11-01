import android.app.TimePickerDialog
import androidx.annotation.PluralsRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.damiandantas.daylighthabits.R
import com.damiandantas.daylighthabits.ui.theme.AppTheme
import com.damiandantas.daylighthabits.utils.hours
import com.damiandantas.daylighthabits.utils.minutes
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

private inline val titleStyle: TextStyle
    @Composable get() = MaterialTheme.typography.bodyLarge

@Composable
fun LabeledTime(title: String, time: ZonedDateTime, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = title,
            style = titleStyle,
        )
        Text(
            text = String.format("%02d:%02d", time.hour, time.minute),
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
fun LabeledDuration(title: String, duration: Duration, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(text = title, style = titleStyle)
        Row {
            TimeComponent(duration.hours)
            Description(pluralRes = R.plurals.hour, count = duration.hours)
            TimeComponent(duration.minutes)
            Description(pluralRes = R.plurals.minute, count = duration.minutes)
        }
    }
}

@Composable
fun DurationPicker(initialValue: Duration, onPick: (Duration) -> Unit, onDismiss: () -> Unit) {
    val context = LocalContext.current

    DisposableEffect(null) {
        val dialog = TimePickerDialog(
            context,
            { _, hour: Int, minute: Int ->
                val duration =
                    Duration.ofHours(hour.toLong()).plusMinutes(minute.toLong())
                onPick(duration)
            },
            initialValue.hours,
            initialValue.minutes,
            true
        )

        dialog.setOnCancelListener { onDismiss() }
        dialog.show()

        onDispose {
            dialog.dismiss()
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFF)
@Composable
private fun LabeledTimePreview() {
    AppTheme {
        val time = ZonedDateTime.of(
            LocalDate.of(2023, 10, 31),
            LocalTime.of(22, 33),
            ZoneId.of("America/Sao_Paulo")
        )

        LabeledTime(title = "Sunrise", time = time)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFF)
@Composable
private fun LabeledDurationPreview() {
    AppTheme {
        LabeledDuration(
            title = "Notice Time",
            duration = Duration.ofHours(10).plusMinutes(30)
        )
    }
}

@Preview(showSystemUi = true)
@Composable
private fun DurationPickerPreview() {
    AppTheme {
        DurationPicker(initialValue = Duration.ofHours(10).plusMinutes(20), onPick = { }, onDismiss = {})
    }
}

@Composable
private fun TimeComponent(timeComponent: Int) {
    Text(
        text = String.format("%d", timeComponent),
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun RowScope.Description(
    @PluralsRes pluralRes: Int,
    count: Int
) {
    Text(
        text = pluralStringResource(id = pluralRes, count = count),
        style = MaterialTheme.typography.bodySmall,
        modifier = Modifier
            .align(Alignment.Bottom)
            .padding(horizontal = 4.dp)
    )
}