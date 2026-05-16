package xyz.sevive.pdfimex.core

import com.artifex.mupdf.fitz.Image
import korlibs.image.bitmap.Bitmap32
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class MuPdfImage(
    private val image: Image,
    override val boundingBox: PdfRect,
) : PdfImage {
    override val width: Int get() = image.width
    override val height: Int get() = image.height

    override suspend fun toBitmap32(): Bitmap32 = withContext(Dispatchers.Default) {
        val pixmap = image.toPixmap()
        val samples = pixmap.samples
        val w = width
        val h = height
        val ints = IntArray(w * h) { i ->
            val offset = i * 4
            val r = samples[offset].toInt() and 0xFF
            val g = samples[offset + 1].toInt() and 0xFF
            val b = samples[offset + 2].toInt() and 0xFF
            val a = samples[offset + 3].toInt() and 0xFF
            (a shl 24) or (r shl 16) or (g shl 8) or b
        }
        Bitmap32(w, h, ints)
    }
}
