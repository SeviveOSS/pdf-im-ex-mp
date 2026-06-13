package xyz.sevive.pdfimex

import xyz.sevive.pdfimex.core.PdfCleanupProvider

class MuPdfCleanupProvider : PdfCleanupProvider {
    override fun afterPage() {
        // amount is percentage
        com.artifex.mupdf.fitz.Context
            .shrinkStore(75)
        System.gc()
    }

    override fun afterDocument() {
        com.artifex.mupdf.fitz.Context
            .emptyStore()
        System.gc()
    }
}
