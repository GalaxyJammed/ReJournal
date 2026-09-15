package com.example.rejournal.ui.theme

import androidx.compose.runtime.mutableStateOf
import com.example.rejournal.data.MoodDisplayMode
import com.example.rejournal.data.MoodPalettes

object MoodVisualsState {
    var mode = mutableStateOf(MoodDisplayMode.EMOJI)
    var colors = mutableStateOf(MoodPalettes.default)
}