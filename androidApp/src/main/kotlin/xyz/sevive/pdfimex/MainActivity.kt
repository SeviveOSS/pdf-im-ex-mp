package xyz.sevive.pdfimex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.init
import xyz.sevive.pdfimex.core.AndroidApp
import xyz.sevive.pdfimex.ui.MainScreen
import xyz.sevive.pdfimex.ui.theme.PdfImExTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FileKit.init(this)
        AndroidApp.init(this)

        enableEdgeToEdge()
        setContent {
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
}
