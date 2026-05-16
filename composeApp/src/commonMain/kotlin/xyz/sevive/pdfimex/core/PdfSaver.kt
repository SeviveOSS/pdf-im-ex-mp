package xyz.sevive.pdfimex.core

import korlibs.image.bitmap.Bitmap32

expect suspend fun saveBitmap32ToGallery(
    bitmap: Bitmap32,
    filenameStem: String,
    dirName: String = "PdfImEx",
)
