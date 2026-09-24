package com.example.rejournal.notifications

import android.content.Context
import androidx.core.content.edit

object MicroWinNotificationPrefs {
    private const val PREFS_NAME = "micro_win_notification_prefs"
    private const val KEY_ENABLED = "enabled"
    private const val KEY_LAST_DATE = "last_date"
    private const val KEY_SCHEDULED_TRIGGER = "scheduled_trigger"

    fun isEnabled(context: Context): Boolean =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getBoolean(KEY_ENABLED, true)

    fun setEnabled(context: Context, enabled: Boolean) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit {
            putBoolean(KEY_ENABLED, enabled)
        }
    }

    fun getLastNotificationDate(context: Context): String =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getString(KEY_LAST_DATE, "") ?: ""

    fun setLastNotificationDate(context: Context, dateString: String) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit {
            putString(KEY_LAST_DATE, dateString)
        }
    }

    fun getScheduledTriggerTime(context: Context): Long =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getLong(KEY_SCHEDULED_TRIGGER, 0L)

    fun setScheduledTriggerTime(context: Context, timeMillis: Long) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit {
            putLong(KEY_SCHEDULED_TRIGGER, timeMillis)
        }
    }
}
