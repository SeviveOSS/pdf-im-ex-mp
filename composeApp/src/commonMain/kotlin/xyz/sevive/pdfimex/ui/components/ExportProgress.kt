package xyz.sevive.pdfimex.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlin.time.Duration

internal fun Duration.formatETA(): String =
    toComponents { hours, minutes, seconds, _ ->
        if (hours > 0) {
            "${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"
        } else {
            "${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"
        }
    }

@Composable
fun ExportProgress(
    progress: Pair<Int, Int>?,
    eta: Duration?,
    modifier: Modifier = Modifier,
) {
    val isDisabled = progress == null || progress.second == 0
    val ratio = if (isDisabled) null else progress.first / progress.second.toFloat()

    val percentageLabel = if (ratio == null) "--%" else "${(ratio * 100).toInt()}%"
    val progressLabel = progress?.let { "${progress.first} / ${progress.second}" } ?: "- / -"
    val etaLabel = eta?.formatETA() ?: "--:--"

    Column(modifier) {
        Text("$percentageLabel ($progressLabel, ETA $etaLabel)")

        LinearProgressIndicator(
            progress = { ratio ?: 0f },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
