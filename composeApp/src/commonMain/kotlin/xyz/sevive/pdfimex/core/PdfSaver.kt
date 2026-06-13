package xyz.sevive.pdfimex.core

import korlibs.image.bitmap.Bitmap32

interface PdfSaver {
    suspend fun save(
        bitmap: Bitmap32,
        filenameStem: String,
        dirName: String = "PdfImEx",
    )
}
