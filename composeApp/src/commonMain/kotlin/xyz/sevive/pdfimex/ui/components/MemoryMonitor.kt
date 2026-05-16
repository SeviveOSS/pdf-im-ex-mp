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
    val usedBytes: Long = 0,
    val maxBytes: Long = 0,
) {
    val availableBytes: Long get() = maxBytes - usedBytes
    val ratio: Float? get() = if (maxBytes == 0L) null else usedBytes / maxBytes.toFloat()
}

data class MemoryUsageReport(
    val device: MemoryUsage = MemoryUsage(),
)

expect fun reportMemoryUsage(): MemoryUsageReport

private fun formatFileSize(bytes: Long): String {
    return when {
        bytes < 1024 -> "$bytes B"
        bytes < 1024 * 1024 -> "${bytes / 1024} KB"
        bytes < 1024 * 1024 * 1024 -> "${bytes / (1024 * 1024)} MB"
        else -> "${"%.1f".format(bytes.toDouble() / (1024 * 1024 * 1024))} GB"
    }
}

@Composable
private fun MemoryMonitorItem(usage: MemoryUsage, label: String, modifier: Modifier = Modifier) {
    val percentageLabel = if (usage.ratio == null) "-" else "${(usage.ratio!! * 100).toInt()}%"
    val usedLabel = formatFileSize(usage.usedBytes)
    val maxLabel = formatFileSize(usage.maxBytes)

    Column(
        modifier,
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Row {
            Text(
                "$label · $percentageLabel ($usedLabel / $maxLabel)",
                style = MaterialTheme.typography.bodySmall,
            )
        }

        if (usage.ratio == null) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        } else {
            LinearProgressIndicator(
                progress = { usage.ratio!! },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
fun MemoryMonitor(modifier: Modifier = Modifier) {
    var report by remember { mutableStateOf(MemoryUsageReport()) }

    LaunchedEffect(Unit) {
        while (true) {
            report = reportMemoryUsage()
            delay(1.seconds)
        }
    }

    Column(
        modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        MemoryMonitorItem(
            report.device,
            "Device",
            Modifier.fillMaxWidth(),
        )
    }
}
