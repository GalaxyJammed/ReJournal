package com.example.rejournal.data

enum class AchievementMetric {
    TOTAL_ENTRIES, BEST_STREAK, GOALS_COMPLETED,
    HAS_PHOTO, HAS_AUDIO, HAS_FAVORITE, HAS_TIME_CAPSULE,
    WELLNESS_DAYS, UNIQUE_TAGS, THOROUGH_LOGS, TESTS_TAKEN,
    MICRO_WINS_LOGGED, MIXTAPES_EARNED
}

data class AchievementTier(val id: String, val target: Int, val title: String, val description: String)
data class AchievementGroup(val groupId: String, val metric: AchievementMetric, val tiers: List<AchievementTier>)

object AchievementDefinitions {
    val groups = listOf(
        AchievementGroup("entries", AchievementMetric.TOTAL_ENTRIES, listOf(
            AchievementTier("entries_1", 1, "First Step", "Log your very first day"),
            AchievementTier("entries_7", 7, "One Week In", "Log 7 days total"),
            AchievementTier("entries_30", 30, "Habit Forming", "Log 30 days total"),
            AchievementTier("entries_100", 100, "Centurion", "Log 100 days total"),
            AchievementTier("entries_365", 365, "Yearly Review", "Log 365 days total")
        )),
        AchievementGroup("streak", AchievementMetric.BEST_STREAK, listOf(
            AchievementTier("streak_3", 3, "Getting Consistent", "Reach a 3-day streak"),
            AchievementTier("streak_6", 6, "Building Momentum", "Reach a 6-day streak"),
            AchievementTier("streak_10", 10, "Double Digits", "Reach a 10-day streak"),
            AchievementTier("streak_30", 30, "Unstoppable", "Reach a 30-day streak"),
            AchievementTier("streak_100", 100, "Centennial Streak", "Reach a 100-day streak")
        )),
        AchievementGroup("goals", AchievementMetric.GOALS_COMPLETED, listOf(
            AchievementTier("goals_1", 1, "Goal Getter", "Complete your first goal"),
            AchievementTier("goals_3", 3, "On a Roll", "Complete 3 goals"),
            AchievementTier("goals_10", 10, "Overachiever", "Complete 10 goals"),
            AchievementTier("goals_25", 25, "Goal Master", "Complete 25 goals"),
            AchievementTier("goals_50", 50, "Limitless", "Complete 50 goals")
        )),
        AchievementGroup("microwins", AchievementMetric.MICRO_WINS_LOGGED, listOf(
            AchievementTier("microwins_1", 1, "Small Victory", "Log your first micro-win"),
            AchievementTier("microwins_5", 5, "Building Joy", "Log 5 micro-wins"),
            AchievementTier("microwins_15", 15, "Positivity Collector", "Log 15 micro-wins"),
            AchievementTier("microwins_30", 30, "Monthly Wins", "Log 30 micro-wins"),
            AchievementTier("microwins_100", 100, "Trophy Room", "Log 100 micro-wins total")
        )),
        AchievementGroup("mixtapes", AchievementMetric.MIXTAPES_EARNED, listOf(
            AchievementTier("mixtapes_1", 1, "First Track", "Earn your first weekly emotional mixtape"),
            AchievementTier("mixtapes_3", 3, "Mini EP", "Earn 3 weekly mixtapes"),
            AchievementTier("mixtapes_5", 5, "Tape Collector", "Earn 5 weekly mixtapes"),
            AchievementTier("mixtapes_12", 12, "Album Producer", "Earn 12 weekly mixtapes"),
            AchievementTier("mixtapes_52", 52, "Yearly Library", "Earn 52 weekly mixtapes across your bookshelf")
        )),
        AchievementGroup("photo", AchievementMetric.HAS_PHOTO, listOf(
            AchievementTier("photo_1", 1, "Picture This", "Add your first photo to an entry"),
            AchievementTier("photo_10", 10, "Visual Diary", "Add photos to 10 entries"),
            AchievementTier("photo_50", 50, "Gallery Curator", "Add photos to 50 entries")
        )),
        AchievementGroup("audio", AchievementMetric.HAS_AUDIO, listOf(
            AchievementTier("audio_1", 1, "Say It Out Loud", "Record your first voice memo"),
            AchievementTier("audio_10", 10, "Voice of Reason", "Record 10 voice memos"),
            AchievementTier("audio_50", 50, "Oral History", "Record 50 voice memos")
        )),
        AchievementGroup("favorite", AchievementMetric.HAS_FAVORITE, listOf(
            AchievementTier("favorite_1", 1, "A Day to Remember", "Mark your first favorite day"),
            AchievementTier("favorite_10", 10, "Top Ten", "Mark 10 favorite days"),
            AchievementTier("favorite_50", 50, "Treasured Memories", "Mark 50 favorite days")
        )),
        AchievementGroup("capsule", AchievementMetric.HAS_TIME_CAPSULE, listOf(
            AchievementTier("capsule_1", 1, "Dear Future Me", "Create your first time capsule"),
            AchievementTier("capsule_5", 5, "Time Traveler", "Create 5 time capsules"),
            AchievementTier("capsule_20", 20, "Legacy Builder", "Create 20 time capsules")
        )),
        AchievementGroup("wellness", AchievementMetric.WELLNESS_DAYS, listOf(
            AchievementTier("wellness_1", 1, "Balanced Day", "Log a day with High Energy (4+), Low Stress (2-), and Good Sleep (4+)"),
            AchievementTier("wellness_7", 7, "Wellness Week", "7 days of high energy, low stress, and good sleep"),
            AchievementTier("wellness_30", 30, "Zen Master", "30 days of high energy, low stress, and good sleep")
        )),
        AchievementGroup("tags", AchievementMetric.UNIQUE_TAGS, listOf(
            AchievementTier("tags_5", 5, "Variety", "Use 5 different activity tags across your logs"),
            AchievementTier("tags_20", 20, "Explorer", "Use 20 different activity tags across your logs"),
            AchievementTier("tags_50", 50, "Full Spectrum", "Use 50 different activity tags across your logs")
        )),
        AchievementGroup("thorough", AchievementMetric.THOROUGH_LOGS, listOf(
            AchievementTier("thorough_3", 3, "Deep Diver", "Log 3 entries with notes longer than 200 characters"),
            AchievementTier("thorough_10", 10, "Expressive", "Log 10 entries with notes longer than 200 characters"),
            AchievementTier("thorough_50", 50, "Novelist", "Log 50 entries with notes longer than 200 characters")
        )),
        AchievementGroup("tests", AchievementMetric.TESTS_TAKEN, listOf(
            AchievementTier("tests_1", 1, "Know Thyself", "Complete your first personality test"),
            AchievementTier("tests_3", 3, "Introspective", "Complete 3 personality tests"),
            AchievementTier("tests_6", 6, "Self-Awareness Guru", "Complete all available personality tests")
        ))
    )

    val allTiers: List<AchievementTier> = groups.flatMap { it.tiers }
}
