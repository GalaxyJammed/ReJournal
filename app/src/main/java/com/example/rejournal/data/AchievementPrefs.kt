package com.example.rejournal.data

import android.content.Context

object AchievementPrefs {
    private const val PREFS_NAME = "achievement_prefs"
    private const val KEY_UNLOCKED = "unlocked_ids"

    fun getUnlockedIds(context: Context): Set<String> {
        val stored = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_UNLOCKED, "") ?: ""
        return if (stored.isBlank()) emptySet() else stored.split(",").toSet()
    }

    fun markUnlocked(context: Context, ids: Set<String>) {
        if (ids.isEmpty()) return
        val current = getUnlockedIds(context)
        val updated = current + ids
        if (updated != current) {
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
                .putString(KEY_UNLOCKED, updated.joinToString(","))
                .apply()
        }
    }
}