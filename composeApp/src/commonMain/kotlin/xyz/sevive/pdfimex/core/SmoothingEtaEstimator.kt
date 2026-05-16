package xyz.sevive.pdfimex.core

import kotlin.collections.ArrayDeque
import kotlin.time.Duration
import kotlin.time.TimeMark
import kotlin.time.TimeSource

class SmoothingEtaEstimator(private val windowSize: Int = 20) {
    private val durations = ArrayDeque<Duration>(windowSize)
    private var lastMark: TimeMark? = null

    fun start() {
        lastMark = TimeSource.Monotonic.markNow()
        durations.clear()
    }

    fun recordStep() {
        val currentMark = TimeSource.Monotonic.markNow()
        lastMark?.let { mark ->
            val elapsed = mark.elapsedNow()
            if (durations.size >= windowSize) {
                durations.removeFirst()
            }
            durations.addLast(elapsed)
        }
        lastMark = currentMark
    }

    fun getRemainingTime(remainingItems: Int): Duration? {
        if (durations.isEmpty() || remainingItems <= 0) return null

        val totalDuration = durations.reduce { acc, duration -> acc + duration }
        val avgDuration = totalDuration / durations.size

        return avgDuration * remainingItems
    }
}
