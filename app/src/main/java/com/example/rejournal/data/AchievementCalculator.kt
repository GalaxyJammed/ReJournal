package com.example.rejournal.data

import android.content.Context

object AchievementCalculator {

    fun currentValue(metric: AchievementMetric, entries: List<MoodEntry>, goalCompletions: Int, timeCapsuleCount: Int): Int {
        return when (metric) {
            AchievementMetric.TOTAL_ENTRIES -> entries.size
            AchievementMetric.BEST_STREAK -> StreakCalculator.calculate(entries).longestStreak
            AchievementMetric.GOALS_COMPLETED -> goalCompletions
            AchievementMetric.HAS_PHOTO -> if (entries.any { it.photoPaths.isNotEmpty() }) 1 else 0
            AchievementMetric.HAS_AUDIO -> if (entries.any { it.audioPaths.isNotEmpty() }) 1 else 0
            AchievementMetric.HAS_FAVORITE -> if (entries.any { it.isFavorite }) 1 else 0
            AchievementMetric.HAS_TIME_CAPSULE -> if (timeCapsuleCount > 0) 1 else 0
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
        timeCapsuleCount: Int
    ): Set<String> {
        val alreadyUnlocked = AchievementPrefs.getUnlockedIds(context)
        val newly = mutableSetOf<String>()
        AchievementDefinitions.groups.forEach { group ->
            val value = currentValue(group.metric, entries, goalCompletions, timeCapsuleCount)
            group.tiers.forEach { tier ->
                if (tier.id !in alreadyUnlocked && value >= tier.target) newly.add(tier.id)
            }
        }
        return newly
    }
}