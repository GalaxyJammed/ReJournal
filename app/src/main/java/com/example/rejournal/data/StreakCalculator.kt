package com.example.rejournal.data

import java.time.LocalDate

data class StreakInfo(val currentStreak: Int, val longestStreak: Int)

object StreakCalculator {
    fun calculate(entries: List<MoodEntry>): StreakInfo {
        if (entries.isEmpty()) return StreakInfo(0, 0)

        val dates = entries.map { it.date }.toSortedSet()
        val sortedList = dates.toList()

        var longest = 1
        var run = 1
        for (i in 1 until sortedList.size) {
            run = if (sortedList[i] == sortedList[i - 1].plusDays(1)) run + 1 else 1
            if (run > longest) longest = run
        }

        val today = LocalDate.now()
        var current = 0
        var cursor = if (dates.contains(today)) today else today.minusDays(1)
        while (dates.contains(cursor)) {
            current++
            cursor = cursor.minusDays(1)
        }

        return StreakInfo(currentStreak = current, longestStreak = longest)
    }
}