package xyz.sevive.pdfimex.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.FileKitMode
import io.github.vinceglb.filekit.dialogs.openFilePicker
import io.github.vinceglb.filekit.nameWithoutExtension
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import xyz.sevive.pdfimex.MainViewModel
import xyz.sevive.pdfimex.ui.components.ExportProgress
import xyz.sevive.pdfimex.ui.components.ExtractStrategySelector
import xyz.sevive.pdfimex.ui.components.MemoryMonitor

@Composable
fun MainScreen(
    vm: MainViewModel,
    modifier: Modifier = Modifier,
) {
    val coroutineScope = rememberCoroutineScope()

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
            onClick = { coroutineScope.launch(Dispatchers.IO) { vm.autoSelectStrategy() } },
            enabled = !isLoading,
        ) {
            Text("Wow auto select")
        }

        Button(
            onClick = {
                // TODO: this crashes when app goes into background!
                coroutineScope.launch(Dispatchers.IO) { vm.startExtract() }
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
