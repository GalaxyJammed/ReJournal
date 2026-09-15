package com.example.rejournal.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.rejournal.data.MoodDisplayMode
import com.example.rejournal.data.moodEmojis
import com.example.rejournal.ui.theme.MoodVisualsState


@Composable
fun moodColorList(): List<Color> {
    val colors by MoodVisualsState.colors
    return colors.map { Color(it) }
}

@Composable
fun moodColorFor(moodValue: Int): Color = moodColorList()[moodValue - 1]

@Composable
fun MoodGlyph(moodValue: Int, size: Dp = 24.dp, textStyle: TextStyle = MaterialTheme.typography.headlineSmall) {
    val mode by MoodVisualsState.mode
    if (mode == MoodDisplayMode.EMOJI) {
        Text(moodEmojis[moodValue - 1], style = textStyle)
    } else {
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .size(size)
                .background(moodColorFor(moodValue), CircleShape)
        )
    }
}