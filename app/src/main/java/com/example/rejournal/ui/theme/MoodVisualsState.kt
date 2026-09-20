package com.example.rejournal.ui.theme

import androidx.compose.runtime.mutableStateOf
import com.example.rejournal.data.MoodDisplayMode
import com.example.rejournal.data.MoodPalettes
import com.example.rejournal.data.MoodEmojiSets

object MoodVisualsState {
    var mode = mutableStateOf(MoodDisplayMode.EMOJI)
    var colors = mutableStateOf(MoodPalettes.default)
    var emojis = mutableStateOf(MoodEmojiSets.default)
}
