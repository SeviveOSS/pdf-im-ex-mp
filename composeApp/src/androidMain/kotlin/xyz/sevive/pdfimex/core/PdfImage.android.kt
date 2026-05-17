package xyz.sevive.pdfimex.core

import com.artifex.mupdf.fitz.Image
import korlibs.image.bitmap.Bitmap32
import korlibs.image.format.readBitmap
import korlibs.io.stream.openAsync

internal class MuPdfImage(
    private val image: Image,
    override val boundingBox: PdfRect,
) : PdfImage {
    override val width: Int get() = image.width
    override val height: Int get() = image.height

    override suspend fun toBitmap32(): Bitmap32 =
        image.toPixmap().asPNG().asByteArray().openAsync().readBitmap().toBMP32()
}
