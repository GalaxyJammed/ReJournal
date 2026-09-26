package com.example.rejournal.data

import android.content.Context
import androidx.core.content.edit

object HealthConnectSyncPrefs {
    private const val PREFS_NAME = "health_connect_sync_prefs"
    private const val KEY_ENABLED = "health_sync_enabled"
    private const val KEY_SYNC_STEPS = "sync_steps"
    private const val KEY_SYNC_WORKOUTS = "sync_workouts"
    private const val KEY_SYNC_SLEEP = "sync_sleep"
    private const val KEY_SYNC_CALORIES = "sync_calories"
    private const val KEY_STEP_GOAL = "step_goal"
    private const val KEY_SLEEP_GOAL_HOURS = "sleep_goal_hours"
    private const val KEY_CALORIE_GOAL = "calorie_goal"
    private const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
    private const val KEY_LAST_SYNCED_TIME = "last_synced_time"
    private const val KEY_SYNCED_RECORD_IDS = "synced_record_ids"
    private const val KEY_SYNCED_STEP_MILESTONES = "synced_step_milestones"
    private const val KEY_SYNCED_CALORIE_MILESTONES = "synced_calorie_milestones"

    fun isEnabled(context: Context): Boolean =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getBoolean(KEY_ENABLED, false)

    fun setEnabled(context: Context, enabled: Boolean) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit {
            putBoolean(KEY_ENABLED, enabled)
        }
    }

    fun isStepsSyncEnabled(context: Context): Boolean =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getBoolean(KEY_SYNC_STEPS, true)

    fun setStepsSyncEnabled(context: Context, enabled: Boolean) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit {
            putBoolean(KEY_SYNC_STEPS, enabled)
        }
    }

    fun isWorkoutsSyncEnabled(context: Context): Boolean =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getBoolean(KEY_SYNC_WORKOUTS, true)

    fun setWorkoutsSyncEnabled(context: Context, enabled: Boolean) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit {
            putBoolean(KEY_SYNC_WORKOUTS, enabled)
        }
    }

    fun isSleepSyncEnabled(context: Context): Boolean =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getBoolean(KEY_SYNC_SLEEP, true)

    fun setSleepSyncEnabled(context: Context, enabled: Boolean) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit {
            putBoolean(KEY_SYNC_SLEEP, enabled)
        }
    }

    fun isCaloriesSyncEnabled(context: Context): Boolean =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getBoolean(KEY_SYNC_CALORIES, true)

    fun setCaloriesSyncEnabled(context: Context, enabled: Boolean) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit {
            putBoolean(KEY_SYNC_CALORIES, enabled)
        }
    }

    fun getStepGoal(context: Context): Int =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getInt(KEY_STEP_GOAL, 10000)

    fun setStepGoal(context: Context, goal: Int) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit {
            putInt(KEY_STEP_GOAL, goal)
        }
    }

    fun getSleepGoalHours(context: Context): Int =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getInt(KEY_SLEEP_GOAL_HOURS, 8)

    fun setSleepGoalHours(context: Context, hours: Int) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit {
            putInt(KEY_SLEEP_GOAL_HOURS, hours)
        }
    }

    fun getCalorieGoal(context: Context): Int =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getInt(KEY_CALORIE_GOAL, 500)

    fun setCalorieGoal(context: Context, goal: Int) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit {
            putInt(KEY_CALORIE_GOAL, goal)
        }
    }

    fun isNotificationsEnabled(context: Context): Boolean =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getBoolean(KEY_NOTIFICATIONS_ENABLED, true)

    fun setNotificationsEnabled(context: Context, enabled: Boolean) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit {
            putBoolean(KEY_NOTIFICATIONS_ENABLED, enabled)
        }
    }

    fun getLastSyncedTime(context: Context): Long =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getLong(KEY_LAST_SYNCED_TIME, 0L)

    fun setLastSyncedTime(context: Context, timeMillis: Long) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit {
            putLong(KEY_LAST_SYNCED_TIME, timeMillis)
        }
    }

    fun getSyncedRecordIds(context: Context): Set<String> =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getStringSet(KEY_SYNCED_RECORD_IDS, emptySet()) ?: emptySet()

    fun addSyncedRecordId(context: Context, recordId: String) {
        val current = getSyncedRecordIds(context).toMutableSet()
        current.add(recordId)
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit {
            putStringSet(KEY_SYNCED_RECORD_IDS, current)
        }
    }

    fun getSyncedStepMilestones(context: Context): Set<String> =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getStringSet(KEY_SYNCED_STEP_MILESTONES, emptySet()) ?: emptySet()

    fun addSyncedStepMilestone(context: Context, milestoneKey: String) {
        val current = getSyncedStepMilestones(context).toMutableSet()
        current.add(milestoneKey)
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit {
            putStringSet(KEY_SYNCED_STEP_MILESTONES, current)
        }
    }

    fun getSyncedCalorieMilestones(context: Context): Set<String> =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getStringSet(KEY_SYNCED_CALORIE_MILESTONES, emptySet()) ?: emptySet()

    fun addSyncedCalorieMilestone(context: Context, milestoneKey: String) {
        val current = getSyncedCalorieMilestones(context).toMutableSet()
        current.add(milestoneKey)
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit {
            putStringSet(KEY_SYNCED_CALORIE_MILESTONES, current)
        }
    }
}
