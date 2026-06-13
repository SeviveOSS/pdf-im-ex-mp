package xyz.sevive.pdfimex

actual fun cleanupResourceAfterPage() {
    // amount is percentage
    com.artifex.mupdf.fitz.Context
        .shrinkStore(75)
    System.gc()
}

actual fun cleanupResourceAfterDocument() {
    com.artifex.mupdf.fitz.Context
        .emptyStore()
    System.gc()
}
