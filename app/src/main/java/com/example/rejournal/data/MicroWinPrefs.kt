package com.example.rejournal.data

import android.content.Context

object MicroWinPrefs {
    private const val PREFS_NAME = "micro_win_prefs"
    private const val KEY_CUSTOM_ADDITIONS = "custom_additions"
    private const val KEY_REMOVED_DEFAULTS = "removed_defaults"

    val defaultSuggestions = listOf(
        "🛏️ Made my bed",
        "🏋️ Worked out",
        "💧 Drank water",
        "📖 Read 10 pages",
        "🥗 Healthy meal",
        "🚶 Took a walk",
        "🧹 Cleaned room",
        "🧘 Meditated"
    )

    fun getQuickAdditions(context: Context): List<String> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val removed = getRemovedDefaults(context)
        val visibleDefaults = defaultSuggestions.filterNot { it in removed }

        val customStored = prefs.getString(KEY_CUSTOM_ADDITIONS, "") ?: ""
        val customList = if (customStored.isBlank()) emptyList() else customStored.split("|||")

        return visibleDefaults + customList
    }

    fun addQuickAddition(context: Context, addition: String) {
        val trimmed = addition.trim()
        if (trimmed.isEmpty()) return
        val current = getQuickAdditions(context)
        if (current.any { it.equals(trimmed, ignoreCase = true) }) return

        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val customStored = prefs.getString(KEY_CUSTOM_ADDITIONS, "") ?: ""
        val customList = if (customStored.isBlank()) mutableListOf() else customStored.split("|||").toMutableList()
        customList.add(trimmed)

        prefs.edit().putString(KEY_CUSTOM_ADDITIONS, customList.joinToString("|||")).apply()
    }

    fun removeQuickAddition(context: Context, addition: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val customStored = prefs.getString(KEY_CUSTOM_ADDITIONS, "") ?: ""
        val customList = if (customStored.isBlank()) emptyList() else customStored.split("|||")

        if (customList.contains(addition)) {
            val updated = customList.filterNot { it == addition }
            prefs.edit().putString(KEY_CUSTOM_ADDITIONS, updated.joinToString("|||")).apply()
        } else if (defaultSuggestions.contains(addition)) {
            val removed = getRemovedDefaults(context).toMutableSet()
            removed.add(addition)
            prefs.edit().putString(KEY_REMOVED_DEFAULTS, removed.joinToString("|||")).apply()
        }
    }

    private fun getRemovedDefaults(context: Context): Set<String> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val stored = prefs.getString(KEY_REMOVED_DEFAULTS, "") ?: ""
        return if (stored.isBlank()) emptySet() else stored.split("|||").toSet()
    }
}
