package com.example.rejournal.data

import android.content.Context

object ThemePrefs {
    private const val PREFS_NAME = "theme_prefs"
    private const val KEY_THEME = "selected_theme"
    private const val KEY_DARK_MODE = "dark_mode"

    fun getTheme(context: Context): AppTheme {
        val name = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_THEME, AppTheme.CLASSIC.name)
        return AppTheme.entries.find { it.name == name } ?: AppTheme.CLASSIC
    }

    fun setTheme(context: Context, theme: AppTheme) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
            .putString(KEY_THEME, theme.name)
            .apply()
    }

    fun isDarkMode(context: Context): Boolean =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getBoolean(KEY_DARK_MODE, false)

    fun setDarkMode(context: Context, darkMode: Boolean) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
            .putBoolean(KEY_DARK_MODE, darkMode)
            .apply()
    }
}