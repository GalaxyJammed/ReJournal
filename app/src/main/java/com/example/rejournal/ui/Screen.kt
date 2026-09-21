package com.example.rejournal.ui

import java.time.LocalDate

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Log : Screen("log")
    object Trend : Screen("trend")
    object Stats : Screen("stats")
    object Search : Screen("search")
    object Extras : Screen("extras")
    object Goals : Screen("goals")
    object GoalCategories : Screen("goalCategories")
    object GoalSuggestions : Screen("goalSuggestions/{category}") {
        fun createRoute(category: String) = "goalSuggestions/$category"
    }
    object GoalDetail : Screen("goalDetail/{goalId}") {
        fun createRoute(goalId: String) = "goalDetail/$goalId"
    }
    object PhotoAlbum : Screen("photoAlbum")
    object PhotoDetail : Screen("photoDetail")
    object VoiceMemoAlbum : Screen("voiceMemoAlbum")
    object MoodDetail : Screen("moodDetail/{moodValue}") {
        fun createRoute(moodValue: Int) = "moodDetail/$moodValue"
    }
    object Settings : Screen("settings")
    object Questionnaire : Screen("questionnaire/{date}") {
        fun createRoute(date: LocalDate): String = "questionnaire/$date"
    }
    object ImportantDays : Screen("importantDays")
    object FavoriteDays : Screen("favoriteDays")

    object TimeCapsules : Screen("timeCapsules")
    object CreateTimeCapsule : Screen("createTimeCapsule")
    object Achievements : Screen("achievements")
    object PositiveMemory : Screen("positiveMemory")
    object Profile : Screen("profile")
    object Sync : Screen("sync")
    object WhatsNew : Screen("whatsNew")
    object Faq : Screen("faq")
    object About : Screen("about")
    object NotificationTroubleshoot : Screen("notificationTroubleshoot")
    object Tests : Screen("tests")
    object MbtiTest : Screen("mbtiTest")
    object NpiTest : Screen("npiTest")
    object DarkTriadTest : Screen("darkTriadTest")
    object BigFiveTest : Screen("bigFiveTest")
}