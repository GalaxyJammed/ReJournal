package com.example.rejournal.ui

import java.time.LocalDate

sealed class Screen(val route: String) {
    object Log : Screen("log")
    object Trend : Screen("trend")
    object Stats : Screen("stats")
    object Search : Screen("search")
    object Extras : Screen("extras")
    object Goals : Screen("goals")
    object GoalDetail : Screen("goalDetail/{goalId}") {
        fun createRoute(goalId: String) = "goalDetail/$goalId"
    }
    object Settings : Screen("settings")
    object Questionnaire : Screen("questionnaire/{date}") {
        fun createRoute(date: LocalDate): String = "questionnaire/$date"
    }
}