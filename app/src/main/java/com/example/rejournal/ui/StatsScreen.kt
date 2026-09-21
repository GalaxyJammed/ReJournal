package com.example.rejournal.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.example.rejournal.ui.components.ButterflyCardWrapper
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.rejournal.data.ActivityInsight
import com.example.rejournal.data.StatsCalculator
import com.example.rejournal.data.StatsPeriod
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import com.example.rejournal.data.ActivityFrequency
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.clickable
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.SentimentDissatisfied
import androidx.compose.material.icons.filled.SentimentVerySatisfied
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.WorkOutline
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.CenterAlignedTopAppBar
import com.example.rejournal.data.ActivityIcons
import com.example.rejournal.data.ActivityTagsPrefs
import com.example.rejournal.ui.verticalScrollbar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(viewModel: MoodViewModel, onMoodClick: (Int) -> Unit) {
    val entries by viewModel.allEntries.collectAsState()
    val moodColors = moodColorList()

    var period by remember { mutableStateOf(StatsPeriod.MONTH) }
    var referenceDate by remember { mutableStateOf(LocalDate.now()) }
    var showInsights by remember { mutableStateOf(false) }

    val stats = remember(entries, period, referenceDate) {
        StatsCalculator.calculate(entries, period, referenceDate)
    }
    val (rangeStart, rangeEnd) = remember(period, referenceDate) {
        StatsCalculator.rangeFor(period, referenceDate)
    }

    fun shiftPeriod(forward: Boolean) {
        val amount = if (forward) 1L else -1L
        referenceDate = when (period) {
            StatsPeriod.WEEK -> referenceDate.plusWeeks(amount)
            StatsPeriod.MONTH -> referenceDate.plusMonths(amount)
            StatsPeriod.YEAR -> referenceDate.plusYears(amount)
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = { CenterAlignedTopAppBar(title = { Text("Stats") }) }
    ) { padding: PaddingValues ->
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .verticalScrollbar(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                StatsPeriod.entries.forEachIndexed { index, option ->
                    SegmentedButton(
                        shape = SegmentedButtonDefaults.itemShape(index = index, count = StatsPeriod.entries.size),
                        selected = period == option,
                        onClick = {
                            period = option
                            referenceDate = LocalDate.now()
                        }
                    ) {
                        Text(option.name.lowercase().replaceFirstChar { it.uppercase() })
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { shiftPeriod(forward = false) }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Previous period")
                }
                Text(
                    periodLabel(period, rangeStart, rangeEnd),
                    style = MaterialTheme.typography.titleMedium
                )
                IconButton(onClick = { shiftPeriod(forward = true) }) {
                    Icon(Icons.Filled.ArrowForward, contentDescription = "Next period")
                }
            }

            if (stats.totalEntries == 0) {
                ButterflyCardWrapper(seed = "StatsEmpty", indexOffset = 0, modifier = Modifier.fillMaxWidth()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = softCardShape,
                        border = softCardBorder()
                    ) {
                        Text(
                            "No entries logged in this period yet.",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            } else {
                ButterflyCardWrapper(seed = "StatsLogged_${stats.totalEntries}", indexOffset = 0, modifier = Modifier.fillMaxWidth()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = softCardShape,
                        border = softCardBorder()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "${stats.totalEntries} day${if (stats.totalEntries == 1) "" else "s"} logged",
                                style = MaterialTheme.typography.titleMedium
                            )
                            StatLine(Icons.Filled.EmojiEmotions, "Average mood: ${String.format("%.1f", stats.averageMood)} / 5")
                            StatLine(Icons.Filled.TrendingUp, "Average energy: ${String.format("%.1f", stats.averageEnergy)} / 5")
                            StatLine(Icons.Filled.WorkOutline, "Average productivity: ${String.format("%.1f", stats.averageProductivity)} / 5")
                            StatLine(Icons.Filled.Psychology, "Average stress: ${String.format("%.1f", stats.averageStress)} / 5")
                            StatLine(Icons.Filled.Bedtime, "Average sleep: ${String.format("%.1f", stats.averageSleep)} / 5")
                        }
                    }
                }

                Text("Mood breakdown (tap a mood for details)", style = MaterialTheme.typography.titleMedium)
                MoodDistributionChart(stats.moodCounts, moodColors = moodColors, onMoodClick = onMoodClick)

                ButterflyCardWrapper(seed = "StatsMilestone_${stats.bestDaysOfWeek.size}", indexOffset = 1, modifier = Modifier.fillMaxWidth()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = softCardShape,
                        border = softCardBorder(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            StatLine(Icons.Filled.SentimentVerySatisfied, "Best day of the ${periodNoun(period)}: ${stats.bestDaysOfWeek.joinToString(", ") { it.displayName() }.ifEmpty { "—" }}")
                            StatLine(Icons.Filled.SentimentDissatisfied, "Toughest day of the ${periodNoun(period)}: ${stats.toughestDaysOfWeek.joinToString(", ") { it.displayName() }.ifEmpty { "—" }}")
                        }
                    }
                }

                OutlinedButton(
                    onClick = { showInsights = !showInsights },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (showInsights) "Hide Additional Stats" else "Show Additional Stats")
                    Icon(
                        imageVector = if (showInsights) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                        contentDescription = null,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                if (showInsights) {
                    InsightsSection(
                        activityInsights = stats.activityInsights,
                        energyCorrelation = stats.energyMoodCorrelation,
                        productivityCorrelation = stats.productivityMoodCorrelation,
                        stressCorrelation = stats.stressMoodCorrelation,
                        sleepCorrelation = stats.sleepMoodCorrelation,
                        mostLoggedActivities = stats.mostLoggedActivities,
                        bestDayActivities = stats.bestDayActivities,
                        worstDayActivities = stats.worstDayActivities
                    )
                }
            }
        }
    }
}

@Composable
private fun InsightsSection(
    activityInsights: List<ActivityInsight>,
    energyCorrelation: Double?,
    productivityCorrelation: Double?,
    stressCorrelation: Double?,
    sleepCorrelation: Double?,
    mostLoggedActivities: List<ActivityFrequency>,
    bestDayActivities: List<ActivityFrequency>,
    worstDayActivities: List<ActivityFrequency>
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

        if (mostLoggedActivities.isNotEmpty()) {
            Text("Most logged activities", style = MaterialTheme.typography.titleMedium)
            ButterflyCardWrapper(seed = "StatsMostLogged", indexOffset = 10, modifier = Modifier.fillMaxWidth()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = softCardShape,
                    border = softCardBorder()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        mostLoggedActivities.take(5).forEach { freq ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        ActivityIcons.resolve(freq.tag, ActivityTagsPrefs.getIconIdForTag(LocalContext.current, freq.tag)),
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(freq.tag, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(start = 6.dp))
                                }
                                Text("${freq.count}x", style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }
        }

        if (activityInsights.isNotEmpty()) {
            Text("Mood by activity", style = MaterialTheme.typography.titleMedium)
            ButterflyCardWrapper(seed = "StatsActivityMood", indexOffset = 11, modifier = Modifier.fillMaxWidth()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = softCardShape,
                    border = softCardBorder()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        activityInsights.forEach { insight ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        ActivityIcons.resolve(insight.tag, ActivityTagsPrefs.getIconIdForTag(LocalContext.current, insight.tag)),
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text("${insight.tag} (${insight.count}x)", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(start = 6.dp))
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        "avg ${String.format("%.1f", insight.averageMood)}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.padding(end = 4.dp)
                                    )
                                    MoodGlyph(
                                        moodValue = insight.averageMood.toInt().coerceIn(1, 5),
                                        size = 20.dp,
                                        textStyle = MaterialTheme.typography.bodyLarge
                                    )
                                }
                            }
                        }
                    }
                }
            }
            Text(
                "Sorted best to worst. Only shows tags logged 2+ times.",
                style = MaterialTheme.typography.labelSmall
            )
        }

        if (bestDayActivities.isNotEmpty() || worstDayActivities.isNotEmpty()) {
            Text("Activities on your extreme days", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                if (bestDayActivities.isNotEmpty()) {
                    ButterflyCardWrapper(seed = "StatsBestDay", indexOffset = 12, modifier = Modifier.weight(1f)) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = softCardShape,
                            border = softCardBorder()
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("On your best day(s)", style = MaterialTheme.typography.labelMedium)
                                bestDayActivities.take(4).forEach { freq ->
                                    Text("${freq.tag} (${freq.count}x)", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }
                if (worstDayActivities.isNotEmpty()) {
                    ButterflyCardWrapper(seed = "StatsWorstDay", indexOffset = 13, modifier = Modifier.weight(1f)) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = softCardShape,
                            border = softCardBorder()
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("On your toughest day(s)", style = MaterialTheme.typography.labelMedium)
                                worstDayActivities.take(4).forEach { freq ->
                                    Text("${freq.tag} (${freq.count}x)", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }
            }
        }

        val correlationLines = listOfNotNull(
            correlationLine("Energy", energyCorrelation),
            correlationLine("Productivity", productivityCorrelation),
            correlationLine("Stress", stressCorrelation),
            correlationLine("Sleep", sleepCorrelation)
        )

        if (correlationLines.isNotEmpty()) {
            Text("Mood patterns", style = MaterialTheme.typography.titleMedium)
            ButterflyCardWrapper(seed = "StatsCorrelations", indexOffset = 14, modifier = Modifier.fillMaxWidth()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = softCardShape,
                    border = softCardBorder()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        correlationLines.forEach { line ->
                            Text(line, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
            Text(
                "These reflect patterns in your own logged data, not medical conclusions - small samples can be noisy.",
                style = MaterialTheme.typography.labelSmall
            )
        }

        if (mostLoggedActivities.isEmpty() && activityInsights.isEmpty() && correlationLines.isEmpty()) {
            Text(
                "Not enough data yet for additional insights. Log a few more days (with activity tags) to unlock this.",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

private fun correlationLine(label: String, correlation: Double?): String? {
    if (correlation == null) return null
    return when {
        correlation >= 0.3 -> "Higher $label tends to line up with better mood days."
        correlation <= -0.3 -> "Higher $label tends to line up with lower mood days."
        else -> "$label doesn't show a clear link to mood yet."
    }
}

@Composable
private fun MoodDistributionChart(moodCounts: Map<Int, Int>, moodColors: List<Color>, onMoodClick: (Int) -> Unit) {
    val maxCount = (moodCounts.values.maxOrNull() ?: 0).coerceAtLeast(1)
    val barTrackHeight = 100.dp

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        (1..5).forEach { mood ->
            val count = moodCounts[mood] ?: 0
            val fraction = (count.toFloat() / maxCount).coerceIn(0f, 1f)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { onMoodClick(mood) }
            ) {
                Text(count.toString(), style = MaterialTheme.typography.labelSmall)
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .width(28.dp)
                        .height(barTrackHeight),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(if (count > 0) fraction.coerceAtLeast(0.04f) else 0f)
                            .background(moodColors[mood - 1], RoundedCornerShape(4.dp))
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                MoodGlyph(mood, size = 20.dp, textStyle = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

private fun java.time.DayOfWeek.displayName(): String =
    getDisplayName(TextStyle.FULL, Locale.getDefault())

private fun periodNoun(period: StatsPeriod): String = when (period) {
    StatsPeriod.WEEK -> "Week"
    StatsPeriod.MONTH -> "Month"
    StatsPeriod.YEAR -> "Year"
}

private fun periodLabel(period: StatsPeriod, start: LocalDate, end: LocalDate): String {
    return when (period) {
        StatsPeriod.WEEK -> {
            val formatter = DateTimeFormatter.ofPattern("MMM d")
            "${start.format(formatter)} – ${end.format(formatter)}"
        }
        StatsPeriod.MONTH -> start.format(DateTimeFormatter.ofPattern("MMMM yyyy"))
        StatsPeriod.YEAR -> start.year.toString()
    }
}

@Composable
private fun StatLine(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
        Text(text, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(start = 8.dp))
    }
}