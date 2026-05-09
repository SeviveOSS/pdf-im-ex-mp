package xyz.sevive.pdfimex

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.ViewModel
import com.artifex.mupdf.fitz.Document
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.nameWithoutExtension
import io.github.vinceglb.filekit.readBytes
import korlibs.image.format.toAndroidBitmap
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import xyz.sevive.pdfimex.core.ExtractStrategy
import xyz.sevive.pdfimex.core.SimpleExtractStrategy
import xyz.sevive.pdfimex.core.SmoothingEtaEstimator
import xyz.sevive.pdfimex.core.extractStrategyFactory
import kotlin.time.Duration

private fun android.graphics.Bitmap.saveToGallery(context: Context, filenameStem: String) {
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, "$filenameStem.png")
        put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
        // Android 10+ 可以指定文件夹（如 Pictures/MyApp）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/PdfImEx")
            put(MediaStore.MediaColumns.IS_PENDING, 1) // 标记为正在处理，其他应用暂不可见
        }
    }

    val resolver = context.contentResolver
    val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

    uri?.let {
        resolver.openOutputStream(it).use { outputStream ->
            if (outputStream != null) {
                this.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            }
        }

        // 写入完成后，解除 IS_PENDING 状态，让相册扫描到
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentValues.clear()
            contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
            resolver.update(it, contentValues, null, null)
        }
    }
}

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
        _uiState.value = _uiState.value.copy(
            selectedExtractStrategy = strategy
        )
    }

    suspend fun autoSelectStrategy() {
        val selectedFile = _uiState.value.selectedFile ?: return

        _uiState.value = _uiState.value.copy(isLoading = true)

        try {
            val pdfDoc = Document.openDocument(selectedFile.readBytes(), "application/pdf")
            val strategy = extractStrategyFactory(pdfDoc)
            _uiState.value = _uiState.value.copy(selectedExtractStrategy = strategy)
        } catch (e: Exception) {
            Log.e(LOG_TAG, "Error auto selecting strategy", e)
        } finally {
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    suspend fun startExtract(context: Context) {
        val selectedFile = _uiState.value.selectedFile ?: return

        _uiState.value = _uiState.value.copy(isLoading = true)
        etaEstimator.start()

        try {
            val pdfDoc = Document.openDocument(selectedFile.readBytes(), "application/pdf")
            val strategy = _uiState.value.selectedExtractStrategy
            val pageCount = pdfDoc.countPages()

            _uiState.value = _uiState.value.copy(
                progress = 0 to pageCount
            )

            for (pageNum in 0..<pageCount) {
                val page = pdfDoc.loadPage(pageNum)

                val bitmap = strategy.extractPage(page)
                bitmap.toAndroidBitmap().saveToGallery(
                    context,
                    filenameStem = "${selectedFile.nameWithoutExtension}-p${pageNum + 1}"
                )

                // amount is percentage
                com.artifex.mupdf.fitz.Context.shrinkStore(75)
                System.gc()
                etaEstimator.recordStep()

                _uiState.value = _uiState.value.copy(
                    progress = pageNum + 1 to pageCount,
                    eta = etaEstimator.getRemainingTime(pageCount - pageNum),
                )
            }
        } catch (e: Exception) {
            Log.e(LOG_TAG, "Error extracting pdf", e)
        } finally {
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }
}
