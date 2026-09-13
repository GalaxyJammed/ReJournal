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
import androidx.compose.ui.unit.dp
import com.example.rejournal.data.MoodEntry
import com.example.rejournal.data.MotivationalMessagePrefs
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale
import androidx.compose.foundation.layout.WindowInsets

private val moodColors = listOf(
    Color(0xFFE57373), // 1 - worst
    Color(0xFFFFB74D), // 2
    Color(0xFFFFF176), // 3
    Color(0xFFAED581), // 4
    Color(0xFF81C784)  // 5 - best
)

private enum class LogViewMode { CALENDAR, YEAR_PIXELS }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogScreen(
    viewModel: MoodViewModel,
    onDayClick: (LocalDate) -> Unit,
    onSearchClick: () -> Unit
) {
    val entries by viewModel.allEntries.collectAsState()
    val streak by viewModel.streakInfo.collectAsState()
    val entriesByDate: Map<LocalDate, MoodEntry> = entries.associateBy { it.date }

    val context = LocalContext.current
    var motivationalMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(entries) {
        val cutoff = LocalDate.now().minusDays(29)
        val recent = entries.filter { !it.date.isBefore(cutoff) }
        val relevant = recent.ifEmpty { entries }
        if (relevant.isNotEmpty()) {
            val avgMood = relevant.map { it.mood }.average()
            val moodLevel = Math.round(avgMood).toInt().coerceIn(1, 5)
            motivationalMessage = MotivationalMessagePrefs.nextMessage(context, moodLevel)
        }
    }

    var viewMode by remember { mutableStateOf(LogViewMode.CALENDAR) }
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    var currentYear by remember { mutableStateOf(LocalDate.now().year) }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = { Text("Your Mood Log") },
                actions = {
                    IconButton(onClick = onSearchClick) {
                        Icon(Icons.Filled.Search, contentDescription = "Search entries")
                    }
                }
            )
        }
    ) { padding: PaddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
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
                    currentMonth = currentMonth,
                    entriesByDate = entriesByDate,
                    onPreviousMonth = { currentMonth = currentMonth.minusMonths(1) },
                    onNextMonth = { currentMonth = currentMonth.plusMonths(1) },
                    onDayClick = onDayClick
                )
            } else {
                YearPixelsView(
                    year = currentYear,
                    entriesByDate = entriesByDate,
                    onPreviousYear = { currentYear -= 1 },
                    onNextYear = { currentYear += 1 },
                    onDayClick = onDayClick
                )
            }

            motivationalMessage?.let { message ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp),
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
}

@Composable
private fun CalendarMonthView(
    currentMonth: YearMonth,
    entriesByDate: Map<LocalDate, MoodEntry>,
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