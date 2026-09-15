package com.example.rejournal.data

import android.content.Context

data class TagWithIcon(val name: String, val iconId: String?)

object ActivityTagsPrefs {
    private const val PREFS_NAME = "activity_tags_prefs"
    private const val KEY_CUSTOM_TAGS = "custom_tags_v2"
    private const val KEY_REMOVED_DEFAULT_TAGS = "removed_default_tags"

    val defaultTags = listOf(
        "Exercise", "Family", "Friends", "Work",
        "Reading", "Outdoors", "Relaxing", "Eating well", "Chores"
    )


    private fun getCustomTagsWithIcons(context: Context): List<TagWithIcon> {
        val stored = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_CUSTOM_TAGS, "") ?: ""
        if (stored.isBlank()) return emptyList()
        return stored.split(",").mapNotNull { entry ->
            val parts = entry.split("||")
            val name = parts.getOrNull(0)?.takeIf { it.isNotBlank() } ?: return@mapNotNull null
            val iconId = parts.getOrNull(1)?.takeIf { it.isNotBlank() }
            TagWithIcon(name, iconId)
        }
    }

    private fun saveCustomTagsWithIcons(context: Context, tags: List<TagWithIcon>) {
        val serialized = tags.joinToString(",") { "${it.name}||${it.iconId ?: ""}" }
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
            .putString(KEY_CUSTOM_TAGS, serialized)
            .apply()
    }

    private fun getRemovedDefaultTags(context: Context): Set<String> {
        val stored = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_REMOVED_DEFAULT_TAGS, "") ?: ""
        return if (stored.isBlank()) emptySet() else stored.split(",").toSet()
    }

    private fun saveRemovedDefaultTags(context: Context, tags: Set<String>) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
            .putString(KEY_REMOVED_DEFAULT_TAGS, tags.joinToString(","))
            .apply()
    }

    fun addCustomTag(context: Context, tag: String, iconId: String? = null) {
        val trimmed = tag.trim()
        if (trimmed.isEmpty()) return
        val current = getCustomTagsWithIcons(context)
        val alreadyExists = current.any { it.name.equals(trimmed, ignoreCase = true) } ||
                defaultTags.any { it.equals(trimmed, ignoreCase = true) }
        if (alreadyExists) return

        saveCustomTagsWithIcons(context, current + TagWithIcon(trimmed, iconId))
    }

    fun removeTag(context: Context, tag: String) {
        val customTags = getCustomTagsWithIcons(context)
        if (customTags.any { it.name.equals(tag, ignoreCase = true) }) {
            saveCustomTagsWithIcons(context, customTags.filterNot { it.name.equals(tag, ignoreCase = true) })
        } else if (defaultTags.any { it.equals(tag, ignoreCase = true) }) {
            saveRemovedDefaultTags(context, getRemovedDefaultTags(context) + tag)
        }
    }

    fun getAllTags(context: Context): List<String> {
        val removedDefaults = getRemovedDefaultTags(context)
        val visibleDefaults = defaultTags.filterNot { it in removedDefaults }
        return visibleDefaults + getCustomTagsWithIcons(context).map { it.name }
    }

    fun getIconIdForTag(context: Context, tag: String): String? =
        getCustomTagsWithIcons(context).find { it.name.equals(tag, ignoreCase = true) }?.iconId
}