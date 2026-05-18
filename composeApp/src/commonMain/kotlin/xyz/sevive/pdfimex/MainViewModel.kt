package xyz.sevive.pdfimex

import androidx.lifecycle.ViewModel
import co.touchlab.kermit.Logger
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.nameWithoutExtension
import io.github.vinceglb.filekit.readBytes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import xyz.sevive.pdfimex.core.ExtractStrategy
import xyz.sevive.pdfimex.core.SimpleExtractStrategy
import xyz.sevive.pdfimex.core.SmoothingEtaEstimator
import xyz.sevive.pdfimex.core.extractStrategyFactory
import xyz.sevive.pdfimex.core.openPdfDocument
import xyz.sevive.pdfimex.core.saveBitmap32ToGallery
import kotlin.time.Duration

expect fun cleanupResourceAfterPage()
expect fun cleanupResourceAfterDocument()

data class MainUiState(
    val selectedFile: PlatformFile? = null,
    val selectedExtractStrategy: ExtractStrategy = SimpleExtractStrategy,
    val isLoading: Boolean = false,
    val progress: Pair<Int, Int>? = null,
    val eta: Duration? = null,
)

class MainViewModel : ViewModel() {
    companion object {
        const val LOG_TAG = "MainVM"
    }

    private val logger = Logger.withTag(LOG_TAG)

    private var etaEstimator = SmoothingEtaEstimator(10)

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState = _uiState.asStateFlow()

    fun selectFile(file: PlatformFile) {
        _uiState.value = _uiState.value.copy(
            selectedFile = file,
            isLoading = false,
            progress = null,
        )
    }

    fun selectExtractStrategy(strategy: ExtractStrategy) {
        _uiState.value = _uiState.value.copy(selectedExtractStrategy = strategy)
    }

    suspend fun autoSelectStrategy() {
        val selectedFile = _uiState.value.selectedFile ?: return

        _uiState.value = _uiState.value.copy(isLoading = true)

        try {
            val pdfBytes = selectedFile.readBytes()
            val pdfDoc = openPdfDocument(pdfBytes)
            val strategy = extractStrategyFactory(pdfDoc)
            _uiState.value = _uiState.value.copy(selectedExtractStrategy = strategy)
            pdfDoc.close()
        } catch (e: Exception) {
            logger.e(e) { "Error auto selecting strategy" }
        } finally {
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    suspend fun startExtract() {
        val selectedFile = _uiState.value.selectedFile ?: return

        _uiState.value = _uiState.value.copy(isLoading = true)
        etaEstimator.start()

        try {
            val pdfBytes = selectedFile.readBytes()
            val pdfDoc = openPdfDocument(pdfBytes)
            val strategy = _uiState.value.selectedExtractStrategy
            val pageCount = pdfDoc.pageCount

            _uiState.value = _uiState.value.copy(progress = 0 to pageCount)

            for (pageNum in 0..<pageCount) {
                cleanupResourceAfterPage()

                val page = pdfDoc.loadPage(pageNum)

                val bitmap = strategy.extractPage(page)
                saveBitmap32ToGallery(
                    bitmap,
                    filenameStem = "${selectedFile.nameWithoutExtension}-p${pageNum + 1}",
                )

                etaEstimator.recordStep()

                _uiState.value = _uiState.value.copy(
                    progress = pageNum + 1 to pageCount,
                    eta = etaEstimator.getRemainingTime(pageCount - pageNum),
                )
            }

            pdfDoc.close()
            cleanupResourceAfterDocument()
        } catch (e: Exception) {
            logger.e(e) { "Error extracting pdf" }
        } finally {
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }
}
