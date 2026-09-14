package com.example.rejournal.data

enum class GoalMetric { PHOTOS, AUDIO_MEMOS, ENTRIES_LOGGED, STREAK, ACTIVITY_TAG, HIGH_ENERGY_DAYS, LOW_STRESS_DAYS, GOOD_SLEEP_DAYS }

data class GoalDefinition(
    val id: String,
    val title: String,
    val description: String,
    val category: GoalCategory,
    val metric: GoalMetric,
    val target: Int,
    val activityTag: String? = null
)

object GoalDefinitions {
    val all = listOf(
        // Fitness
        GoalDefinition("fitness_exercise_5", "Exercise 5 Times", "Tag \"Exercise\" on 5 days", GoalCategory.FITNESS, GoalMetric.ACTIVITY_TAG, 5, activityTag = "Exercise"),
        GoalDefinition("fitness_outdoors_5", "Get Outdoors 5 Times", "Tag \"Outdoors\" on 5 days", GoalCategory.FITNESS, GoalMetric.ACTIVITY_TAG, 5, activityTag = "Outdoors"),
        GoalDefinition("fitness_energy_5", "5 High-Energy Days", "Log energy 4 or higher on 5 days", GoalCategory.FITNESS, GoalMetric.HIGH_ENERGY_DAYS, 5),

        // Habits
        GoalDefinition("habits_entries_10", "Log 10 Entries", "Log 10 new days", GoalCategory.HABITS, GoalMetric.ENTRIES_LOGGED, 10),
        GoalDefinition("habits_streak_7", "7-Day Streak", "Reach a 7 day logging streak", GoalCategory.HABITS, GoalMetric.STREAK, 7),
        GoalDefinition("habits_streak_14", "14-Day Streak", "Reach a 14 day logging streak", GoalCategory.HABITS, GoalMetric.STREAK, 14),

        // Healthier Lifestyle
        GoalDefinition("healthy_eating_5", "Eat Well 5 Times", "Tag \"Eating well\" on 5 days", GoalCategory.HEALTHY_LIFESTYLE, GoalMetric.ACTIVITY_TAG, 5, activityTag = "Eating well"),
        GoalDefinition("healthy_sleep_5", "5 Good Sleep Nights", "Log sleep 4 or higher on 5 days", GoalCategory.HEALTHY_LIFESTYLE, GoalMetric.GOOD_SLEEP_DAYS, 5),
        GoalDefinition("healthy_lowstress_5", "5 Low-Stress Days", "Log stress 2 or lower on 5 days", GoalCategory.HEALTHY_LIFESTYLE, GoalMetric.LOW_STRESS_DAYS, 5),

        // Growth
        GoalDefinition("growth_reading_5", "Read 5 Times", "Tag \"Reading\" on 5 days", GoalCategory.GROWTH, GoalMetric.ACTIVITY_TAG, 5, activityTag = "Reading"),
        GoalDefinition("growth_photos_5", "Snap 5 Photos", "Add 5 photos to your entries", GoalCategory.GROWTH, GoalMetric.PHOTOS, 5),
        GoalDefinition("growth_audio_5", "Record 5 Voice Memos", "Add 5 voice memos to your entries", GoalCategory.GROWTH, GoalMetric.AUDIO_MEMOS, 5),

        // Break Bad Habits
        GoalDefinition("break_streak_30", "30-Day Streak", "Reach a 30 day logging streak", GoalCategory.BREAK_BAD_HABITS, GoalMetric.STREAK, 30),
        GoalDefinition("break_lowstress_7", "7 Low-Stress Days", "Log stress 2 or lower on 7 days", GoalCategory.BREAK_BAD_HABITS, GoalMetric.LOW_STRESS_DAYS, 7),
        GoalDefinition("break_relax_5", "Relax 5 Times", "Tag \"Relaxing\" on 5 days", GoalCategory.BREAK_BAD_HABITS, GoalMetric.ACTIVITY_TAG, 5, activityTag = "Relaxing")
    )

    fun byId(id: String): GoalDefinition? = all.find { it.id == id }
    fun byCategory(category: GoalCategory): List<GoalDefinition> = all.filter { it.category == category }
}