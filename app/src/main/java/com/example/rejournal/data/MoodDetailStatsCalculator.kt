package com.example.rejournal.data

import java.time.DayOfWeek

data class TagFrequency(val tag: String, val count: Int)

data class MoodDetailStats(
    val totalOccurrences: Int,
    val topTags: List<TagFrequency>,
    val mostCommonDaysOfWeek: List<DayOfWeek>,
    val averageEnergy: Double,
    val averageProductivity: Double,
    val averageStress: Double,
    val averageSleep: Double
)

object MoodDetailStatsCalculator {

    fun calculate(allEntries: List<MoodEntry>, moodValue: Int): MoodDetailStats {
        val matching = allEntries.filter { it.mood == moodValue }

        val topTags = matching
            .flatMap { it.activities }
            .groupingBy { it }
            .eachCount()
            .map { (tag, count) -> TagFrequency(tag, count) }
            .sortedByDescending { it.count }
            .take(5)

        val countsByDayOfWeek = matching.groupingBy { it.date.dayOfWeek }.eachCount()
        val maxCount = countsByDayOfWeek.values.maxOrNull()
        val mostCommonDaysOfWeek = countsByDayOfWeek.filter { it.value == maxCount }.keys
            .sortedBy { it.value }

        return MoodDetailStats(
            totalOccurrences = matching.size,
            topTags = topTags,
            mostCommonDaysOfWeek = mostCommonDaysOfWeek,
            averageEnergy = if (matching.isNotEmpty()) matching.map { it.energy }.average() else 0.0,
            averageProductivity = if (matching.isNotEmpty()) matching.map { it.productivity }.average() else 0.0,
            averageStress = if (matching.isNotEmpty()) matching.map { it.stress }.average() else 0.0,
            averageSleep = if (matching.isNotEmpty()) matching.map { it.sleep }.average() else 0.0
        )
    }
}