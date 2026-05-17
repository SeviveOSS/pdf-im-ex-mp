package xyz.sevive.pdfimex.core

import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.div
import io.github.vinceglb.filekit.write
import korlibs.image.bitmap.Bitmap32
import korlibs.image.format.PNG

actual suspend fun saveBitmap32ToGallery(
    bitmap: Bitmap32,
    filenameStem: String,
    dirName: String,
) {
    val dir = PlatformFile("E:/tmp")
    val outputFile = dir / "${filenameStem}.png"

    outputFile.write(PNG.encode(bitmap))
}
