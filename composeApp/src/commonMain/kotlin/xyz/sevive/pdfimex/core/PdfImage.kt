package xyz.sevive.pdfimex.core

import korlibs.image.bitmap.Bitmap32

interface PdfImage {
    val width: Int
    val height: Int
    val boundingBox: PdfRect

    suspend fun toBitmap32(): Bitmap32
}
