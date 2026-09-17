package com.example.rejournal.data

enum class AchievementMetric {
    TOTAL_ENTRIES, BEST_STREAK, GOALS_COMPLETED,
    HAS_PHOTO, HAS_AUDIO, HAS_FAVORITE, HAS_TIME_CAPSULE
}

data class AchievementTier(val id: String, val target: Int, val title: String, val description: String)
data class AchievementGroup(val groupId: String, val metric: AchievementMetric, val tiers: List<AchievementTier>)

object AchievementDefinitions {
    val groups = listOf(
        AchievementGroup("entries", AchievementMetric.TOTAL_ENTRIES, listOf(
            AchievementTier("entries_1", 1, "First Step", "Log your very first day"),
            AchievementTier("entries_7", 7, "One Week In", "Log 7 days total"),
            AchievementTier("entries_30", 30, "Habit Forming", "Log 30 days total"),
            AchievementTier("entries_100", 100, "Centurion", "Log 100 days total")
        )),
        AchievementGroup("streak", AchievementMetric.BEST_STREAK, listOf(
            AchievementTier("streak_3", 3, "Getting Consistent", "Reach a 3-day streak"),
            AchievementTier("streak_6", 6, "Building Momentum", "Reach a 6-day streak"),
            AchievementTier("streak_10", 10, "Double Digits", "Reach a 10-day streak"),
            AchievementTier("streak_30", 30, "Unstoppable", "Reach a 30-day streak")
        )),
        AchievementGroup("goals", AchievementMetric.GOALS_COMPLETED, listOf(
            AchievementTier("goals_1", 1, "Goal Getter", "Complete your first goal"),
            AchievementTier("goals_3", 3, "On a Roll", "Complete 3 goals"),
            AchievementTier("goals_10", 10, "Overachiever", "Complete 10 goals")
        )),
        AchievementGroup("photo", AchievementMetric.HAS_PHOTO, listOf(
            AchievementTier("photo_1", 1, "Picture This", "Add your first photo to an entry")
        )),
        AchievementGroup("audio", AchievementMetric.HAS_AUDIO, listOf(
            AchievementTier("audio_1", 1, "Say It Out Loud", "Record your first voice memo")
        )),
        AchievementGroup("favorite", AchievementMetric.HAS_FAVORITE, listOf(
            AchievementTier("favorite_1", 1, "A Day to Remember", "Mark your first favorite day")
        )),
        AchievementGroup("capsule", AchievementMetric.HAS_TIME_CAPSULE, listOf(
            AchievementTier("capsule_1", 1, "Dear Future Me", "Create your first time capsule")
        ))
    )

    val allTiers: List<AchievementTier> = groups.flatMap { it.tiers }
}