package xyz.sevive.pdfimex.ui.components

import android.os.Debug

actual fun reportMemoryUsage(): List<MemoryUsage> {
    val runtime = Runtime.getRuntime()

    val runtimeMax = runtime.maxMemory()
    val runtimeTotal = runtime.totalMemory()
    val runtimeFree = runtime.freeMemory()
    val runtimeUsed = runtimeTotal - runtimeFree

    val nativeHeap = Debug.getNativeHeapSize()
    val nativeFree = Debug.getNativeHeapFreeSize()
    val nativeUsed = nativeHeap - nativeFree

    return listOf(
        MemoryUsage(
            label = "Runtime",
            usedBytes = runtimeUsed,
            maxBytes = runtimeMax,
        ),
        MemoryUsage(
            label = "Native",
            usedBytes = nativeUsed,
            maxBytes = nativeHeap,
        ),
    )
}
