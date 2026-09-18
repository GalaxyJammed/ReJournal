package com.example.rejournal.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.rejournal.data.MoodEntry
import com.example.rejournal.data.MotivationalMessagePrefs
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import com.example.rejournal.data.TimeCapsule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import com.example.rejournal.data.ReflectionPrefs
import kotlin.random.Random
import com.example.rejournal.data.AchievementCalculator
import com.example.rejournal.data.AchievementDefinitions
import com.example.rejournal.data.AchievementPrefs
import com.example.rejournal.data.AchievementTier
import com.example.rejournal.data.GoalProgressPrefs
import androidx.compose.material.icons.filled.Favorite
import com.example.rejournal.ui.verticalScrollbar
import androidx.compose.ui.graphics.vector.ImageVector

private enum class LogViewMode { CALENDAR, YEAR_PIXELS }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogScreen(
    viewModel: MoodViewModel,
    onDayClick: (LocalDate) -> Unit,
    onSearchClick: () -> Unit,
    onVisitPositiveMemory: (MoodEntry) -> Unit
) {
    val entries by viewModel.allEntries.collectAsState()
    val streak by viewModel.streakInfo.collectAsState()
    val entriesByDate: Map<LocalDate, MoodEntry> = entries.associateBy { it.date }

    val context = LocalContext.current
    var motivationalMessage by remember { mutableStateOf<String?>(null) }

    val moodColors = moodColorList()

    val importantDays by viewModel.allImportantDays.collectAsState()
    val importantDates = remember(importantDays) { importantDays.map { it.date }.toSet() }

    val pendingCapsules by viewModel.pendingCapsules.collectAsState()
    var capsuleBeingShown by remember { mutableStateOf<TimeCapsule?>(null) }

    LaunchedEffect(pendingCapsules) {
        if (capsuleBeingShown == null) {
            capsuleBeingShown = pendingCapsules.firstOrNull()
        }
    }

    LaunchedEffect(entries) {
        val cutoff = LocalDate.now().minusDays(29)
        val recent = entries.filter { !it.date.isBefore(cutoff) }
        val relevant = recent.ifEmpty { entries }
        if (relevant.isNotEmpty()) {
            val avgMood = relevant.map { it.mood }.average()
            val moodLevel = Math.round(avgMood).toInt().coerceIn(1, 5)
            motivationalMessage = MotivationalMessagePrefs.nextMessage(context, moodLevel, entries)
        }
    }

    var reflectionState by remember { mutableStateOf<Pair<String, MoodEntry>?>(null) }
    var hasCheckedReflectionToday by remember { mutableStateOf(false) }

    LaunchedEffect(entries) {
        if (hasCheckedReflectionToday) return@LaunchedEffect
        val today = LocalDate.now()
        val loggedToday = entries.any { it.date == today }
        if (loggedToday) return@LaunchedEffect
        if (ReflectionPrefs.hasRolledToday(context)) return@LaunchedEffect

        hasCheckedReflectionToday = true
        ReflectionPrefs.markRolledToday(context)

        if (Random.nextFloat() < ReflectionPrefs.DAILY_CHANCE) {
            val weekEntry = entries.find { it.date == today.minusWeeks(1) }
            val monthEntry = entries.find { it.date == today.minusMonths(1) }
            reflectionState = when {
                weekEntry != null -> "last week" to weekEntry
                monthEntry != null -> "last month" to monthEntry
                else -> null
            }
        }
    }

    var viewMode by remember { mutableStateOf(LogViewMode.CALENDAR) }
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    var currentYear by remember { mutableStateOf(LocalDate.now().year) }

    val timeCapsulesForAchievements by viewModel.allTimeCapsules.collectAsState()
    val goalCompletionsForAchievements = remember(entries) { GoalProgressPrefs.totalCompletions(context) }
    var achievementQueue by remember { mutableStateOf(listOf<AchievementTier>()) }

    LaunchedEffect(entries, timeCapsulesForAchievements) {
        val newly = AchievementCalculator.computeNewlyUnlockedTierIds(
            context, entries, goalCompletionsForAchievements, timeCapsulesForAchievements.size
        )
        if (newly.isNotEmpty()) {
            AchievementPrefs.markUnlocked(context, newly)
            achievementQueue = achievementQueue + AchievementDefinitions.allTiers.filter { it.id in newly }
        }
    }

    var positiveMemoryEntry by remember { mutableStateOf<MoodEntry?>(null) }

    LaunchedEffect(entries) {
        val candidates = entries.filter { it.mood >= 4 && it.date != LocalDate.now() }
        positiveMemoryEntry = if (candidates.isNotEmpty()) candidates[Random.nextInt(candidates.size)] else null
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Your Mood Log") },
                actions = {
                    IconButton(onClick = onSearchClick) {
                        Icon(Icons.Filled.Search, contentDescription = "Search entries")
                    }
                }
            )
        }
    ) { padding: PaddingValues ->
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .verticalScrollbar(scrollState)
                .padding(16.dp)
        ) {
            if (streak.currentStreak > 0 || streak.longestStreak > 0) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "🔥 ${streak.currentStreak} day${if (streak.currentStreak == 1) "" else "s"}",
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text(
                                "Current streak",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                "Best: ${streak.longestStreak}",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }
            }

            OutlinedButton(
                onClick = {
                    viewMode = if (viewMode == LogViewMode.CALENDAR) LogViewMode.YEAR_PIXELS else LogViewMode.CALENDAR
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Text(if (viewMode == LogViewMode.CALENDAR) "Switch to year view" else "Switch to month view")
            }

            if (viewMode == LogViewMode.CALENDAR) {
                CalendarMonthView(
                    moodColors = moodColors,
                    currentMonth = currentMonth,
                    entriesByDate = entriesByDate,
                    importantDates = importantDates,
                    onPreviousMonth = { currentMonth = currentMonth.minusMonths(1) },
                    onNextMonth = { currentMonth = currentMonth.plusMonths(1) },
                    onDayClick = onDayClick
                )
            } else {
                YearPixelsView(
                    moodColors = moodColors,
                    year = currentYear,
                    entriesByDate = entriesByDate,
                    onPreviousYear = { currentYear -= 1 },
                    onNextYear = { currentYear += 1 },
                    onDayClick = onDayClick
                )
            }
            positiveMemoryEntry?.let { memoryEntry ->
                OutlinedButton(
                    onClick = { onVisitPositiveMemory(memoryEntry) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Text("Visit Past Positive Memory")
                }
            }

            motivationalMessage?.let { message ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Text(
                        message,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
    capsuleBeingShown?.let { capsule ->
        AlertDialog(
            onDismissRequest = { },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.dismissCapsule(capsule)
                    capsuleBeingShown = null
                }) { Text("Got it") }
            },
            title = { Text("📬 A message from your past self") },
            text = { Text(capsule.message) }
        )
    }
    achievementQueue.firstOrNull()?.let { tier ->
        AlertDialog(
            onDismissRequest = { },
            confirmButton = {
                TextButton(onClick = { achievementQueue = achievementQueue.drop(1) }) { Text("Nice!") }
            },
            title = { Text("🏆 Achievement Unlocked!") },
            text = {
                Column {
                    Text(tier.title, style = MaterialTheme.typography.titleMedium)
                    Text(tier.description, style = MaterialTheme.typography.bodyMedium)
                }
            }
        )
    }
    reflectionState?.let { (label, pastEntry) ->
        AlertDialog(
            onDismissRequest = { reflectionState = null },
            confirmButton = {
                TextButton(onClick = {
                    reflectionState = null
                    onDayClick(LocalDate.now())
                }) { Text("Log Today") }
            },
            dismissButton = {
                TextButton(onClick = { reflectionState = null }) { Text("Not now") }
            },
            title = { Text("A quick reflection") },
            text = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("$label on this day, you felt ")
                    MoodGlyph(pastEntry.mood, size = 20.dp)
                    Text(". How do you feel now?")
                }
            }
        )
    }
}

@Composable
private fun CalendarMonthView(
    moodColors: List<Color>,
    currentMonth: YearMonth,
    entriesByDate: Map<LocalDate, MoodEntry>,
    importantDates: Set<LocalDate>,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onDayClick: (LocalDate) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPreviousMonth) {
            Icon(Icons.Filled.ArrowBack, contentDescription = "Previous month")
        }
        Text(
            "${currentMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${currentMonth.year}",
            style = MaterialTheme.typography.titleLarge
        )
        IconButton(onClick = onNextMonth) {
            Icon(Icons.Filled.ArrowForward, contentDescription = "Next month")
        }
    }

    Row(modifier = Modifier.fillMaxWidth().padding(top = 12.dp)) {
        listOf("S", "M", "T", "W", "T", "F", "S").forEach { label ->
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                Text(label, style = MaterialTheme.typography.labelMedium)
            }
        }
    }

    val firstOfMonth = currentMonth.atDay(1)
    val leadingBlanks = firstOfMonth.dayOfWeek.value % 7
    val daysInMonth = currentMonth.lengthOfMonth()
    val cells: List<LocalDate?> = List(leadingBlanks) { null } +
            (1..daysInMonth).map { currentMonth.atDay(it) }
    val weeks = cells.chunked(7)

    Column(modifier = Modifier.padding(top = 4.dp)) {
        weeks.forEach { week ->
            Row(modifier = Modifier.fillMaxWidth()) {
                week.forEach { date ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .padding(2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (date != null) {
                            val entry = entriesByDate[date]
                            val bgColor = entry?.let { moodColors[it.mood - 1] }
                                ?: MaterialTheme.colorScheme.surfaceVariant
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(bgColor, RoundedCornerShape(8.dp))
                                    .clickable { onDayClick(date) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    date.dayOfMonth.toString(),
                                    style = MaterialTheme.typography.bodyMedium,
                                    textAlign = TextAlign.Center
                                )

                                if (date in importantDates) {
                                    OutlinedMarkerIcon(
                                        icon = Icons.Filled.Star,
                                        description = "Important day",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.align(Alignment.TopEnd)
                                    )
                                }
                                if (entry?.isFavorite == true) {
                                    OutlinedMarkerIcon(
                                        icon = Icons.Filled.Favorite,
                                        description = "Favorite day",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.align(Alignment.TopStart)
                                    )
                                }
                            }
                        }
                    }
                }
                repeat(7 - week.size) {
                    Box(modifier = Modifier.weight(1f).aspectRatio(1f))
                }
            }
        }
    }
}

@Composable
private fun YearPixelsView(
    moodColors: List<Color>,
    year: Int,
    entriesByDate: Map<LocalDate, MoodEntry>,
    onPreviousYear: () -> Unit,
    onNextYear: () -> Unit,
    onDayClick: (LocalDate) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPreviousYear) {
            Icon(Icons.Filled.ArrowBack, contentDescription = "Previous year")
        }
        Text(year.toString(), style = MaterialTheme.typography.titleLarge)
        IconButton(onClick = onNextYear) {
            Icon(Icons.Filled.ArrowForward, contentDescription = "Next year")
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp)
    ) {
        for (month in 1..12) {
            val yearMonth = YearMonth.of(year, month)
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    yearMonth.month.getDisplayName(TextStyle.SHORT, Locale.getDefault()).take(1),
                    style = MaterialTheme.typography.labelSmall
                )
                Spacer(modifier = Modifier.height(4.dp))
                for (day in 1..31) {
                    if (day <= yearMonth.lengthOfMonth()) {
                        val date = yearMonth.atDay(day)
                        val entry = entriesByDate[date]
                        val isFuture = date.isAfter(LocalDate.now())
                        val color = when {
                            entry != null -> moodColors[entry.mood - 1]
                            isFuture -> Color.Transparent
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        }
                        Box(
                            modifier = Modifier
                                .padding(1.dp)
                                .size(9.dp)
                                .background(color, RoundedCornerShape(2.dp))
                                .clickable(enabled = !isFuture) { onDayClick(date) }
                        )
                    } else {
                        Spacer(modifier = Modifier.size(11.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun OutlinedMarkerIcon(
    icon: ImageVector,
    description: String,
    tint: Color,
    modifier: Modifier = Modifier,
    size: Dp = 14.dp
) {
    Box(
        modifier = modifier.size(size + 4.dp),
        contentAlignment = Alignment.Center
    ) {
        // A slightly larger, dark silhouette drawn first acts as a stroke/
        // outline, since Compose's vector Icon has no native stroke support.
        Icon(
            icon,
            contentDescription = null,
            tint = Color.Black.copy(alpha = 0.55f),
            modifier = Modifier.size(size + 4.dp)
        )
        Icon(
            icon,
            contentDescription = description,
            tint = tint,
            modifier = Modifier.size(size)
        )
    }
}