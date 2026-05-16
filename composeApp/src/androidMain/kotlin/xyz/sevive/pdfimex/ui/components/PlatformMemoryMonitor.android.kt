package xyz.sevive.pdfimex.ui.components

import android.app.ActivityManager
import android.content.Context
import xyz.sevive.pdfimex.core.AndroidApp

actual fun reportMemoryUsage(): MemoryUsageReport {
    val activityManager =
        AndroidApp.appContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val memoryInfo = ActivityManager.MemoryInfo()
    activityManager.getMemoryInfo(memoryInfo)

    return MemoryUsageReport(
        device = MemoryUsage(
            usedBytes = memoryInfo.totalMem - memoryInfo.availMem,
            maxBytes = memoryInfo.totalMem,
        ),
    )
}
