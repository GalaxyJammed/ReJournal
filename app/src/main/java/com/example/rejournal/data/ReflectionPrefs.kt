package com.example.rejournal.data

import android.content.Context
import java.time.LocalDate

object ReflectionPrefs {
    private const val PREFS_NAME = "reflection_prefs"
    private const val KEY_LAST_ROLL_DATE = "last_roll_date"
    const val DAILY_CHANCE = 0.35f


    fun hasRolledToday(context: Context): Boolean {
        val stored = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_LAST_ROLL_DATE, null)
        return stored == LocalDate.now().toString()
    }

    fun markRolledToday(context: Context) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
            .putString(KEY_LAST_ROLL_DATE, LocalDate.now().toString())
            .apply()
    }
}