package xyz.sevive.pdfimex.core

interface PdfDocument {
    val pageCount: Int

    fun loadPage(index: Int): PdfPage

    fun close()
}
