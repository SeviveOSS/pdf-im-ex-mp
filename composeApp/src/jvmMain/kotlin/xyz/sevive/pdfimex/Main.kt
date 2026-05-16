package xyz.sevive.pdfimex

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import io.github.vinceglb.filekit.FileKit
import xyz.sevive.pdfimex.ui.MainScreen
import xyz.sevive.pdfimex.ui.theme.PdfImExTheme

fun main() = application {
    FileKit.init(appId = "xyz.sevive.pdfimex")

    val windowState = rememberWindowState(width = 800.dp, height = 600.dp)

    Window(
        onCloseRequest = ::exitApplication,
        title = "PdfImEx",
        state = windowState,
    ) {
        val vm = remember { MainViewModel() }

        PdfImExTheme {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                MainScreen(
                    vm = vm,
                    modifier = Modifier.padding(innerPadding),
                )
            }
        }
    }
}
