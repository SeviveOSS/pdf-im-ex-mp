package xyz.sevive.pdfimex.core

import com.artifex.mupdf.fitz.Context
import com.artifex.mupdf.fitz.Document
import com.artifex.mupdf.fitz.Page
import korlibs.image.bitmap.Bitmap
import korlibs.image.bitmap.Bitmap32
import korlibs.image.format.readBitmap
import korlibs.io.stream.openAsync

fun pageToImages(page: Page): List<MupdfImageWrapper> {
    val images = mutableListOf<MupdfImageWrapper>()

    val sText = page.toStructuredText("preserve-images")
    sText.walk(
        ImageStructuredTextWalker(onImage = { images.add(it) }),
    )
    return images
}

class ExtractStrategyException(
    message: String?,
    throwable: Throwable? = null,
) : Exception(message, throwable)

interface ExtractStrategy {
    fun getName(): String
    suspend fun isPreferred(page: Page): Boolean
    suspend fun extractPage(page: Page): Bitmap
}

object SimpleExtractStrategy : ExtractStrategy {
    override fun getName(): String = "simple"

    override suspend fun isPreferred(page: Page): Boolean {
        val images = pageToImages(page)

        return images.size == 1
    }

    override suspend fun extractPage(page: Page): Bitmap {
        val images = pageToImages(page)

        // select the largest image
        val image = images.maxByOrNull {
            it.image.width * it.image.height
        } ?: throw ExtractStrategyException("No images on page.")

        return image.image.toPixmap().asPNG().asByteArray().openAsync().readBitmap()
    }
}

object SlicedExtractStrategy : ExtractStrategy {
    override fun getName(): String = "sliced"

    private enum class SliceDirection { VERTICAL, HORIZONTAL }

    private fun detectSliceDirection(
        images: List<MupdfImageWrapper>,
        threshold: Double = 0.01,
    ): SliceDirection? {
        val xCenters = mutableListOf<Float>()
        val yCenters = mutableListOf<Float>()

        images.forEach {
            val xCenter = (it.boundingBox.x1 - it.boundingBox.x0) / 2
            val yCenter = (it.boundingBox.y1 - it.boundingBox.y0) / 2

            xCenters.add(xCenter)
            yCenters.add(yCenter)
        }

        val xDiff = xCenters.max() - xCenters.min()
        val yDiff = yCenters.max() - yCenters.min()

        return when {
            xDiff < threshold && yDiff < threshold -> null
            xDiff < threshold -> SliceDirection.VERTICAL
            yDiff < threshold -> SliceDirection.HORIZONTAL
            else -> null
        }
    }

    override suspend fun isPreferred(page: Page): Boolean {
        fun <T, R> isListEqual(list: List<T>, selector: (T) -> R): Boolean {
            return list.map { selector(it) }.toSet().size == 1
        }

        val images = pageToImages(page)
        if (images.size < 2) return false

        val direction = detectSliceDirection(images) ?: return false

        return if (direction == SliceDirection.HORIZONTAL) isListEqual(images, { it.image.height })
        else isListEqual(images, { it.image.width })
    }

    override suspend fun extractPage(page: Page): Bitmap {
        val rawImages = pageToImages(page)
        if (rawImages.isEmpty()) throw ExtractStrategyException("No images on page.")

        val direction = detectSliceDirection(rawImages)
            ?: throw ExtractStrategyException("Cannot confirm slice direction.")

        val images = rawImages.sortedBy {
            if (direction == SliceDirection.VERTICAL) it.boundingBox.y0 else it.boundingBox.x0
        }

        val (width, height) = if (direction == SliceDirection.VERTICAL) {
            images.maxOf { it.image.width } to images.sumOf { it.image.height }
        } else {
            images.maxOf { it.image.height } to images.sumOf { it.image.width }
        }

        val canvas = Bitmap32(width = width, height = height)

        var pasteOffset = 0
        for (image in images) {
            val slice =
                image.image.toPixmap().asPNG().asByteArray().openAsync().readBitmap().toBMP32()

            if (direction == SliceDirection.VERTICAL) {
                canvas.put(slice, 0, pasteOffset)
                pasteOffset += slice.height
            } else {
                canvas.put(slice, pasteOffset, 0)
                pasteOffset += slice.width
            }
        }

        return canvas
    }
}

suspend fun extractStrategyFactory(pdfDoc: Document): ExtractStrategy {
    val totalPages = pdfDoc.countPages()

    val strategiesScore = mutableMapOf(
        SimpleExtractStrategy to 0,
        SlicedExtractStrategy to 0,
    )

    for (pageNum in 0..<totalPages) {
        val page = pdfDoc.loadPage(pageNum)

        for ((strategy, score) in strategiesScore) {
            if (strategy.isPreferred(page)) strategiesScore[strategy] = score + 1
        }

        Context.shrinkStore(75)
    }

    if (strategiesScore.values.sum() == 0) {
        // TODO: return null...
        return SimpleExtractStrategy
    }

    return strategiesScore.maxBy { it.value }.key
}
