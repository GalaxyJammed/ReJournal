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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
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
import com.example.rejournal.ui.components.IconPill
import com.example.rejournal.ui.components.PastelIcon
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
import androidx.compose.material.icons.filled.AddReaction
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.NoteAlt
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.automirrored.filled.ShortText
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.SentimentDissatisfied
import androidx.compose.material.icons.filled.SentimentVerySatisfied
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.WorkOutline
import com.example.rejournal.data.PeriodStats
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.HorizontalDivider
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
                    PastelIcon(Icons.Filled.ArrowBack, contentDescription = "Previous period")
                }
                Text(
                    periodLabel(period, rangeStart, rangeEnd),
                    style = MaterialTheme.typography.titleMedium
                )
                IconButton(onClick = { shiftPeriod(forward = true) }) {
                    PastelIcon(Icons.Filled.ArrowForward, contentDescription = "Next period")
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
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                "${stats.totalEntries} day${if (stats.totalEntries == 1) "" else "s"} logged",
                                style = MaterialTheme.typography.titleMedium
                            )
                            HorizontalDivider(modifier = Modifier.padding(bottom = 2.dp))
                            MetricBarRow(Icons.Filled.AddReaction, "Mood", stats.averageMood, Color(0xFFBA68C8))
                            MetricBarRow(Icons.Filled.TrendingUp, "Energy", stats.averageEnergy, Color(0xFFFFB74D))
                            MetricBarRow(Icons.Filled.WorkOutline, "Productivity", stats.averageProductivity, Color(0xFF81C784))
                            MetricBarRow(Icons.Filled.Psychology, "Stress", stats.averageStress, Color(0xFFE57373))
                            MetricBarRow(Icons.Filled.Hotel, "Sleep", stats.averageSleep, Color(0xFF64B5F6))
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
                    PastelIcon(
                        icon = if (showInsights) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                        contentDescription = null,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                if (showInsights) {
                    InsightsSection(stats = stats)
                }
            }
        }
    }
}

@Composable
private fun JournalHighlightItem(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier
    ) {
        IconPill(icon = icon, size = 36.dp, iconSize = 20.dp)
        Column {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun InsightsSection(stats: PeriodStats) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

        val moodLabels = listOf("Rough", "Meh", "Neutral", "Good", "Great")
        val dominantMoodLabel = stats.dominantMood?.let { moodVal ->
            val label = moodLabels.getOrElse(moodVal - 1) { "Neutral" }
            val pct = if (stats.totalEntries > 0) (stats.dominantMoodCount * 100 / stats.totalEntries) else 0
            "$label ($pct%)"
        } ?: "—"

        Text("Journaling Highlights", style = MaterialTheme.typography.titleMedium)
        ButterflyCardWrapper(seed = "StatsJournalHighlights", indexOffset = 9, modifier = Modifier.fillMaxWidth()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = softCardShape,
                border = softCardBorder()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        JournalHighlightItem(
                            icon = Icons.Filled.CalendarToday,
                            label = "Consistency",
                            value = "${stats.loggingRatePercentage}% (${stats.totalEntries}/${stats.totalDaysInPeriod}d)",
                            modifier = Modifier.weight(1f)
                        )
                        JournalHighlightItem(
                            icon = Icons.Filled.NoteAlt,
                            label = "Words written",
                            value = "${stats.totalWordsLogged}",
                            modifier = Modifier.weight(1f)
                        )
                    }
                    HorizontalDivider()
                    Row(modifier = Modifier.fillMaxWidth()) {
                        JournalHighlightItem(
                            icon = Icons.AutoMirrored.Filled.ShortText,
                            label = "Avg entry depth",
                            value = "${stats.avgWordsPerEntry} words/entry",
                            modifier = Modifier.weight(1f)
                        )
                        JournalHighlightItem(
                            icon = Icons.Filled.EmojiEmotions,
                            label = "Primary mood",
                            value = dominantMoodLabel,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        if (stats.mostLoggedActivities.isNotEmpty()) {
            Text("Most logged activities", style = MaterialTheme.typography.titleMedium)
            ButterflyCardWrapper(seed = "StatsMostLogged", indexOffset = 10, modifier = Modifier.fillMaxWidth()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = softCardShape,
                    border = softCardBorder()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        stats.mostLoggedActivities.take(5).forEach { freq ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconPill(
                                        icon = ActivityIcons.resolve(freq.tag, ActivityTagsPrefs.getIconIdForTag(LocalContext.current, freq.tag)),
                                        size = 28.dp,
                                        iconSize = 16.dp
                                    )
                                    Text(freq.tag, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(start = 8.dp))
                                }
                                Text("${freq.count}x", style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }
        }

        if (stats.activityInsights.isNotEmpty()) {
            Text("Mood by activity", style = MaterialTheme.typography.titleMedium)
            ButterflyCardWrapper(seed = "StatsActivityMood", indexOffset = 11, modifier = Modifier.fillMaxWidth()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = softCardShape,
                    border = softCardBorder()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        stats.activityInsights.forEach { insight ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconPill(
                                        icon = ActivityIcons.resolve(insight.tag, ActivityTagsPrefs.getIconIdForTag(LocalContext.current, insight.tag)),
                                        size = 28.dp,
                                        iconSize = 16.dp
                                    )
                                    Text("${insight.tag} (${insight.count}x)", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(start = 8.dp))
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

        if (stats.bestDayActivities.isNotEmpty() || stats.worstDayActivities.isNotEmpty()) {
            Text("Activities on your extreme days", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                if (stats.bestDayActivities.isNotEmpty()) {
                    ButterflyCardWrapper(seed = "StatsBestDay", indexOffset = 12, modifier = Modifier.weight(1f)) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = softCardShape,
                            border = softCardBorder()
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("Best day(s)", style = MaterialTheme.typography.labelMedium)
                                HorizontalDivider()
                                stats.bestDayActivities.take(5).forEach { freq ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        modifier = Modifier.padding(vertical = 1.dp)
                                    ) {
                                        IconPill(
                                            icon = ActivityIcons.resolve(
                                                freq.tag,
                                                ActivityTagsPrefs.getIconIdForTag(LocalContext.current, freq.tag)
                                            ),
                                            size = 24.dp,
                                            iconSize = 14.dp
                                        )
                                        Text(
                                            text = "${freq.tag} (${freq.count}x)",
                                            style = MaterialTheme.typography.bodySmall,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                if (stats.worstDayActivities.isNotEmpty()) {
                    ButterflyCardWrapper(seed = "StatsWorstDay", indexOffset = 13, modifier = Modifier.weight(1f)) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = softCardShape,
                            border = softCardBorder()
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("Toughest day(s)", style = MaterialTheme.typography.labelMedium)
                                HorizontalDivider()
                                stats.worstDayActivities.take(5).forEach { freq ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        modifier = Modifier.padding(vertical = 1.dp)
                                    ) {
                                        IconPill(
                                            icon = ActivityIcons.resolve(
                                                freq.tag,
                                                ActivityTagsPrefs.getIconIdForTag(LocalContext.current, freq.tag)
                                            ),
                                            size = 24.dp,
                                            iconSize = 14.dp
                                        )
                                        Text(
                                            text = "${freq.tag} (${freq.count}x)",
                                            style = MaterialTheme.typography.bodySmall,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        val correlationLines = listOfNotNull(
            correlationLine("Energy", stats.energyMoodCorrelation),
            correlationLine("Productivity", stats.productivityMoodCorrelation),
            correlationLine("Stress", stats.stressMoodCorrelation),
            correlationLine("Sleep", stats.sleepMoodCorrelation)
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
                        correlationLines.forEachIndexed { index, line ->
                            if (index > 0) {
                                HorizontalDivider()
                            }
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

        if (stats.mostLoggedActivities.isEmpty() && stats.activityInsights.isEmpty() && correlationLines.isEmpty()) {
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
private fun CustomProgressBar(
    progress: Float,
    color: Color,
    modifier: Modifier = Modifier,
    trackColor: Color = MaterialTheme.colorScheme.surfaceContainerHigh
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(CircleShape)
            .background(trackColor)
    ) {
        if (progress > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(progress.coerceIn(0f, 1f))
                    .clip(CircleShape)
                    .background(color)
            )
        }
    }
}

@Composable
private fun MetricBarRow(
    icon: ImageVector,
    label: String,
    value: Double,
    barColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.width(128.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PastelIcon(
                icon = icon,
                contentDescription = label,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        CustomProgressBar(
            progress = (value / 5.0).toFloat(),
            color = barColor,
            modifier = Modifier
                .weight(1f)
                .height(8.dp)
        )

        Text(
            text = String.format(Locale.US, "%.1f", value),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(32.dp),
            textAlign = TextAlign.End
        )
    }
}

@Composable
private fun StatLine(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
        IconPill(icon = icon, size = 32.dp, iconSize = 18.dp)
        Text(text, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(start = 12.dp))
    }
}