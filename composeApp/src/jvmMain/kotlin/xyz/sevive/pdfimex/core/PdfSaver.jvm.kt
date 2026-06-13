package xyz.sevive.pdfimex.core

import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.createDirectories
import io.github.vinceglb.filekit.div
import io.github.vinceglb.filekit.picturesDir
import io.github.vinceglb.filekit.write
import korlibs.image.bitmap.Bitmap32
import korlibs.image.format.PNG

class FileKitPdfSaver : PdfSaver {
    override suspend fun save(
        bitmap: Bitmap32,
        filenameStem: String,
        dirName: String,
    ) {
        val dir = FileKit.picturesDir / dirName
        dir.createDirectories()

        val outputFile = dir / "$filenameStem.png"

        outputFile.write(PNG.encode(bitmap))
    }
}
