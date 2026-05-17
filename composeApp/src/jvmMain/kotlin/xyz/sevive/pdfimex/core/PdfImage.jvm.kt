package xyz.sevive.pdfimex.core

import korlibs.image.awt.toBMP32
import korlibs.image.bitmap.Bitmap32
import org.apache.pdfbox.pdmodel.graphics.image.PDImage


internal class PdfBoxPdfImage(
    private val pdImage: PDImage,
    override val boundingBox: PdfRect,
) : PdfImage {
    override val width: Int
        get() = pdImage.width
    override val height: Int
        get() = pdImage.height

    override suspend fun toBitmap32(): Bitmap32 {
        return pdImage.image.toBMP32()
    }
}