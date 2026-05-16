package xyz.sevive.pdfimex.ui.components

actual fun reportMemoryUsage(): MemoryUsageReport {
    val runtime = Runtime.getRuntime()
    return MemoryUsageReport(
        device = MemoryUsage(
            usedBytes = runtime.totalMemory() - runtime.freeMemory(),
            maxBytes = runtime.maxMemory(),
        ),
    )
}
