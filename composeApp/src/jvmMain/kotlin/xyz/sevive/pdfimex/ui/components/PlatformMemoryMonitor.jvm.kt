package xyz.sevive.pdfimex.ui.components

actual fun reportMemoryUsage(): List<MemoryUsage> {
    val runtime = Runtime.getRuntime()
    val maxMemory = runtime.maxMemory()
    val totalMemory = runtime.totalMemory()
    val freeMemory = runtime.freeMemory()

    val usedMemory = totalMemory - freeMemory

    return listOf(
        MemoryUsage(
            label = "Runtime",
            usedBytes = usedMemory,
            maxBytes = maxMemory,
        ),
    )
}
