package com.example.rejournal.data

enum class GoalMetric { PHOTOS, AUDIO_MEMOS, ENTRIES_LOGGED, STREAK, ACTIVITY_TAG }

data class GoalDefinition(
    val id: String,
    val title: String,
    val description: String,
    val metric: GoalMetric,
    val target: Int,
    val activityTag: String? = null
)

object GoalDefinitions {
    val all = listOf(
        GoalDefinition("photos_5", "Snap 5 Photos", "Add 5 photos to your entries", GoalMetric.PHOTOS, 5),
        GoalDefinition("audio_5", "Record 5 Voice Memos", "Add 5 voice memos to your entries", GoalMetric.AUDIO_MEMOS, 5),
        GoalDefinition("entries_10", "Log 10 Entries", "Log 10 new days", GoalMetric.ENTRIES_LOGGED, 10),
        GoalDefinition("streak_7", "7-Day Streak", "Reach a 7 day logging streak", GoalMetric.STREAK, 7),
        GoalDefinition("exercise_5", "Exercise 5 Times", "Tag \"Exercise\" on 5 days", GoalMetric.ACTIVITY_TAG, 5, activityTag = "Exercise")
    )

    fun byId(id: String): GoalDefinition? = all.find { it.id == id }
}