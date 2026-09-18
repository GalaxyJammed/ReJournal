package com.example.rejournal.data

import android.content.Context
import java.time.LocalTime

object GreetingHelper {
    private const val PREFS_NAME = "greeting_prefs"
    private const val KEY_LAST_PERIOD = "last_period"
    private const val KEY_LAST_INDEX = "last_index"

    private val morning = listOf(
        "Greetings, %s! Ready for a fresh morning?",
        "Good morning, %s! Let's make today count.",
        "Rise and shine, %s! What's on your mind today?"
    )
    private val afternoon = listOf(
        "Hey %s! How's your day going so far?",
        "Good afternoon, %s! A good moment to check in.",
        "Hi %s, how has today been treating you?"
    )
    private val evening = listOf(
        "Good evening, %s! Let's wind down for the day.",
        "Evening, %s! How did today go?",
        "Hey %s, time to reflect on the day."
    )
    private val night = listOf(
        "Still up, %s? Let's capture today before you rest.",
        "Good night, %s! One last check-in before bed?",
        "Late one, %s - how are you feeling right now?"
    )

    private fun periodFor(time: LocalTime): String = when {
        time.hour < 5 -> "night"
        time.hour < 12 -> "morning"
        time.hour < 17 -> "afternoon"
        time.hour < 21 -> "evening"
        else -> "night"
    }

    private fun messagesFor(period: String): List<String> = when (period) {
        "morning" -> morning
        "afternoon" -> afternoon
        "evening" -> evening
        else -> night
    }

    fun nextGreeting(context: Context, nickname: String): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val period = periodFor(LocalTime.now())
        val messages = messagesFor(period)

        val lastPeriod = prefs.getString(KEY_LAST_PERIOD, null)
        val lastIndex = prefs.getInt(KEY_LAST_INDEX, -1)

        val nextIndex = if (lastPeriod == period) (lastIndex + 1) % messages.size else 0

        prefs.edit()
            .putString(KEY_LAST_PERIOD, period)
            .putInt(KEY_LAST_INDEX, nextIndex)
            .apply()

        return messages[nextIndex].format(nickname)
    }
}