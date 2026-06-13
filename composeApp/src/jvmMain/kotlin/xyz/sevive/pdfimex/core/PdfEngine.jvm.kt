package xyz.sevive.pdfimex.core

import org.apache.pdfbox.Loader
import org.apache.pdfbox.cos.COSObject
import org.apache.pdfbox.pdmodel.DefaultResourceCache
import org.apache.pdfbox.pdmodel.graphics.PDXObject
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject

/**
 * 禁用图像缓存，优化内存占用量
 */
internal class NoImageResourceCache : DefaultResourceCache() {
    override fun put(
        indirect: COSObject?,
        xobject: PDXObject?,
    ) {
        if (xobject is PDImageXObject) {
            // 不缓存图像
            return
        }

        super.put(indirect, xobject)
    }
}

class PdfBoxEngine : PdfEngine {
    override fun openDocument(bytes: ByteArray): PdfDocument {
        val doc = Loader.loadPDF(bytes)
        doc.resourceCache = NoImageResourceCache()

        return PdfBoxPdfDocument(doc)
    }
}
