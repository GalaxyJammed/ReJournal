package com.example.rejournal.data

import android.content.Context
import java.time.YearMonth

object CalendarSyncPrefs {
    private const val PREFS_NAME = "calendar_sync_prefs"
    private const val KEY_SELECTED_IDS = "selected_ids"
    private const val KEY_LAST_SYNCED_MONTH = "last_synced_month"

    fun getSelectedCalendarIds(context: Context): Set<Long> {
        val stored = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_SELECTED_IDS, "") ?: ""
        return if (stored.isBlank()) emptySet() else stored.split(",").mapNotNull { it.toLongOrNull() }.toSet()
    }

    fun setSelectedCalendarIds(context: Context, ids: Set<Long>) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
            .putString(KEY_SELECTED_IDS, ids.joinToString(","))
            .apply()
    }

    fun getLastSyncedMonth(context: Context): String? =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getString(KEY_LAST_SYNCED_MONTH, null)

    fun setLastSyncedMonth(context: Context, month: YearMonth) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
            .putString(KEY_LAST_SYNCED_MONTH, month.toString())
            .apply()
    }
}