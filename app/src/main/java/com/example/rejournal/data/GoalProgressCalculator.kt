package com.example.rejournal.data

import android.content.Context

object GoalProgressCalculator {

    fun currentProgress(definition: GoalDefinition, state: GoalState, entries: List<MoodEntry>): Int {
        val startDate = state.startDate ?: return 0
        val sinceStart = entries.filter { !it.date.isBefore(startDate) }
        return when (definition.metric) {
            GoalMetric.PHOTOS -> sinceStart.sumOf { it.photoPaths.size }
            GoalMetric.AUDIO_MEMOS -> sinceStart.sumOf { it.audioPaths.size }
            GoalMetric.ENTRIES_LOGGED -> sinceStart.size
            GoalMetric.STREAK -> StreakCalculator.calculate(entries).currentStreak
            GoalMetric.ACTIVITY_TAG -> sinceStart.count { definition.activityTag in it.activities }
            GoalMetric.HIGH_ENERGY_DAYS -> sinceStart.count { it.energy >= 4 }
            GoalMetric.LOW_STRESS_DAYS -> sinceStart.count { it.stress <= 2 }
            GoalMetric.GOOD_SLEEP_DAYS -> sinceStart.count { it.sleep >= 4 }
        }
    }

    fun checkAndCompleteActiveGoals(context: Context, entries: List<MoodEntry>) {
        GoalDefinitions.all.forEach { definition ->
            val state = GoalProgressPrefs.getState(context, definition.id)
            if (state.isActive && currentProgress(definition, state, entries) >= definition.target) {
                GoalProgressPrefs.completeGoal(context, definition.id)
            }
        }
    }
}