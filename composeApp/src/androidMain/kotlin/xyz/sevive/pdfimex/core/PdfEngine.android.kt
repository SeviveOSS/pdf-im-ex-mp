package xyz.sevive.pdfimex.core

import com.artifex.mupdf.fitz.Context
import com.artifex.mupdf.fitz.Document

class MuPdfEngine : PdfEngine {
    override fun openDocument(bytes: ByteArray): PdfDocument {
        Context.shrinkStore(75)
        return MuPdfDocument(Document.openDocument(bytes, "application/pdf"))
    }
}
