package com.example.rejournal.data

import android.content.Context

object ActivityTagsPrefs {
    private const val PREFS_NAME = "activity_tags_prefs"
    private const val KEY_CUSTOM_TAGS = "custom_tags"
    private const val KEY_REMOVED_DEFAULT_TAGS = "removed_default_tags"

    val defaultTags = listOf(
        "Exercise", "Family", "Friends", "Work",
        "Reading", "Outdoors", "Relaxing", "Eating well", "Chores"
    )

    private fun getCustomTags(context: Context): List<String> {
        val stored = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_CUSTOM_TAGS, "") ?: ""
        return if (stored.isBlank()) emptyList() else stored.split(",")
    }

    private fun getRemovedDefaultTags(context: Context): Set<String> {
        val stored = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_REMOVED_DEFAULT_TAGS, "") ?: ""
        return if (stored.isBlank()) emptySet() else stored.split(",").toSet()
    }

    private fun saveCustomTags(context: Context, tags: List<String>) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
            .putString(KEY_CUSTOM_TAGS, tags.joinToString(","))
            .apply()
    }

    private fun saveRemovedDefaultTags(context: Context, tags: Set<String>) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
            .putString(KEY_REMOVED_DEFAULT_TAGS, tags.joinToString(","))
            .apply()
    }

    fun addCustomTag(context: Context, tag: String) {
        val trimmed = tag.trim()
        if (trimmed.isEmpty()) return
        val current = getCustomTags(context)
        val alreadyExists = current.any { it.equals(trimmed, ignoreCase = true) } ||
                defaultTags.any { it.equals(trimmed, ignoreCase = true) }
        if (alreadyExists) return

        saveCustomTags(context, current + trimmed)
    }

    // Removing a tag only affects which options show up going forward — it does
    // NOT touch any past MoodEntry rows, since those already store their own
    // list of activity strings independent of this available-options list.
    fun removeTag(context: Context, tag: String) {
        val customTags = getCustomTags(context)
        if (customTags.any { it.equals(tag, ignoreCase = true) }) {
            saveCustomTags(context, customTags.filterNot { it.equals(tag, ignoreCase = true) })
        } else if (defaultTags.any { it.equals(tag, ignoreCase = true) }) {
            saveRemovedDefaultTags(context, getRemovedDefaultTags(context) + tag)
        }
    }

    fun getAllTags(context: Context): List<String> {
        val removedDefaults = getRemovedDefaultTags(context)
        val visibleDefaults = defaultTags.filterNot { it in removedDefaults }
        return visibleDefaults + getCustomTags(context)
    }
}