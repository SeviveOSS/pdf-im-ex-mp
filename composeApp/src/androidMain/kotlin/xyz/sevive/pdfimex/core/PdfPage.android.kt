package xyz.sevive.pdfimex.core

internal class MuPdfPage(
    private val page: com.artifex.mupdf.fitz.Page,
) : PdfPage {
    override val images: List<PdfImage> by lazy {
        pageToImages(page)
    }
}
