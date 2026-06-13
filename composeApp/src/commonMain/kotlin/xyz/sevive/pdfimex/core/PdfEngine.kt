package xyz.sevive.pdfimex.core

interface PdfEngine {
    fun openDocument(bytes: ByteArray): PdfDocument
}
