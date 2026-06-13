package xyz.sevive.pdfimex.core

interface PdfCleanupProvider {
    fun afterPage()

    fun afterDocument()
}
