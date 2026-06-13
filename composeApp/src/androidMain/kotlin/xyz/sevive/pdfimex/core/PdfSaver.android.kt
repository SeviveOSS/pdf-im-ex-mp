package xyz.sevive.pdfimex.core

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import korlibs.image.bitmap.Bitmap32
import korlibs.image.format.toAndroidBitmap

class MediaStorePdfSaver(
    private val context: Context,
) : PdfSaver {
    override suspend fun save(
        bitmap: Bitmap32,
        filenameStem: String,
        dirName: String,
    ) {
        bitmap.toAndroidBitmap().saveToGallery(filenameStem, dirName)
    }

    private fun Bitmap.saveToGallery(
        filenameStem: String,
        dirName: String,
    ) {
        val contentValues =
            ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, "$filenameStem.png")
                put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.MediaColumns.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/$dirName")
                    put(MediaStore.MediaColumns.IS_PENDING, 1)
                }
            }

        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        uri?.let {
            resolver.openOutputStream(it).use { outputStream ->
                if (outputStream != null) {
                    this.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.clear()
                contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                resolver.update(it, contentValues, null, null)
            }
        }
    }
}
