package xyz.sevive.pdfimex.core

internal class MuPdfDocument(
    private val doc: com.artifex.mupdf.fitz.Document,
) : PdfDocument {
    override val pageCount: Int get() = doc.countPages()

    override fun loadPage(index: Int): PdfPage = MuPdfPage(doc.loadPage(index))

    override fun close() {
        doc.destroy()
    }
}
