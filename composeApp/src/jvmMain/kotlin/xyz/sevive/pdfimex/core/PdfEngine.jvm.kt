package xyz.sevive.pdfimex.core

import org.apache.pdfbox.Loader

actual fun openPdfDocument(bytes: ByteArray): PdfDocument {
    val doc = Loader.loadPDF(bytes)
    return PdfBoxPdfDocument(doc)
}
