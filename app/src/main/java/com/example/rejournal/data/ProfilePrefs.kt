package com.example.rejournal.data

import android.content.Context

object ProfilePrefs {
    private const val PREFS_NAME = "profile_prefs"
    private const val KEY_NICKNAME = "nickname"
    private const val KEY_AGE = "age"
    private const val KEY_ONBOARDED = "onboarded"
    const val MAX_NICKNAME_LENGTH = 12

    fun isOnboarded(context: Context): Boolean =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getBoolean(KEY_ONBOARDED, false)

    fun setOnboarded(context: Context, value: Boolean) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
            .putBoolean(KEY_ONBOARDED, value)
            .apply()
    }

    fun getNickname(context: Context): String? =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getString(KEY_NICKNAME, null)?.takeIf { it.isNotBlank() }

    fun getAge(context: Context): Int? {
        val stored = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getInt(KEY_AGE, -1)
        return if (stored >= 0) stored else null
    }

    fun save(context: Context, nickname: String, age: Int?) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
            .putString(KEY_NICKNAME, nickname.trim().take(MAX_NICKNAME_LENGTH))
            .putInt(KEY_AGE, age ?: -1)
            .apply()
    }
}