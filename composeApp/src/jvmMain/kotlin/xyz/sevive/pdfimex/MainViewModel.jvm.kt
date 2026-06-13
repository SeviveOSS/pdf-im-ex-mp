package xyz.sevive.pdfimex

import xyz.sevive.pdfimex.core.PdfCleanupProvider

class JvmPdfCleanupProvider : PdfCleanupProvider {
    override fun afterPage() {
        System.gc()
    }

    override fun afterDocument() {
        System.gc()
    }
}
