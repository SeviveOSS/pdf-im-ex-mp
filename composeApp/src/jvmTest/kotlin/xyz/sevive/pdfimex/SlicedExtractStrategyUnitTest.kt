package xyz.sevive.pdfimex

import io.github.vinceglb.filekit.readBytes
import korlibs.image.format.readBitmap
import korlibs.io.stream.openAsync
import kotlinx.coroutines.runBlocking
import xyz.sevive.pdfimex.core.PdfBoxEngine
import xyz.sevive.pdfimex.core.SlicedExtractStrategy
import xyz.sevive.pdfimex.core.extractStrategyFactory
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SlicedExtractStrategyUnitTest {
    @Test
    fun testVertical() {
        val pdfFile = TestUtils.getResourceFile("vertical.pdf")

        val singleImageWidth = 794
        val singleImageHeight = 125

        val imageCount = 9

        runBlocking {
            val pdfDoc = PdfBoxEngine().openDocument(pdfFile.readBytes())
            assertEquals(1, pdfDoc.pageCount)

            val page = pdfDoc.loadPage(0)
            val images = page.images
            assertEquals(imageCount, images.size)
            assertEquals(
                List(imageCount) { singleImageWidth to singleImageHeight },
                images.map { it.width to it.height },
            )

            val strategy = extractStrategyFactory(pdfDoc)
            assertTrue { strategy is SlicedExtractStrategy }

            val outputImage = strategy.extractPage(page)
            val expectedImage =
                TestUtils
                    .getResourceFile("vertical.png")
                    .readBytes()
                    .openAsync()
                    .readBitmap()
                    .toBMP32()

            assertEquals(expectedImage.size, outputImage.size)

            // TODO: there are some slightly color differences between the images...
            val tolerance = 3
            expectedImage.zip(outputImage).forEach { (expectedPixel, actualPixel) ->
                assertTrue {
                    abs(expectedPixel.r - actualPixel.r) <= tolerance &&
                        abs(expectedPixel.g - actualPixel.g) <= tolerance &&
                        abs(expectedPixel.b - actualPixel.b) <= tolerance &&
                        abs(expectedPixel.a - actualPixel.a) <= tolerance
                }
            }
        }
    }
}
