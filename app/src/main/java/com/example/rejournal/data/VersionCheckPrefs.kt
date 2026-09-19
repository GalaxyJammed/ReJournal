package com.example.rejournal.data

import android.content.Context
import java.time.LocalDate

object VersionCheckPrefs {
    private const val PREFS_NAME = "version_check_prefs"
    private const val KEY_LAST_CHECK_DATE = "last_check_date"
    private const val KEY_CACHED_VERSION = "cached_version"
    private const val KEY_CACHED_URL = "cached_url"

    fun hasCheckedToday(context: Context): Boolean {
        val stored = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_LAST_CHECK_DATE, null)
        return stored == LocalDate.now().toString()
    }

    fun saveResult(context: Context, update: AvailableUpdate?) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
            .putString(KEY_LAST_CHECK_DATE, LocalDate.now().toString())
            .putString(KEY_CACHED_VERSION, update?.version)
            .putString(KEY_CACHED_URL, update?.releaseUrl)
            .apply()
    }

    fun getCachedUpdate(context: Context): AvailableUpdate? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val version = prefs.getString(KEY_CACHED_VERSION, null) ?: return null
        val url = prefs.getString(KEY_CACHED_URL, null) ?: return null
        return AvailableUpdate(version, url)
    }
}