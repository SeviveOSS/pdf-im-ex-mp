package xyz.sevive.pdfimex.ui.components

import android.app.ActivityManager
import android.content.Context
import android.text.format.Formatter
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import korlibs.time.seconds
import kotlinx.coroutines.delay
import java.text.NumberFormat

data class MemoryUsage(
    val usedBytes: Long = 0,
    val maxBytes: Long = 0,
) {
    val availableBytes = maxBytes - usedBytes
    val ratio = if (maxBytes == 0L) null else usedBytes / maxBytes.toFloat()
}

data class MemoryUsageReport(
    val device: MemoryUsage = MemoryUsage(),
)


fun reportMemoryUsage(context: Context): MemoryUsageReport {

    val activityManager = context.getSystemService(ActivityManager::class.java)
    val memoryInfo = ActivityManager.MemoryInfo()
    activityManager.getMemoryInfo(memoryInfo)


    return MemoryUsageReport(
        device = MemoryUsage(
            usedBytes = memoryInfo.totalMem - memoryInfo.availMem,
            maxBytes = memoryInfo.totalMem,
        ),
    )
}

@Composable
private fun MemoryMonitorItem(usage: MemoryUsage, label: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current

    val percentageNumberFormat = NumberFormat.getPercentInstance()
    percentageNumberFormat.maximumFractionDigits = 0

    val percentageLabel =
        if (usage.ratio == null) "-" else percentageNumberFormat.format(usage.ratio)

    val usedLabel = Formatter.formatFileSize(context, usage.usedBytes)
    val maxLabel = Formatter.formatFileSize(context, usage.maxBytes)

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
                progress = { usage.ratio },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
fun MemoryMonitor(modifier: Modifier = Modifier) {
    var report by remember { mutableStateOf(MemoryUsageReport()) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        while (true) {
            report = reportMemoryUsage(context)
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
