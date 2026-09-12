package com.example.rejournal.data

import java.time.DayOfWeek
import java.time.LocalDate
import kotlin.math.sqrt

enum class StatsPeriod { WEEK, MONTH, YEAR }

data class ActivityInsight(val tag: String, val averageMood: Double, val count: Int)
data class ActivityFrequency(val tag: String, val count: Int)

data class PeriodStats(
    val totalEntries: Int,
    val moodCounts: Map<Int, Int>,
    val averageMood: Double,
    val averageEnergy: Double,
    val averageProductivity: Double,
    val averageStress: Double,
    val averageSleep: Double,
    val bestDaysOfWeek: List<DayOfWeek>,
    val toughestDaysOfWeek: List<DayOfWeek>,
    val bestDays: List<MoodEntry>,
    val worstDays: List<MoodEntry>,
    val activityInsights: List<ActivityInsight>,
    val energyMoodCorrelation: Double?,
    val productivityMoodCorrelation: Double?,
    val stressMoodCorrelation: Double?,
    val sleepMoodCorrelation: Double?,
    val mostLoggedActivities: List<ActivityFrequency>,
    val bestDayActivities: List<ActivityFrequency>,
    val worstDayActivities: List<ActivityFrequency>
)

object StatsCalculator {

    private const val MIN_ENTRIES_FOR_CORRELATION = 5
    private const val MIN_OCCURRENCES_FOR_ACTIVITY_INSIGHT = 2

    fun rangeFor(period: StatsPeriod, referenceDate: LocalDate): Pair<LocalDate, LocalDate> {
        return when (period) {
            StatsPeriod.WEEK -> {
                val startOffset = referenceDate.dayOfWeek.value % 7
                val start = referenceDate.minusDays(startOffset.toLong())
                start to start.plusDays(6)
            }
            StatsPeriod.MONTH -> {
                val start = referenceDate.withDayOfMonth(1)
                start to start.plusMonths(1).minusDays(1)
            }
            StatsPeriod.YEAR -> {
                val start = referenceDate.withDayOfYear(1)
                start to start.withMonth(12).withDayOfMonth(31)
            }
        }
    }

    fun calculate(entries: List<MoodEntry>, period: StatsPeriod, referenceDate: LocalDate): PeriodStats {
        val (start, end) = rangeFor(period, referenceDate)
        val periodEntries = entries.filter { it.date >= start && it.date <= end }

        val moodCounts = (1..5).associateWith { moodValue -> periodEntries.count { it.mood == moodValue } }
        val averageMood = if (periodEntries.isNotEmpty()) periodEntries.map { it.mood }.average() else 0.0
        val averageEnergy = if (periodEntries.isNotEmpty()) periodEntries.map { it.energy }.average() else 0.0
        val averageProductivity = if (periodEntries.isNotEmpty()) periodEntries.map { it.productivity }.average() else 0.0
        val averageStress = if (periodEntries.isNotEmpty()) periodEntries.map { it.stress }.average() else 0.0
        val averageSleep = if (periodEntries.isNotEmpty()) periodEntries.map { it.sleep }.average() else 0.0

        val entriesByDayOfWeek = periodEntries.groupBy { it.date.dayOfWeek }
        val averageByDayOfWeek: Map<DayOfWeek, Double> = entriesByDayOfWeek
            .mapValues { (_, list) -> Math.round(list.map { it.mood }.average() * 1000.0) / 1000.0 }
        val countByDayOfWeek: Map<DayOfWeek, Int> = entriesByDayOfWeek.mapValues { (_, list) -> list.size }

        val maxAvg = averageByDayOfWeek.values.maxOrNull()
        val minAvg = averageByDayOfWeek.values.minOrNull()

        val bestCandidates = averageByDayOfWeek.filter { it.value == maxAvg }.keys
        val bestMaxCount = bestCandidates.maxOfOrNull { countByDayOfWeek[it] ?: 0 }
        val bestDaysOfWeek = bestCandidates.filter { countByDayOfWeek[it] == bestMaxCount }
            .sortedBy { it.value }

        val toughestCandidates = averageByDayOfWeek.filter { it.value == minAvg }.keys
        val toughestMaxCount = toughestCandidates.maxOfOrNull { countByDayOfWeek[it] ?: 0 }
        val toughestDaysOfWeek = toughestCandidates.filter { countByDayOfWeek[it] == toughestMaxCount }
            .sortedBy { it.value }

        val maxMood = periodEntries.maxOfOrNull { it.mood }
        val minMood = periodEntries.minOfOrNull { it.mood }
        val bestDays = periodEntries.filter { it.mood == maxMood }.sortedBy { it.date }
        val worstDays = periodEntries.filter { it.mood == minMood }.sortedBy { it.date }

        val activityInsights = periodEntries
            .flatMap { entry -> entry.activities.map { tag -> tag to entry.mood } }
            .groupBy({ it.first }, { it.second })
            .filter { (_, moods) -> moods.size >= MIN_OCCURRENCES_FOR_ACTIVITY_INSIGHT }
            .map { (tag, moods) -> ActivityInsight(tag, moods.average(), moods.size) }
            .sortedByDescending { it.averageMood }

        val energyCorrelation = if (periodEntries.size >= MIN_ENTRIES_FOR_CORRELATION) {
            correlation(periodEntries.map { it.energy.toDouble() }, periodEntries.map { it.mood.toDouble() })
        } else null
        val productivityCorrelation = if (periodEntries.size >= MIN_ENTRIES_FOR_CORRELATION) {
            correlation(periodEntries.map { it.productivity.toDouble() }, periodEntries.map { it.mood.toDouble() })
        } else null
        val stressCorrelation = if (periodEntries.size >= MIN_ENTRIES_FOR_CORRELATION) {
            correlation(periodEntries.map { it.stress.toDouble() }, periodEntries.map { it.mood.toDouble() })
        } else null
        val sleepCorrelation = if (periodEntries.size >= MIN_ENTRIES_FOR_CORRELATION) {
            correlation(periodEntries.map { it.sleep.toDouble() }, periodEntries.map { it.mood.toDouble() })
        } else null

        val mostLoggedActivities = periodEntries
            .flatMap { it.activities }
            .groupingBy { it }
            .eachCount()
            .map { (tag, count) -> ActivityFrequency(tag, count) }
            .sortedByDescending { it.count }

        val bestDayActivities = tagFrequencies(bestDays)
        val worstDayActivities = tagFrequencies(worstDays)

        return PeriodStats(
            totalEntries = periodEntries.size,
            moodCounts = moodCounts,
            averageMood = averageMood,
            averageEnergy = averageEnergy,
            averageProductivity = averageProductivity,
            averageStress = averageStress,
            averageSleep = averageSleep,
            bestDaysOfWeek = bestDaysOfWeek,
            toughestDaysOfWeek = toughestDaysOfWeek,
            bestDays = bestDays,
            worstDays = worstDays,
            activityInsights = activityInsights,
            energyMoodCorrelation = energyCorrelation,
            productivityMoodCorrelation = productivityCorrelation,
            stressMoodCorrelation = stressCorrelation,
            sleepMoodCorrelation = sleepCorrelation,
            mostLoggedActivities = mostLoggedActivities,
            bestDayActivities = bestDayActivities,
            worstDayActivities = worstDayActivities
        )
    }

    private fun tagFrequencies(entries: List<MoodEntry>): List<ActivityFrequency> {
        return entries
            .flatMap { it.activities }
            .groupingBy { it }
            .eachCount()
            .map { (tag, count) -> ActivityFrequency(tag, count) }
            .sortedByDescending { it.count }
    }

    private fun correlation(xs: List<Double>, ys: List<Double>): Double {
        val n = xs.size
        if (n < 2) return 0.0
        val meanX = xs.average()
        val meanY = ys.average()
        var numerator = 0.0
        var sumSqX = 0.0
        var sumSqY = 0.0
        for (i in xs.indices) {
            val dx = xs[i] - meanX
            val dy = ys[i] - meanY
            numerator += dx * dy
            sumSqX += dx * dx
            sumSqY += dy * dy
        }
        val denominator = sqrt(sumSqX * sumSqY)
        return if (denominator == 0.0) 0.0 else numerator / denominator
    }
}