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
        GoalDefinition("fitness_walk_7", "Daily Walk Week", "Tag \"Walking\" on 7 days", GoalCategory.FITNESS, GoalMetric.ACTIVITY_TAG, 7, activityTag = "Walking"),
        GoalDefinition("fitness_cardio_3", "Heart Pumping", "Tag \"Cardio\" on 3 days", GoalCategory.FITNESS, GoalMetric.ACTIVITY_TAG, 3, activityTag = "Cardio"),

        // Habits
        GoalDefinition("habits_entries_10", "Log 10 Entries", "Log 10 new days", GoalCategory.HABITS, GoalMetric.ENTRIES_LOGGED, 10),
        GoalDefinition("habits_streak_7", "7-Day Streak", "Reach a 7 day logging streak", GoalCategory.HABITS, GoalMetric.STREAK, 7),
        GoalDefinition("habits_streak_14", "14-Day Streak", "Reach a 14 day logging streak", GoalCategory.HABITS, GoalMetric.STREAK, 14),
        GoalDefinition("habits_water_5", "Stay Hydrated", "Tag \"Drinking water\" on 5 days", GoalCategory.HABITS, GoalMetric.ACTIVITY_TAG, 5, activityTag = "Drinking water"),
        GoalDefinition("habits_meditate_5", "Mindful Moments", "Tag \"Meditation\" on 5 days", GoalCategory.HABITS, GoalMetric.ACTIVITY_TAG, 5, activityTag = "Meditation"),

        // Healthier Lifestyle
        GoalDefinition("healthy_eating_5", "Eat Well 5 Times", "Tag \"Eating well\" on 5 days", GoalCategory.HEALTHY_LIFESTYLE, GoalMetric.ACTIVITY_TAG, 5, activityTag = "Eating well"),
        GoalDefinition("healthy_sleep_5", "5 Good Sleep Nights", "Log sleep 4 or higher on 5 days", GoalCategory.HEALTHY_LIFESTYLE, GoalMetric.GOOD_SLEEP_DAYS, 5),
        GoalDefinition("healthy_lowstress_5", "5 Low-Stress Days", "Log stress 2 or lower on 5 days", GoalCategory.HEALTHY_LIFESTYLE, GoalMetric.LOW_STRESS_DAYS, 5),
        GoalDefinition("healthy_fruit_7", "Vibrant Diet", "Tag \"Fruit & Veg\" on 7 days", GoalCategory.HEALTHY_LIFESTYLE, GoalMetric.ACTIVITY_TAG, 7, activityTag = "Fruit & Veg"),
        GoalDefinition("healthy_no_caffeine_3", "Caffeine Free Afternoon", "Tag \"No late caffeine\" on 3 days", GoalCategory.HEALTHY_LIFESTYLE, GoalMetric.ACTIVITY_TAG, 3, activityTag = "No late caffeine"),

        // Growth
        GoalDefinition("growth_reading_5", "Read 5 Times", "Tag \"Reading\" on 5 days", GoalCategory.GROWTH, GoalMetric.ACTIVITY_TAG, 5, activityTag = "Reading"),
        GoalDefinition("growth_photos_5", "Snap 5 Photos", "Add 5 photos to your entries", GoalCategory.GROWTH, GoalMetric.PHOTOS, 5),
        GoalDefinition("growth_audio_5", "Record 5 Voice Memos", "Add 5 voice memos to your entries", GoalCategory.GROWTH, GoalMetric.AUDIO_MEMOS, 5),
        GoalDefinition("growth_journal_deep_3", "Deep Reflection", "Log 3 entries with over 200 words", GoalCategory.GROWTH, GoalMetric.ENTRIES_LOGGED, 3),
        GoalDefinition("growth_creative_3", "Express Yourself", "Tag \"Creative\" on 3 days", GoalCategory.GROWTH, GoalMetric.ACTIVITY_TAG, 3, activityTag = "Creative"),

        // Break Bad Habits
        GoalDefinition("break_streak_30", "30-Day Streak", "Reach a 30 day logging streak", GoalCategory.BREAK_BAD_HABITS, GoalMetric.STREAK, 30),
        GoalDefinition("break_lowstress_7", "7 Low-Stress Days", "Log stress 2 or lower on 7 days", GoalCategory.BREAK_BAD_HABITS, GoalMetric.LOW_STRESS_DAYS, 7),
        GoalDefinition("break_relax_5", "Relax 5 Times", "Tag \"Relaxing\" on 5 days", GoalCategory.BREAK_BAD_HABITS, GoalMetric.ACTIVITY_TAG, 5, activityTag = "Relaxing"),
        GoalDefinition("break_screens_5", "Digital Detox", "Tag \"Less screen time\" on 5 days", GoalCategory.BREAK_BAD_HABITS, GoalMetric.ACTIVITY_TAG, 5, activityTag = "Less screen time"),
        GoalDefinition("break_early_bed_5", "Early to Bed", "Tag \"Early sleep\" on 5 days", GoalCategory.BREAK_BAD_HABITS, GoalMetric.ACTIVITY_TAG, 5, activityTag = "Early sleep")
    )

    fun byId(id: String): GoalDefinition? = all.find { it.id == id }
    fun byCategory(category: GoalCategory): List<GoalDefinition> = all.filter { it.category == category }
}