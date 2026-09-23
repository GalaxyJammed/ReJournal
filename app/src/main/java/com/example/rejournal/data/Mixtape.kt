package com.example.rejournal.data

import androidx.compose.ui.graphics.Color
import java.time.LocalDate

data class CassetteStyle(
    val primaryColor: Color,
    val secondaryColor: Color,
    val accentColor: Color,
    val styleName: String
)

data class Mixtape(
    val year: Int,
    val weekNumber: Int,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val entries: List<MoodEntry>,
    val averageMood: Double,
    val averageEnergy: Double,
    val averageProductivity: Double,
    val averageStress: Double,
    val averageSleep: Double,
    val topActivities: List<Pair<String, Int>>,
    val title: String,
    val vibeGenre: String,
    val cassetteStyle: CassetteStyle
) {
    val hasEntries: Boolean get() = entries.isNotEmpty()
}
