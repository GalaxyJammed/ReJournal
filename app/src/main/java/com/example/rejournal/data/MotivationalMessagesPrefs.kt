package com.example.rejournal.data

import android.content.Context
import kotlin.random.Random

object MotivationalMessagePrefs {
    private const val PREFS_NAME = "motivational_message_prefs"
    private const val KEY_LAST_LEVEL = "last_level"
    private const val KEY_LAST_INDEX = "last_index"

    fun nextMessage(context: Context, moodLevel: Int, entries: List<MoodEntry>): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        if (Random.nextFloat() < 0.2f) {
            val insight = MotivationalMessages.getSmartInsight(entries)
            if (insight != null) return insight
        }

        val messages = MotivationalMessages.messagesFor(moodLevel)

        val lastLevel = prefs.getInt(KEY_LAST_LEVEL, -1)
        val lastIndex = prefs.getInt(KEY_LAST_INDEX, -1)

        val nextIndex = if (lastLevel == moodLevel) {
            (lastIndex + 1) % messages.size
        } else {
            0
        }

        prefs.edit()
            .putInt(KEY_LAST_LEVEL, moodLevel)
            .putInt(KEY_LAST_INDEX, nextIndex)
            .apply()

        return messages[nextIndex]
    }
}
