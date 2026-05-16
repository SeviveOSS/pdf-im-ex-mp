package xyz.sevive.pdfimex.core

import korlibs.image.bitmap.Bitmap32
import kotlin.math.max

class ExtractStrategyException(
    message: String?,
    throwable: Throwable? = null,
) : Exception(message, throwable)

interface ExtractStrategy {
    fun getName(): String
    suspend fun isPreferred(page: PdfPage): Boolean
    suspend fun extractPage(page: PdfPage): Bitmap32
}

object SimpleExtractStrategy : ExtractStrategy {
    override fun getName(): String = "simple"

    override suspend fun isPreferred(page: PdfPage): Boolean {
        return page.images.size == 1
    }

    override suspend fun extractPage(page: PdfPage): Bitmap32 {
        val image = page.images.maxByOrNull { it.width * it.height }
            ?: throw ExtractStrategyException("No images on page.")

        return image.toBitmap32()
    }
}

object SlicedExtractStrategy : ExtractStrategy {
    override fun getName(): String = "sliced"

    private enum class SliceDirection { VERTICAL, HORIZONTAL }

    private fun detectSliceDirection(
        images: List<PdfImage>,
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

    override suspend fun isPreferred(page: PdfPage): Boolean {
        fun <T, R> isListEqual(list: List<T>, selector: (T) -> R): Boolean {
            return list.map { selector(it) }.toSet().size == 1
        }

        val images = page.images
        if (images.size < 2) return false

        val direction = detectSliceDirection(images) ?: return false

        return if (direction == SliceDirection.HORIZONTAL) {
            isListEqual(images) { it.height }
        } else {
            isListEqual(images) { it.width }
        }
    }

    override suspend fun extractPage(page: PdfPage): Bitmap32 {
        val rawImages = page.images
        if (rawImages.isEmpty()) throw ExtractStrategyException("No images on page.")

        val direction = detectSliceDirection(rawImages)
            ?: throw ExtractStrategyException("Cannot confirm slice direction.")

        val sortedImages = rawImages.sortedBy {
            if (direction == SliceDirection.VERTICAL) it.boundingBox.y0 else it.boundingBox.x0
        }

        val (width, height) = if (direction == SliceDirection.VERTICAL) {
            sortedImages.maxOf { it.width } to sortedImages.sumOf { it.height }
        } else {
            sortedImages.maxOf { it.height } to sortedImages.sumOf { it.width }
        }

        val canvas = Bitmap32(width = max(width, 1), height = max(height, 1))

        var pasteOffset = 0
        for (image in sortedImages) {
            val slice = image.toBitmap32()

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

suspend fun extractStrategyFactory(pdfDoc: PdfDocument): ExtractStrategy {
    val totalPages = pdfDoc.pageCount

    val strategiesScore = mutableMapOf(
        SimpleExtractStrategy to 0,
        SlicedExtractStrategy to 0,
    )

    for (pageNum in 0..<totalPages) {
        val page = pdfDoc.loadPage(pageNum)

        for ((strategy, score) in strategiesScore) {
            if (strategy.isPreferred(page)) strategiesScore[strategy] = score + 1
        }
    }

    if (strategiesScore.values.sum() == 0) {
        return SimpleExtractStrategy
    }

    return strategiesScore.maxBy { it.value }.key
}
