package xyz.sevive.pdfimex.core

import org.apache.pdfbox.contentstream.PDFGraphicsStreamEngine
import org.apache.pdfbox.cos.COSName
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.graphics.image.PDImage
import org.apache.pdfbox.util.Matrix
import java.awt.geom.Point2D
import java.awt.geom.Rectangle2D
import kotlin.math.max
import kotlin.math.min

internal class ImageExtractor(
    page: PDPage,
) : PDFGraphicsStreamEngine(page) {
    data class ImageResult(
        val pdImage: PDImage,
        val boundingBox: PdfRect,
    )

    val images = mutableListOf<ImageResult>()

    /**
     * 此函数由 Gemini 生成
     */
    private fun calculateImageBoundingRect(matrix: Matrix): Rectangle2D {
        with(matrix) {
            // 计算变换后的 4 个顶点
            val x1 = translateX
            val y1 = translateY

            val x2 = scalingFactorX + translateX
            val y2 = shearY + translateY

            val x3 = shearX + translateX
            val y3 = scalingFactorY + translateY

            val x4 = scalingFactorX + shearX + translateX
            val y4 = shearY + scalingFactorY + translateY

            // 找出 X 和 Y 的最大值与最小值
            val minX = min(min(x1, x2), min(x3, x4))
            val minY = min(min(y1, y2), min(y3, y4))
            val maxX = max(max(x1, x2), max(x3, x4))
            val maxY = max(max(y1, y2), max(y3, y4))

            // 计算最终的宽度和高度
            val width = maxX - minX
            val height = maxY - minY

            // 注意：PDF 的坐标系原点通常在左下角
            // 对坐标做变换至以左上角为原点，与安卓端使用的 mupdf 行为对齐
            val pageHeight = page.mediaBox.height
            return Rectangle2D.Float(minX, pageHeight - maxY, width, height)
        }
    }

    override fun drawImage(pdImage: PDImage) {
        val rect = calculateImageBoundingRect(graphicsState.currentTransformationMatrix)

        images.add(
            ImageResult(
                pdImage = pdImage,
                boundingBox =
                    PdfRect(
                        x0 = rect.x.toFloat(),
                        y0 = rect.y.toFloat(),
                        x1 = (rect.x + rect.width).toFloat(),
                        y1 = (rect.y + rect.height).toFloat(),
                    ),
            ),
        )
    }

    // Required overrides (no-op implementations)
    override fun appendRectangle(
        p0: Point2D,
        p1: Point2D,
        p2: Point2D,
        p3: Point2D,
    ) {}

    override fun clip(windingRule: Int) {}

    override fun moveTo(
        x: Float,
        y: Float,
    ) {}

    override fun lineTo(
        x: Float,
        y: Float,
    ) {}

    override fun curveTo(
        x1: Float,
        y1: Float,
        x2: Float,
        y2: Float,
        x3: Float,
        y3: Float,
    ) {}

    override fun getCurrentPoint(): Point2D? = null

    override fun closePath() {}

    override fun endPath() {}

    override fun strokePath() {}

    override fun fillPath(windingRule: Int) {}

    override fun fillAndStrokePath(windingRule: Int) {}

    override fun shadingFill(shadingName: COSName) {}
}

internal class PdfBoxPdfPage(
    private val page: PDPage,
) : PdfPage {
    override val images: List<PdfImage>
        get() {
            val extractor = ImageExtractor(page)
            extractor.processPage(page)

            return extractor.images.map {
                PdfBoxPdfImage(pdImage = it.pdImage, boundingBox = it.boundingBox)
            }
        }
}
