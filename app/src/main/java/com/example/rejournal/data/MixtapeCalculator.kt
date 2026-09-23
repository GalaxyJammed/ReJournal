package com.example.rejournal.data

import androidx.compose.ui.graphics.Color
import java.time.LocalDate
import java.util.Random

object MixtapeCalculator {

    val defaultBlankStyle = CassetteStyle(
        primaryColor = Color(0xFF424242),
        secondaryColor = Color(0xFF212121),
        accentColor = Color(0xFF757575),
        styleName = "Blank"
    )

    fun getAvailableYears(entries: List<MoodEntry>): List<Int> {
        val currentYear = LocalDate.now().year
        if (entries.isEmpty()) return listOf(currentYear)
        val entryYears = entries.map { it.date.year }.toSet()
        val allYears = (entryYears + currentYear).sortedDescending()
        return allYears
    }

    fun calculateForYear(
        entries: List<MoodEntry>,
        year: Int,
        newestFirst: Boolean = true
    ): List<Mixtape> {
        val jan1 = LocalDate.of(year, 1, 1)
        val firstMonday = jan1.minusDays((jan1.dayOfWeek.value - 1).toLong())

        val dec31 = LocalDate.of(year, 12, 31)
        val numWeeks = if (firstMonday.plusWeeks(52).isBefore(dec31) || firstMonday.plusWeeks(52) == dec31) 53 else 52

        val mixtapes = ArrayList<Mixtape>(numWeeks)

        for (w in 1..numWeeks) {
            val weekStart = firstMonday.plusWeeks((w - 1).toLong())
            val weekEnd = weekStart.plusDays(6)
            val weekEntries = entries.filter { it.date >= weekStart && it.date <= weekEnd }

            mixtapes.add(buildMixtape(year, w, weekStart, weekEnd, weekEntries))
        }

        return if (newestFirst) mixtapes.reversed() else mixtapes
    }

    private fun buildMixtape(
        year: Int,
        weekNumber: Int,
        startDate: LocalDate,
        endDate: LocalDate,
        entries: List<MoodEntry>
    ): Mixtape {
        if (entries.isEmpty()) {
            return Mixtape(
                year = year,
                weekNumber = weekNumber,
                startDate = startDate,
                endDate = endDate,
                entries = emptyList(),
                averageMood = 0.0,
                averageEnergy = 0.0,
                averageProductivity = 0.0,
                averageStress = 0.0,
                averageSleep = 0.0,
                topActivities = emptyList(),
                title = "Blank Tape #$weekNumber",
                vibeGenre = "Unrecorded 📼",
                cassetteStyle = defaultBlankStyle
            )
        }

        val avgMood = entries.map { it.mood }.average()
        val avgEnergy = entries.map { it.energy }.average()
        val avgProd = entries.map { it.productivity }.average()
        val avgStress = entries.map { it.stress }.average()
        val avgSleep = entries.map { it.sleep }.average()

        val activityCounts = entries
            .flatMap { it.activities }
            .groupBy { it }
            .mapValues { it.value.size }
            .toList()
            .sortedByDescending { it.second }

        val seed = year * 1000L + weekNumber * 10L + entries.size
        val random = Random(seed)

        val title = generateTitle(
            weekNumber = weekNumber,
            entries = entries,
            avgMood = avgMood,
            avgEnergy = avgEnergy,
            avgProd = avgProd,
            avgStress = avgStress,
            topActivities = activityCounts,
            random = random
        )

        val vibeGenre = determineGenre(avgMood, avgEnergy, avgProd, avgStress)
        val style = determineStyle(avgMood, avgEnergy, avgStress, avgProd)

        return Mixtape(
            year = year,
            weekNumber = weekNumber,
            startDate = startDate,
            endDate = endDate,
            entries = entries.sortedBy { it.date },
            averageMood = avgMood,
            averageEnergy = avgEnergy,
            averageProductivity = avgProd,
            averageStress = avgStress,
            averageSleep = avgSleep,
            topActivities = activityCounts,
            title = title,
            vibeGenre = vibeGenre,
            cassetteStyle = style
        )
    }

    private fun generateTitle(
        weekNumber: Int,
        entries: List<MoodEntry>,
        avgMood: Double,
        avgEnergy: Double,
        avgProd: Double,
        avgStress: Double,
        topActivities: List<Pair<String, Int>>,
        random: Random
    ): String {
        val adjectives = when {
            avgMood >= 4.0 && avgEnergy >= 3.5 -> listOf("Energetic", "Vibrant", "Euphoric", "Electric", "Exuberant", "Radiant", "Unstoppable", "Dynamic")
            avgMood >= 4.0 && avgEnergy < 3.5 -> listOf("Serene", "Blissful", "Tranquil", "Peaceful", "Cozy", "Golden", "Mellow", "Harmonious")
            avgMood >= 3.0 && avgProd >= 3.5 -> listOf("Focused", "Driven", "Methodical", "Productive", "Determined", "Sharp")
            avgMood >= 3.0 && avgStress >= 3.5 -> listOf("Intense", "Frenzied", "Charged", "Restless", "High-Voltage")
            avgMood >= 3.0 -> listOf("Steady", "Smooth", "Rhythmic", "Eclectic", "Curated", "Flowing", "Balanced")
            avgStress >= 3.5 -> listOf("Turbulent", "Stormy", "Chaotic", "Overheated", "Hectic")
            avgEnergy < 2.5 -> listOf("Melancholic", "Somber", "Bittersweet", "Quiet", "Muted", "Low-Key")
            else -> listOf("Reflective", "Pensive", "Bittersweet", "Shadowed", "Dusty", "Lo-Fi")
        }

        val topTag = topActivities.firstOrNull()?.first?.lowercase() ?: ""
        val nouns = when {
            topTag.contains("work") || topTag.contains("code") || topTag.contains("coding") || topTag.contains("study") || topTag.contains("office") || topTag.contains("writing") ->
                listOf("Sessions", "Hustle", "Chronicles", "Drafts", "Lab", "Sprint", "Workshops")
            topTag.contains("gym") || topTag.contains("run") || topTag.contains("exercise") || topTag.contains("sport") || topTag.contains("yoga") || topTag.contains("walk") ->
                listOf("Grooves", "Strides", "Flow", "Marathon", "Tempo", "Circuit")
            topTag.contains("friend") || topTag.contains("family") || topTag.contains("party") || topTag.contains("social") || topTag.contains("date") ->
                listOf("Jam", "Harmonies", "Gigs", "Gatherings", "Chorus", "Echoes")
            topTag.contains("game") || topTag.contains("movie") || topTag.contains("tv") || topTag.contains("music") || topTag.contains("relax") || topTag.contains("read") ->
                listOf("Odyssey", "Interludes", "Escapes", "Reveries", "Saturations", "Waves")
            topTag.contains("coffee") || topTag.contains("tea") || topTag.contains("food") || topTag.contains("cook") ->
                listOf("Brew", "Flavors", "Tastes", "Feasts", "Mix")
            topTag.contains("travel") || topTag.contains("hike") || topTag.contains("nature") || topTag.contains("drive") ->
                listOf("Expedition", "Trails", "Voyages", "Excursions")
            avgMood >= 4.0 -> listOf("Anthems", "Symphony", "Beats", "Soundtrack", "Vibes")
            avgMood >= 3.0 -> listOf("Sessions", "Tapes", "Mix", "Soundtrack", "Chapters")
            else -> listOf("Echoes", "Melodies", "Reveries", "Ballads", "Rewind")
        }

        val bestDayEntry = entries.maxByOrNull { it.mood }
        val bestDayName = bestDayEntry?.date?.dayOfWeek?.name?.lowercase()?.replaceFirstChar { it.uppercase() } ?: "Midweek"

        val timeframeOptions = listOf(
            "$bestDayName Sessions",
            "$bestDayName Highs",
            "$bestDayName Reveries",
            "Midweek Mix",
            "Weekend Rewind",
            "Vol. $weekNumber"
        )

        val adjective = adjectives[random.nextInt(adjectives.size)]
        val noun = nouns[random.nextInt(nouns.size)]

        return when (random.nextInt(3)) {
            0 -> "The $adjective $bestDayName $noun"
            1 -> "The $adjective $noun"
            else -> "The $adjective $noun (${timeframeOptions[random.nextInt(timeframeOptions.size)]})"
        }
    }

    private fun determineGenre(
        avgMood: Double,
        avgEnergy: Double,
        avgProd: Double,
        avgStress: Double
    ): String {
        return when {
            avgMood >= 4.0 && avgEnergy >= 3.5 -> "Upbeat Synthwave ⚡"
            avgMood >= 4.0 && avgEnergy < 3.5 -> "Chill Lofi Beats ☕"
            avgMood >= 3.0 && avgProd >= 3.5 -> "Cyberpunk Groove 🚀"
            avgMood >= 3.0 && avgStress >= 3.5 -> "Punk Rock Overdrive 🎸"
            avgMood >= 3.0 -> "Golden Hour Pop 🌅"
            avgStress >= 3.5 -> "Industrial Metal ⚡"
            avgEnergy < 2.5 -> "Melancholic Rain 🌧️"
            else -> "Ambient Lo-Fi 🌌"
        }
    }

    private fun determineStyle(
        avgMood: Double,
        avgEnergy: Double,
        avgStress: Double,
        avgProd: Double
    ): CassetteStyle {
        return when {
            avgMood >= 4.0 && avgEnergy >= 3.5 -> CassetteStyle(
                primaryColor = Color(0xFF00E5FF),
                secondaryColor = Color(0xFFFF007F),
                accentColor = Color(0xFF76FF03),
                styleName = "Neon90s"
            )
            avgMood >= 4.0 && avgEnergy < 3.5 -> CassetteStyle(
                primaryColor = Color(0xFFCE93D8),
                secondaryColor = Color(0xFFFFCC80),
                accentColor = Color(0xFF80CBC4),
                styleName = "LofiChill"
            )
            avgMood >= 3.0 && avgProd >= 3.5 -> CassetteStyle(
                primaryColor = Color(0xFFFFEA00),
                secondaryColor = Color(0xFFFF1744),
                accentColor = Color(0xFF00E676),
                styleName = "Cyberpunk"
            )
            avgMood >= 3.0 && avgStress >= 3.5 -> CassetteStyle(
                primaryColor = Color(0xFFFF5252),
                secondaryColor = Color(0xFF37474F),
                accentColor = Color(0xFFFFAB40),
                styleName = "ElectricPunk"
            )
            avgMood >= 3.0 -> CassetteStyle(
                primaryColor = Color(0xFFFFB74D),
                secondaryColor = Color(0xFFF57C00),
                accentColor = Color(0xFFFFE082),
                styleName = "SunsetGold"
            )
            avgEnergy < 2.5 -> CassetteStyle(
                primaryColor = Color(0xFF5C6BC0),
                secondaryColor = Color(0xFF1A237E),
                accentColor = Color(0xFF80DEEA),
                styleName = "RainyMidnight"
            )
            else -> CassetteStyle(
                primaryColor = Color(0xFF78909C),
                secondaryColor = Color(0xFF37474F),
                accentColor = Color(0xFFCFD8DC),
                styleName = "VintageDust"
            )
        }
    }
}
