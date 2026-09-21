package com.example.rejournal.data

import android.content.Context

object ToolkitPrefs {
    private const val PREFS_NAME = "toolkit_prefs"
    private const val KEY_MEDIA_LINK = "comfort_media_link"
    private const val KEY_MEDIA_LABEL = "comfort_media_label"

    private fun prefs(context: Context) = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getMediaLink(context: Context): String? =
        prefs(context).getString(KEY_MEDIA_LINK, null)?.takeIf { it.isNotBlank() }

    fun getMediaLabel(context: Context): String? =
        prefs(context).getString(KEY_MEDIA_LABEL, null)?.takeIf { it.isNotBlank() }

    fun saveMedia(context: Context, link: String, label: String) {
        prefs(context).edit()
            .putString(KEY_MEDIA_LINK, link.trim())
            .putString(KEY_MEDIA_LABEL, label.trim())
            .apply()
    }

    fun clearMedia(context: Context) {
        prefs(context).edit()
            .remove(KEY_MEDIA_LINK)
            .remove(KEY_MEDIA_LABEL)
            .apply()
    }
}