package com.example.rejournal.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.dp


@Composable
fun softCardBorder(): BorderStroke {
    val outline = MaterialTheme.colorScheme.outlineVariant
    val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f
    val alpha = if (isDark) 0.8f else 0.7f
    return BorderStroke(1.5.dp, outline.copy(alpha = alpha))
}

val softCardShape = RoundedCornerShape(16.dp)