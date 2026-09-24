package com.example.rejournal.data

import android.content.Context

object AchievementCalculator {

    fun currentValue(
        metric: AchievementMetric,
        entries: List<MoodEntry>,
        goalCompletions: Int,
        timeCapsuleCount: Int,
        testsTakenCount: Int = 0,
        microWinsCount: Int = 0,
        mixtapesCount: Int = 0
    ): Int {
        return when (metric) {
            AchievementMetric.TOTAL_ENTRIES -> entries.map { it.date }.distinct().size
            AchievementMetric.BEST_STREAK -> StreakCalculator.calculate(entries).longestStreak
            AchievementMetric.GOALS_COMPLETED -> goalCompletions
            AchievementMetric.HAS_PHOTO -> entries.count { it.photoPaths.isNotEmpty() }
            AchievementMetric.HAS_AUDIO -> entries.count { it.audioPaths.isNotEmpty() }
            AchievementMetric.HAS_FAVORITE -> entries.count { it.isFavorite }
            AchievementMetric.HAS_TIME_CAPSULE -> timeCapsuleCount
            AchievementMetric.WELLNESS_DAYS -> entries.filter { it.energy >= 4 && it.stress <= 2 && it.sleep >= 4 }.map { it.date }.distinct().size
            AchievementMetric.UNIQUE_TAGS -> entries.flatMap { it.activities }.distinct().size
            AchievementMetric.THOROUGH_LOGS -> entries.count { it.note.length > 200 }
            AchievementMetric.TESTS_TAKEN -> testsTakenCount
            AchievementMetric.MICRO_WINS_LOGGED -> microWinsCount
            AchievementMetric.MIXTAPES_EARNED -> mixtapesCount
        }
    }

    fun currentTierIndex(group: AchievementGroup, currentValue: Int): Int {
        var index = -1
        group.tiers.forEachIndexed { i, tier -> if (currentValue >= tier.target) index = i }
        return index
    }

    fun computeNewlyUnlockedTierIds(
        context: Context,
        entries: List<MoodEntry>,
        goalCompletions: Int,
        timeCapsuleCount: Int,
        testsTakenCount: Int = 0,
        microWinsCount: Int = 0,
        mixtapesCount: Int = 0
    ): Set<String> {
        val alreadyUnlocked = AchievementPrefs.getUnlockedIds(context)
        val newly = mutableSetOf<String>()
        AchievementDefinitions.groups.forEach { group ->
            val value = currentValue(group.metric, entries, goalCompletions, timeCapsuleCount, testsTakenCount, microWinsCount, mixtapesCount)
            group.tiers.forEach { tier ->
                if (tier.id !in alreadyUnlocked && value >= tier.target) newly.add(tier.id)
            }
        }
        return newly
    }
}
