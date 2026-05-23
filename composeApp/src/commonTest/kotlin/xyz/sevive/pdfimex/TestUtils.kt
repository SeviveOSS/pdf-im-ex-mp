package xyz.sevive.pdfimex

import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.div
import io.github.vinceglb.filekit.projectDir

class TestUtils {
    companion object {
        val resourceDirectory = FileKit.projectDir / "src/commonTest/resources"

        fun getResourceFile(filename: String): PlatformFile {
            return PlatformFile(resourceDirectory, filename)
        }
    }
}
