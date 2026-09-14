package com.example.rejournal.ui.theme

import androidx.compose.runtime.mutableStateOf
import com.example.rejournal.data.AppTheme

object ThemeState {
    var current = mutableStateOf(AppTheme.CLASSIC)
    var darkMode = mutableStateOf(false)
}