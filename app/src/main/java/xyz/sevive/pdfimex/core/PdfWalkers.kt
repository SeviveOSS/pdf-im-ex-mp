package xyz.sevive.pdfimex.core

import android.util.Log
import com.artifex.mupdf.fitz.Font
import com.artifex.mupdf.fitz.Image
import com.artifex.mupdf.fitz.Matrix
import com.artifex.mupdf.fitz.Point
import com.artifex.mupdf.fitz.Quad
import com.artifex.mupdf.fitz.Rect
import com.artifex.mupdf.fitz.StructuredTextWalker

data class MupdfImageWrapper(
    val boundingBox: Rect,
    val transform: Matrix?,
    val image: Image,
)

class ImageStructuredTextWalker(
    val onImage: (image: MupdfImageWrapper) -> Unit,
) : StructuredTextWalker {
    override fun onImageBlock(bbox: Rect?, transform: Matrix?, image: Image?) {
        if (image == null) {
            Log.w("mupdf", "onImageBlock but image is null. WTF?")
            return
        }

        if (bbox == null) {
            Log.w("mupdf", "onImageBlock but bbox is null. WTF?")
            return
        }

        onImage(MupdfImageWrapper(boundingBox = bbox, transform = transform, image = image))
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