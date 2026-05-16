package xyz.sevive.pdfimex.core

import android.util.Log
import com.artifex.mupdf.fitz.Font
import com.artifex.mupdf.fitz.Image
import com.artifex.mupdf.fitz.Matrix
import com.artifex.mupdf.fitz.Page
import com.artifex.mupdf.fitz.Point
import com.artifex.mupdf.fitz.Quad
import com.artifex.mupdf.fitz.Rect
import com.artifex.mupdf.fitz.StructuredTextWalker

internal class ImageStructuredTextWalker(
    val onImage: (image: MuPdfImage) -> Unit,
) : StructuredTextWalker {
    override fun onImageBlock(bbox: Rect?, transform: Matrix?, image: Image?) {
        if (image == null) {
            Log.w("mupdf", "onImageBlock but image is null")
            return
        }
        if (bbox == null) {
            Log.w("mupdf", "onImageBlock but bbox is null")
            return
        }
        onImage(MuPdfImage(image = image, boundingBox = bbox.toPdfRect()))
    }

    override fun beginTextBlock(p0: Rect?) {}
    override fun endTextBlock() {}
    override fun beginLine(p0: Rect?, p1: Int, p2: Point?) {}
    override fun endLine() {}
    override fun onChar(p0: Int, p1: Point?, p2: Font?, p3: Float, p4: Quad?, p5: Int, p6: Int) {}
    override fun beginStruct(p0: String?, p1: String?, p2: Int) {}
    override fun endStruct() {}
    override fun onVector(p0: Rect?, p1: StructuredTextWalker.VectorInfo?, p2: Int) {}
}

internal fun com.artifex.mupdf.fitz.Rect.toPdfRect() =
    PdfRect(x0 = x0, y0 = y0, x1 = x1, y1 = y1)

internal fun pageToImages(page: com.artifex.mupdf.fitz.Page): List<PdfImage> {
    val images = mutableListOf<PdfImage>()
    val sText = page.toStructuredText("preserve-images")
    sText.walk(ImageStructuredTextWalker { images.add(it) })
    return images
}
