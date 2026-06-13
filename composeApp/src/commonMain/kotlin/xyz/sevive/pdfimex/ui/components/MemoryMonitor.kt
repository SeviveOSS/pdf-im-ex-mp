package xyz.sevive.pdfimex.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

data class MemoryUsage(
    val label: String,
    val usedBytes: Long = 0,
    val maxBytes: Long = 0,
) {
    val ratio = if (maxBytes == 0L) null else usedBytes / maxBytes.toFloat()
}

expect fun reportMemoryUsage(): List<MemoryUsage>

private fun formatFileSize(bytes: Long): String =
    when {
        bytes < 1024 -> "$bytes B"
        bytes < 1024 * 1024 -> "${bytes / 1024} KB"
        bytes < 1024 * 1024 * 1024 -> "${bytes / (1024 * 1024)} MB"
        else -> "${"%.1f".format(bytes.toDouble() / (1024 * 1024 * 1024))} GB"
    }

@Composable
private fun MemoryMonitorItem(
    report: MemoryUsage,
    modifier: Modifier = Modifier,
) {
    val percentageLabel = if (report.ratio == null) "-" else "${(report.ratio * 100).toInt()}%"
    val usedLabel = formatFileSize(report.usedBytes)
    val maxLabel = formatFileSize(report.maxBytes)

    Column(
        modifier,
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Row {
            Text(
                "${report.label} · $percentageLabel ($usedLabel / $maxLabel)",
                style = MaterialTheme.typography.bodySmall,
            )
        }

        if (report.ratio == null) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        } else {
            LinearProgressIndicator(
                progress = { report.ratio },
                modifier = Modifier.fillMaxWidth(),
                gapSize = 0.dp,
            )
        }
    }
}

@Composable
fun MemoryMonitor(modifier: Modifier = Modifier) {
    var reports by remember { mutableStateOf(emptyList<MemoryUsage>()) }

    LaunchedEffect(Unit) {
        while (true) {
            reports = reportMemoryUsage()
            delay(1.seconds)
        }
    }

    Column(
        modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        for (report in reports) {
            MemoryMonitorItem(
                report,
                Modifier.fillMaxWidth(),
            )
        }
    }
}
