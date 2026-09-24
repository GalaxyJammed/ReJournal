package com.example.rejournal.data

import android.content.Context
import java.time.LocalDate

object GoalProgressCalculator {

    fun currentProgress(
        definition: GoalDefinition,
        state: GoalState,
        entries: List<MoodEntry>,
        microWins: List<MicroWin> = emptyList()
    ): Int {
        val startDate = state.startDate ?: return 0
        val sinceStart = entries.filter { !it.date.isBefore(startDate) }
        return when (definition.metric) {
            GoalMetric.PHOTOS -> sinceStart.sumOf { it.photoPaths.size }
            GoalMetric.AUDIO_MEMOS -> sinceStart.sumOf { it.audioPaths.size }
            GoalMetric.ENTRIES_LOGGED -> sinceStart.map { it.date }.distinct().size
            GoalMetric.STREAK -> {
                val streakInfo = StreakCalculator.calculate(sinceStart)
                maxOf(streakInfo.currentStreak, streakInfo.longestStreak)
            }
            GoalMetric.ACTIVITY_TAG -> {
                val tag = definition.activityTag
                if (tag == null) 0 else sinceStart.filter { tag in it.activities }.map { it.date }.distinct().size
            }
            GoalMetric.HIGH_ENERGY_DAYS -> sinceStart.filter { it.energy >= 4 }.map { it.date }.distinct().size
            GoalMetric.LOW_STRESS_DAYS -> sinceStart.filter { it.stress <= 2 }.map { it.date }.distinct().size
            GoalMetric.GOOD_SLEEP_DAYS -> sinceStart.filter { it.sleep >= 4 }.map { it.date }.distinct().size
            GoalMetric.MICRO_WINS -> microWins.count { !it.date.isBefore(startDate) }
            GoalMetric.EARN_MIXTAPES -> {
                val years = MixtapeCalculator.getAvailableYears(sinceStart)
                years.sumOf { yr -> MixtapeCalculator.calculateForYear(sinceStart, yr).count { it.hasEntries } }
            }
            GoalMetric.THOROUGH_LOGS -> sinceStart.count { it.note.length > 200 }
        }
    }

    fun checkAndCompleteActiveGoals(
        context: Context,
        entries: List<MoodEntry>,
        microWins: List<MicroWin> = emptyList()
    ) {
        GoalDefinitions.all.forEach { definition ->
            val state = GoalProgressPrefs.getState(context, definition.id)
            if (state.isActive && currentProgress(definition, state, entries, microWins) >= definition.target) {
                GoalProgressPrefs.completeGoal(context, definition.id)
            }
        }
    }
}
