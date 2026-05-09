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
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.FileKitMode
import io.github.vinceglb.filekit.dialogs.openFilePicker
import io.github.vinceglb.filekit.nameWithoutExtension
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import xyz.sevive.pdfimex.MainViewModel
import xyz.sevive.pdfimex.ui.components.ExtractStrategySelector
import xyz.sevive.pdfimex.ui.components.MemoryMonitor
import java.text.NumberFormat
import kotlin.time.Duration

private fun Duration.formatETA(): String {
    return toComponents { hours, minutes, seconds, _ ->
        if (hours > 0) {
            "%02d:%02d:%02d".format(hours, minutes, seconds)
        } else {
            "%02d:%02d".format(minutes, seconds)
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

    val percentageNumberFormat = NumberFormat.getPercentInstance()
    percentageNumberFormat.maximumFractionDigits = 0
    val progressLabel = progress?.let { "${progress.first} / ${progress.second}" } ?: "- / -"
    val percentageLabel = ratio?.let { percentageNumberFormat.format(it) } ?: "--%"
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
fun MainScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val vm: MainViewModel = viewModel()

    val uiState by vm.uiState.collectAsStateWithLifecycle()
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
            onClick = {
                coroutineScope.launch { vm.autoSelectStrategy() }
            },
            enabled = !isLoading,
        ) {
            Text("Wow auto select")
        }

        Button(
            onClick = {
                coroutineScope.launch(Dispatchers.IO) {
                    vm.startExtract(context)
                }
            },
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
