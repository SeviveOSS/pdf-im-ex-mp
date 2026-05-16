package xyz.sevive.pdfimex.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.FileKitMode
import io.github.vinceglb.filekit.dialogs.openFilePicker
import io.github.vinceglb.filekit.nameWithoutExtension
import kotlinx.coroutines.launch
import xyz.sevive.pdfimex.MainViewModel
import xyz.sevive.pdfimex.ui.components.ExtractStrategySelector
import xyz.sevive.pdfimex.ui.components.MemoryMonitor
import kotlin.time.Duration
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.compose.collectAsStateWithLifecycle

internal fun Duration.formatETA(): String {
    return toComponents { hours, minutes, seconds, _ ->
        if (hours > 0) {
            "${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"
        } else {
            "${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"
        }
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

@Composable
fun MainScreen(
    vm: MainViewModel,
    modifier: Modifier = Modifier,
) {
    val coroutineScope = rememberCoroutineScope()

    val uiState = vm.uiState.collectAsStateWithLifecycle().value
    val isLoading = uiState.isLoading
    val selectedFile = uiState.selectedFile

    Column(modifier) {
        MemoryMonitor(Modifier.fillMaxWidth())

        Text(text = selectedFile?.nameWithoutExtension ?: "")

        ExtractStrategySelector(
            currentStrategy = uiState.selectedExtractStrategy,
            onStrategyChange = { vm.selectExtractStrategy(it) },
            enabled = !isLoading,
        )

        Button(
            onClick = {
                coroutineScope.launch {
                    FileKit.openFilePicker(mode = FileKitMode.Single)?.let {
                        vm.selectFile(it)
                    }
                }
            },
            enabled = !isLoading,
        ) {
            Text("Wow choose")
        }

        Button(
            onClick = { coroutineScope.launch { vm.autoSelectStrategy() } },
            enabled = !isLoading,
        ) {
            Text("Wow auto select")
        }

        Button(
            onClick = { coroutineScope.launch { vm.startExtract() } },
            enabled = !isLoading,
        ) {
            Text("Wow extract")
        }

        ExportProgress(
            progress = uiState.progress,
            eta = uiState.eta,
            Modifier.fillMaxWidth(),
        )
    }
}
