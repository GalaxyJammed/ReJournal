package com.example.rejournal.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rejournal.data.ActivityTagsPrefs
import com.example.rejournal.data.MoodEntry
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import androidx.compose.ui.graphics.Brush

private val moodColors = listOf(
    Color(0xFFE57373),
    Color(0xFFFFB74D),
    Color(0xFFFFF176),
    Color(0xFFAED581),
    Color(0xFF81C784)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrendScreen(viewModel: MoodViewModel) {
    val context = LocalContext.current
    val entries by viewModel.allEntries.collectAsState()
    val availableTags = remember { ActivityTagsPrefs.getAllTags(context) }

    var energyFilter by remember { mutableStateOf<Int?>(null) }
    var productivityFilter by remember { mutableStateOf<Int?>(null) }
    var stressFilter by remember { mutableStateOf<Int?>(null) }
    var sleepFilter by remember { mutableStateOf<Int?>(null) }
    var selectedTags by remember { mutableStateOf(setOf<String>()) }

    val hasFilters = selectedTags.isNotEmpty() ||
            energyFilter != null || productivityFilter != null || stressFilter != null || sleepFilter != null

    val cutoff = LocalDate.now().minusDays(29)
    val recentEntries: List<MoodEntry> = remember(entries, energyFilter, productivityFilter, stressFilter, sleepFilter, selectedTags) {
        entries.filter { entry ->
            !entry.date.isBefore(cutoff) &&
                    (energyFilter == null || entry.energy == energyFilter) &&
                    (productivityFilter == null || entry.productivity == productivityFilter) &&
                    (stressFilter == null || entry.stress == stressFilter) &&
                    (sleepFilter == null || entry.sleep == sleepFilter) &&
                    (selectedTags.isEmpty() || selectedTags.all { it in entry.activities })
        }.sortedBy { it.date }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = { TopAppBar(title = { Text("Trend") }) }
    ) { padding: PaddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TrendSliderFilter(label = "Energy", value = energyFilter, onValueChange = { energyFilter = it })
            TrendSliderFilter(label = "Productivity", value = productivityFilter, onValueChange = { productivityFilter = it })
            TrendSliderFilter(label = "Stress", value = stressFilter, onValueChange = { stressFilter = it })
            TrendSliderFilter(label = "Sleep", value = sleepFilter, onValueChange = { sleepFilter = it })

            Text("Activities (all selected must match)", style = MaterialTheme.typography.titleMedium)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(availableTags) { tag ->
                    FilterChip(
                        selected = tag in selectedTags,
                        onClick = {
                            selectedTags = if (tag in selectedTags) selectedTags - tag else selectedTags + tag
                        },
                        label = { Text(tag) }
                    )
                }
            }

            if (hasFilters) {
                TextButton(onClick = {
                    selectedTags = emptySet()
                    energyFilter = null
                    productivityFilter = null
                    stressFilter = null
                    sleepFilter = null
                }) {
                    Text("Clear filters")
                }
            }

            if (recentEntries.size < 2) {
                Text(
                    if (hasFilters) "Not enough matching days to draw a trend." else "Log at least 2 days to see a trend line.",
                    modifier = Modifier.padding(top = 24.dp)
                )
            } else {
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp)) {
                    MoodLineChart(entries = recentEntries)
                }
            }
        }
    }
}

@Composable
private fun TrendSliderFilter(
    label: String,
    value: Int?,
    onValueChange: (Int?) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, style = MaterialTheme.typography.titleMedium)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(value?.toString() ?: "Off", style = MaterialTheme.typography.titleMedium)
                if (value != null) {
                    Icon(
                        Icons.Filled.Close,
                        contentDescription = "Clear $label filter",
                        modifier = Modifier
                            .size(20.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                            .clickable { onValueChange(null) }
                            .padding(3.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        Slider(
            value = (value ?: 1).toFloat(),
            onValueChange = { onValueChange(it.toInt()) },
            valueRange = 1f..5f,
            steps = 3
        )
    }
}

@Composable
private fun MoodLineChart(entries: List<MoodEntry>) {
    val textMeasurer = rememberTextMeasurer()
    val dateFormatter = DateTimeFormatter.ofPattern("M/d")
    val axisLabelStyle = TextStyle(fontSize = 10.sp, color = Color.Gray)

    val firstDate = entries.first().date
    val lastDate = entries.last().date
    val totalDaySpan = java.time.temporal.ChronoUnit.DAYS.between(firstDate, lastDate).toFloat()

    Canvas(modifier = Modifier.fillMaxSize()) {
        val leftPadding = 24.dp.toPx()
        val bottomPadding = 24.dp.toPx()
        val topPadding = 8.dp.toPx()
        val chartWidth = size.width - leftPadding
        val chartHeight = size.height - bottomPadding - topPadding

        for (mood in 1..5) {
            val y = topPadding + chartHeight * (1f - (mood - 1) / 4f)
            drawLine(
                color = Color.LightGray,
                start = Offset(leftPadding, y),
                end = Offset(size.width, y),
                strokeWidth = 1.dp.toPx()
            )
            drawText(
                textMeasurer = textMeasurer,
                text = mood.toString(),
                topLeft = Offset(0f, y - 8.dp.toPx()),
                style = axisLabelStyle
            )
        }

        val points = entries.map { entry ->
            val daysFromStart = java.time.temporal.ChronoUnit.DAYS.between(firstDate, entry.date).toFloat()
            val fraction = if (totalDaySpan > 0f) daysFromStart / totalDaySpan else 0f
            val x = leftPadding + chartWidth * fraction
            val y = topPadding + chartHeight * (1f - (entry.mood - 1) / 4f)
            Offset(x, y)
        }

        for (i in 0 until points.size - 1) {
            val startColor = moodColors[entries[i].mood - 1]
            val endColor = moodColors[entries[i + 1].mood - 1]
            drawLine(
                brush = Brush.linearGradient(
                    colors = listOf(startColor, endColor),
                    start = points[i],
                    end = points[i + 1]
                ),
                start = points[i],
                end = points[i + 1],
                strokeWidth = 3.dp.toPx()
            )
        }

        val labelEvery = maxOf(1, entries.size / 6)
        points.forEachIndexed { index, point ->
            drawCircle(
                color = moodColors[entries[index].mood - 1],
                radius = 6.dp.toPx(),
                center = point
            )
            drawCircle(
                color = Color.Black,
                radius = 6.dp.toPx(),
                center = point,
                style = Stroke(width = 1.dp.toPx())
            )
            if (index % labelEvery == 0 || index == points.size - 1) {
                val label = entries[index].date.format(dateFormatter)
                val labelWidth = textMeasurer.measure(label, style = axisLabelStyle).size.width
                val x = (point.x - labelWidth / 2f).coerceIn(0f, size.width - labelWidth)
                drawText(
                    textMeasurer = textMeasurer,
                    text = label,
                    topLeft = Offset(x, size.height - bottomPadding + 4.dp.toPx()),
                    style = axisLabelStyle
                )
            }
        }
    }
}