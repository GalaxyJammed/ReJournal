package com.example.rejournal.data

import android.content.Context

enum class MoodDisplayMode { EMOJI, CIRCLE }

object MoodEmojiSets {
    val default = listOf("😞", "😕", "😐", "🙂", "😄")
    val weather = listOf("⛈️", "🌧️", "☁️", "🌤️", "☀️")
    val animals = listOf("🦎", "🦊", "🐱", "🐶", "🦁")

    val presets: Map<String, List<String>> = linkedMapOf(
        "Default" to default,
        "Weather" to weather,
        "Animals" to animals
    )
}

val moodEmojis = MoodEmojiSets.default

object MoodPalettes {
    val default = listOf(0xFFE57373L, 0xFFFFB74DL, 0xFFFFF176L, 0xFF81C784L, 0xFF4CAF50L)
    val pastel = listOf(0xFFF6A6A6L, 0xFFF9CBA0L, 0xFFF7EFA0L, 0xFFC9E4B5L, 0xFFA8D8B9L)
    val ocean = listOf(0xFF264E70L, 0xFF4A7C8CL, 0xFF6FA3A0L, 0xFF9FD8B5L, 0xFFE6F5D0L)
    val sunset = listOf(0xFF6A0572L, 0xFFAB1E5BL, 0xFFE84545L, 0xFFFF9B42L, 0xFFFFD37EL)

    val presets: Map<String, List<Long>> = linkedMapOf(
        "Default" to default,
        "Pastel" to pastel,
        "Ocean" to ocean,
        "Sunset" to sunset
    )
}

object MoodAppearancePrefs {
    private const val PREFS_NAME = "mood_appearance_prefs"
    private const val KEY_MODE = "mode"
    private const val KEY_PALETTE = "palette"
    private const val KEY_EMOJI_SET = "emoji_set"
    private const val KEY_CUSTOM_PREFIX = "custom_color_"

    private fun prefs(context: Context) = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getMode(context: Context): MoodDisplayMode {
        val name = prefs(context).getString(KEY_MODE, MoodDisplayMode.EMOJI.name)
        return MoodDisplayMode.entries.find { it.name == name } ?: MoodDisplayMode.EMOJI
    }

    fun setMode(context: Context, mode: MoodDisplayMode) {
        prefs(context).edit().putString(KEY_MODE, mode.name).apply()
    }

    fun getPaletteName(context: Context): String = prefs(context).getString(KEY_PALETTE, "Default") ?: "Default"

    fun setPaletteName(context: Context, name: String) {
        prefs(context).edit().putString(KEY_PALETTE, name).apply()
    }

    fun getEmojiSetName(context: Context): String = prefs(context).getString(KEY_EMOJI_SET, "Default") ?: "Default"

    fun setEmojiSetName(context: Context, name: String) {
        prefs(context).edit().putString(KEY_EMOJI_SET, name).apply()
    }

    fun getCustomColors(context: Context): List<Long> =
        (0..4).map { prefs(context).getLong("$KEY_CUSTOM_PREFIX$it", MoodPalettes.default[it]) }

    fun setCustomColor(context: Context, index: Int, colorLong: Long) {
        prefs(context).edit().putLong("$KEY_CUSTOM_PREFIX$index", colorLong).apply()
    }

    fun getActiveColors(context: Context): List<Long> {
        val paletteName = getPaletteName(context)
        return if (paletteName == "Custom") getCustomColors(context) else MoodPalettes.presets[paletteName] ?: MoodPalettes.default
    }

    private const val KEY_CUSTOM_EMOJI_PREFIX = "custom_emoji_"

    fun getCustomEmojis(context: Context): List<String> =
        (0..4).map { prefs(context).getString("$KEY_CUSTOM_EMOJI_PREFIX$it", MoodEmojiSets.default[it]) ?: MoodEmojiSets.default[it] }

    fun setCustomEmoji(context: Context, index: Int, emoji: String) {
        prefs(context).edit().putString("$KEY_CUSTOM_EMOJI_PREFIX$index", emoji).apply()
    }

    fun getActiveEmojis(context: Context): List<String> {
        val name = getEmojiSetName(context)
        return if (name == "Custom") getCustomEmojis(context) else MoodEmojiSets.presets[name] ?: MoodEmojiSets.default
    }

    fun resetCustomEmojis(context: Context) {
        val editor = prefs(context).edit()
        (0..4).forEach { editor.remove("$KEY_CUSTOM_EMOJI_PREFIX$it") }
        editor.apply()
    }
}
