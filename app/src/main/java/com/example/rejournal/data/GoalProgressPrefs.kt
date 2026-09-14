package com.example.rejournal.data

import android.content.Context
import java.time.LocalDate

data class GoalState(
    val isActive: Boolean,
    val startDate: LocalDate?,
    val completions: Int,
    val attempts: Int
)

object GoalProgressPrefs {
    private const val PREFS_NAME = "goal_progress_prefs"
    const val MAX_ACTIVE_GOALS = 3

    private fun keyActive(id: String) = "${id}_active"
    private fun keyStart(id: String) = "${id}_start"
    private fun keyCompletions(id: String) = "${id}_completions"
    private fun keyAttempts(id: String) = "${id}_attempts"

    fun getState(context: Context, id: String): GoalState {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val isActive = prefs.getBoolean(keyActive(id), false)
        val startEpoch = prefs.getLong(keyStart(id), -1L)
        val startDate = if (startEpoch >= 0) LocalDate.ofEpochDay(startEpoch) else null
        return GoalState(
            isActive = isActive,
            startDate = startDate,
            completions = prefs.getInt(keyCompletions(id), 0),
            attempts = prefs.getInt(keyAttempts(id), 0)
        )
    }

    fun activeGoals(context: Context): List<GoalDefinition> =
        GoalDefinitions.all.filter { getState(context, it.id).isActive }

    fun activeCount(context: Context): Int = activeGoals(context).size

    fun canStartNewGoal(context: Context): Boolean = activeCount(context) < MAX_ACTIVE_GOALS

    fun startGoal(context: Context, id: String) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
            .putBoolean(keyActive(id), true)
            .putLong(keyStart(id), LocalDate.now().toEpochDay())
            .apply()
    }

    fun cancelGoal(context: Context, id: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val attempts = prefs.getInt(keyAttempts(id), 0)
        prefs.edit()
            .putBoolean(keyActive(id), false)
            .remove(keyStart(id))
            .putInt(keyAttempts(id), attempts + 1)
            .apply()
    }

    fun completeGoal(context: Context, id: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val completions = prefs.getInt(keyCompletions(id), 0)
        val attempts = prefs.getInt(keyAttempts(id), 0)
        prefs.edit()
            .putBoolean(keyActive(id), false)
            .remove(keyStart(id))
            .putInt(keyCompletions(id), completions + 1)
            .putInt(keyAttempts(id), attempts + 1)
            .apply()
    }

    fun totalCompletions(context: Context): Int =
        GoalDefinitions.all.sumOf { getState(context, it.id).completions }
}