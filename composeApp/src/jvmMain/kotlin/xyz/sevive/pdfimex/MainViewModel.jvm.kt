package xyz.sevive.pdfimex

actual fun cleanupResourceAfterPage() {
    System.gc()
}

actual fun cleanupResourceAfterDocument() {
    System.gc()
}
