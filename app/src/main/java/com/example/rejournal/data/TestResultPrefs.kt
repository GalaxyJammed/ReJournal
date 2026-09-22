package com.example.rejournal.data

import android.content.Context

object TestResultPrefs {
    private const val PREFS_NAME = "test_result_prefs"
    private const val KEY_TESTS_COMPLETED = "tests_completed_count"

    fun getTestsCompletedCount(context: Context): Int {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getInt(KEY_TESTS_COMPLETED, 0)
    }

    fun incrementTestsCompletedCount(context: Context) {
        val current = getTestsCompletedCount(context)
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
            .putInt(KEY_TESTS_COMPLETED, current + 1)
            .apply()
    }
}
