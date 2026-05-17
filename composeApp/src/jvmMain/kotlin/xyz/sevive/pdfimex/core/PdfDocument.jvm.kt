package xyz.sevive.pdfimex.core

import org.apache.pdfbox.pdmodel.PDDocument

internal class PdfBoxPdfDocument(private val doc: PDDocument) : PdfDocument {
    override val pageCount: Int
        get() = doc.numberOfPages

    override fun loadPage(index: Int): PdfPage {
        return PdfBoxPdfPage(doc.getPage(index))
    }

    override fun close() {
        doc.close()
    }
}