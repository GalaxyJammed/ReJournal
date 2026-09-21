package com.example.rejournal.ui

import com.example.rejournal.ui.components.PastelIcon
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import com.example.rejournal.data.MoodDisplayMode
import com.example.rejournal.ui.theme.MoodVisualsState
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.WorkOutline
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.rejournal.data.ActivityIcons
import com.example.rejournal.data.ActivityTagsPrefs
import com.example.rejournal.data.ImageSaveHelper
import com.example.rejournal.data.MoodEntry
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.ui.graphics.Brush

private const val TOP_TAG_COUNT = 3

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
    var tagsExpanded by remember { mutableStateOf(false) }
    var showConstellation by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var constellationRange by remember { mutableStateOf<Pair<LocalDate, LocalDate>?>(null) }
    var showFullMonth by remember { mutableStateOf(false) }

    val tagUsageCounts = remember(entries) {
        entries.flatMap { it.activities }.groupingBy { it }.eachCount()
    }
    val topTags = remember(availableTags, tagUsageCounts) {
        availableTags.sortedByDescending { tagUsageCounts[it] ?: 0 }.take(TOP_TAG_COUNT)
    }
    val collapsedTags = remember(topTags, selectedTags) {
        (topTags + selectedTags.filter { it in availableTags }).distinct()
    }
    val remainingTags = remember(availableTags, collapsedTags) {
        availableTags.filterNot { it in collapsedTags }
    }

    val hasFilters = selectedTags.isNotEmpty() ||
            energyFilter != null || productivityFilter != null || stressFilter != null || sleepFilter != null

    val allFilteredEntries = remember(entries, energyFilter, productivityFilter, stressFilter, sleepFilter, selectedTags) {
        entries.filter { entry ->
            (energyFilter == null || entry.energy == energyFilter) &&
                    (productivityFilter == null || entry.productivity == productivityFilter) &&
                    (stressFilter == null || entry.stress == stressFilter) &&
                    (sleepFilter == null || entry.sleep == sleepFilter) &&
                    (selectedTags.isEmpty() || selectedTags.all { it in entry.activities })
        }.sortedBy { it.date }
    }

    val currentMonthStart = LocalDate.now().withDayOfMonth(1)
    val monthFilteredEntries = remember(allFilteredEntries) {
        val now = LocalDate.now()
        allFilteredEntries.filter { entry ->
            !entry.date.isBefore(currentMonthStart) && !entry.date.isAfter(now)
        }
    }

    val miniChartEntries = remember(allFilteredEntries) {
        allFilteredEntries.takeLast(7)
    }

    Scaffold(
        containerColor = Color.Transparent,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Mood Trend") },
                actions = {
                    if (hasFilters) {
                        TextButton(onClick = {
                            selectedTags = emptySet()
                            energyFilter = null
                            productivityFilter = null
                            stressFilter = null
                            sleepFilter = null
                        }) {
                            Text("Clear")
                        }
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (miniChartEntries.size < 2) {
                Text(
                    if (hasFilters) "Not enough matching days this month to draw a trend." else "Log at least 2 days this month to see a trend line.",
                    modifier = Modifier.padding(vertical = 24.dp)
                )
            } else {
                Text(
                    "Click to view more",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .clickable { showFullMonth = true }
                ) {
                    MoodLineChart(entries = miniChartEntries)
                }
            }

            OutlinedButton(
                onClick = { showDatePicker = true },
                modifier = Modifier.fillMaxWidth(),
                enabled = entries.size >= 2
            ) {
                Text("Create Constellation")
            }

            TrendSliderFilter(icon = Icons.Filled.TrendingUp, label = "Energy", value = energyFilter, onValueChange = { energyFilter = it })
            TrendSliderFilter(icon = Icons.Filled.WorkOutline, label = "Productivity", value = productivityFilter, onValueChange = { productivityFilter = it })
            TrendSliderFilter(icon = Icons.Filled.Psychology, label = "Stress", value = stressFilter, onValueChange = { stressFilter = it })
            TrendSliderFilter(icon = Icons.Filled.Hotel, label = "Sleep", value = sleepFilter, onValueChange = { sleepFilter = it })

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                    PastelIcon(Icons.Filled.DirectionsRun, contentDescription = null, modifier = Modifier.size(20.dp))
                    Text("Activities", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(start = 8.dp))
                }
                IconButton(onClick = { tagsExpanded = !tagsExpanded }) {
                    PastelIcon(
                        if (tagsExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                        contentDescription = if (tagsExpanded) "Show fewer tags" else "Show more tags"
                    )
                }
            }

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                collapsedTags.forEach { tag ->
                    FilterChip(
                        selected = tag in selectedTags,
                        onClick = {
                            selectedTags = if (tag in selectedTags) selectedTags - tag else selectedTags + tag
                        },
                        label = { Text(tag) },
                        leadingIcon = {
                            PastelIcon(
                                ActivityIcons.resolve(tag, ActivityTagsPrefs.getIconIdForTag(context, tag)),
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    )
                }

                if (tagsExpanded) {
                    remainingTags.forEach { tag ->
                        FilterChip(
                            selected = tag in selectedTags,
                            onClick = {
                                selectedTags = if (tag in selectedTags) selectedTags - tag else selectedTags + tag
                            },
                            label = { Text(tag) },
                            leadingIcon = {
                                PastelIcon(
                                    ActivityIcons.resolve(tag, ActivityTagsPrefs.getIconIdForTag(context, tag)),
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        )
                    }
                }
            }
        }
    }

    if (showFullMonth) {
        FullMonthTrendView(
            entries = monthFilteredEntries,
            onExit = { showFullMonth = false }
        )
    }

    if (showDatePicker) {
        val dateRangePickerState = rememberDateRangePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                val start = dateRangePickerState.selectedStartDateMillis
                val end = dateRangePickerState.selectedEndDateMillis
                val isValidRange = if (start != null && end != null) {
                    val startDate = Instant.ofEpochMilli(start).atZone(ZoneOffset.UTC).toLocalDate()
                    val endDate = Instant.ofEpochMilli(end).atZone(ZoneOffset.UTC).toLocalDate()
                    val days = ChronoUnit.DAYS.between(startDate, endDate)
                    days in 0L..9L
                } else false

                TextButton(
                    onClick = {
                        if (start != null && end != null) {
                            constellationRange = Pair(
                                Instant.ofEpochMilli(start).atZone(ZoneOffset.UTC).toLocalDate(),
                                Instant.ofEpochMilli(end).atZone(ZoneOffset.UTC).toLocalDate()
                            )
                            showDatePicker = false
                            showConstellation = true
                        }
                    },
                    enabled = isValidRange
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DateRangePicker(
                state = dateRangePickerState,
                title = {
                    Column(modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp)) {
                        Text("Select up to 10 days", style = MaterialTheme.typography.titleMedium)
                        val start = dateRangePickerState.selectedStartDateMillis
                        val end = dateRangePickerState.selectedEndDateMillis
                        if (start != null && end == null) {
                            val target = Instant.ofEpochMilli(start).atZone(ZoneOffset.UTC).toLocalDate().plusDays(9)
                            Text(
                                "10-day target: ${target.format(DateTimeFormatter.ofPattern("MMM d"))}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                },
                modifier = Modifier.weight(1f)
            )
        }
    }

    if (showConstellation && constellationRange != null) {
        val (start, end) = constellationRange!!
        val filteredEntries = entries.filter { !it.date.isBefore(start) && !it.date.isAfter(end) }
            .sortedBy { it.date }

        ConstellationView(
            entries = filteredEntries,
            startDate = start,
            endDate = end,
            onExit = { showConstellation = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FullMonthTrendView(
    entries: List<MoodEntry>,
    onExit: () -> Unit
) {
    Dialog(
        onDismissRequest = onExit,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Monthly Trend") },
                    navigationIcon = {
                        IconButton(onClick = onExit) {
                            PastelIcon(Icons.Default.Close, contentDescription = "Close")
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        ) { padding ->
            if (entries.size < 2) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Not enough logged days this month.")
                }
            } else {
                val scrollState = rememberScrollState()
                val dayWidth = 72.dp
                val chartWidth = (dayWidth * entries.size).coerceAtLeast(400.dp)

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(vertical = 32.dp)
                        .horizontalScroll(scrollState)
                ) {
                    Box(
                        modifier = Modifier
                            .width(chartWidth)
                            .fillMaxHeight()
                            .padding(horizontal = 24.dp)
                    ) {
                        MoodLineChart(entries = entries, isExpanded = true)
                    }
                }
            }
        }
    }
}

@Composable
private fun TrendSliderFilter(
    icon: ImageVector,
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                PastelIcon(icon, contentDescription = null, modifier = Modifier.size(20.dp))
                Text(label, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(start = 8.dp))
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(value?.toString() ?: "Off", style = MaterialTheme.typography.titleMedium)
                if (value != null) {
                    IconButton(
                        onClick = { onValueChange(null) },
                        modifier = Modifier.size(20.dp)
                    ) {
                        PastelIcon(
                            Icons.Filled.Close,
                            contentDescription = "Clear $label filter",
                            modifier = Modifier.size(14.dp)
                        )
                    }
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
private fun MoodLineChart(entries: List<MoodEntry>, isExpanded: Boolean = false) {
    val moodColors = moodColorList()
    val textMeasurer = rememberTextMeasurer()
    val dateFormatter = DateTimeFormatter.ofPattern("d")
    val axisLabelStyle = TextStyle(fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    val mode by MoodVisualsState.mode
    val emojis by MoodVisualsState.emojis
    val surfaceColor = MaterialTheme.colorScheme.surface
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface

    val firstDate = entries.first().date
    val lastDate = entries.last().date
    val totalDaySpan = ChronoUnit.DAYS.between(firstDate, lastDate).toFloat()

    Canvas(modifier = Modifier.fillMaxSize()) {
        val leftPadding = 48.dp.toPx()
        val bottomPadding = 32.dp.toPx()
        val topPadding = 16.dp.toPx()
        val chartWidth = size.width - leftPadding - 16.dp.toPx()
        val chartHeight = size.height - bottomPadding - topPadding

        for (mood in 1..5) {
            val y = topPadding + chartHeight * (1f - (mood - 1) / 4f)
            drawLine(
                color = Color.LightGray.copy(alpha = 0.3f),
                start = Offset(leftPadding, y),
                end = Offset(size.width, y),
                strokeWidth = 1.dp.toPx()
            )

            if (mode == MoodDisplayMode.EMOJI) {
                val emoji = emojis[mood - 1]
                val layout = textMeasurer.measure(emoji, style = TextStyle(fontSize = 16.sp))
                drawText(
                    textMeasurer = textMeasurer,
                    text = emoji,
                    topLeft = Offset(leftPadding - layout.size.width - 8.dp.toPx(), y - layout.size.height / 2f),
                    style = TextStyle(fontSize = 16.sp)
                )
            } else {
                drawCircle(
                    color = moodColors[mood - 1],
                    radius = 6.dp.toPx(),
                    center = Offset(leftPadding - 16.dp.toPx(), y)
                )
            }
        }

        val points = entries.map { entry ->
            val daysFromStart = ChronoUnit.DAYS.between(firstDate, entry.date).toFloat()
            val fraction = if (totalDaySpan > 0f) daysFromStart / totalDaySpan else 0f
            Offset(leftPadding + chartWidth * fraction, topPadding + chartHeight * (1f - (entry.mood - 1) / 4f))
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

        val labelEvery = maxOf(1, entries.size / 8)
        points.forEachIndexed { index, point ->
            drawCircle(color = moodColors[entries[index].mood - 1], radius = 5.dp.toPx(), center = point)
            drawCircle(color = Color.White, radius = 5.dp.toPx(), center = point, style = Stroke(width = 2.dp.toPx()))

            if (isExpanded) {
                val label = entries[index].date.format(dateFormatter)
                val labelLayout = textMeasurer.measure(label, style = axisLabelStyle.copy(color = onSurfaceColor))
                
                val labelX = point.x - labelLayout.size.width / 2f
                val labelY = point.y + 12.dp.toPx()

                val paddingH = 6.dp.toPx()
                val paddingV = 2.dp.toPx()
                val bgRect = Rect(
                    left = labelX - paddingH,
                    top = labelY - paddingV,
                    right = labelX + labelLayout.size.width + paddingH,
                    bottom = labelY + labelLayout.size.height + paddingV
                )
                
                drawRoundRect(
                    color = surfaceColor.copy(alpha = 0.85f),
                    topLeft = bgRect.topLeft,
                    size = bgRect.size,
                    cornerRadius = CornerRadius(4.dp.toPx())
                )

                drawLine(
                    color = onSurfaceColor.copy(alpha = 0.2f),
                    start = Offset(point.x, point.y + 5.dp.toPx()),
                    end = Offset(point.x, labelY),
                    strokeWidth = 1.dp.toPx()
                )

                drawText(
                    textMeasurer = textMeasurer,
                    text = label,
                    topLeft = Offset(labelX, labelY),
                    style = axisLabelStyle.copy(color = onSurfaceColor)
                )
            } else if (index % labelEvery == 0 || index == points.size - 1) {
                val label = entries[index].date.format(dateFormatter)
                val labelLayout = textMeasurer.measure(label, style = axisLabelStyle)
                val x = (point.x - labelLayout.size.width / 2f)
                drawText(
                    textMeasurer = textMeasurer,
                    text = label,
                    topLeft = Offset(x, size.height - bottomPadding + 8.dp.toPx()),
                    style = axisLabelStyle
                )
            }
        }
    }
}

private data class BackgroundStar(val xFraction: Float, val yFraction: Float, val radius: Float, val alpha: Float)

@Composable
private fun ConstellationView(
    entries: List<MoodEntry>,
    startDate: LocalDate,
    endDate: LocalDate,
    onExit: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val graphicsLayer = rememberGraphicsLayer()

    Dialog(
        onDismissRequest = onExit,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .drawWithContent {
                        graphicsLayer.record {
                            this@drawWithContent.drawContent()
                        }
                        drawLayer(graphicsLayer)
                    }
            ) {
                val backgroundStars = remember {
                    List(50) {
                        BackgroundStar(
                            xFraction = Random.nextFloat(),
                            yFraction = Random.nextFloat(),
                            radius = Random.nextFloat() * 2f + 1f,
                            alpha = Random.nextFloat() * 0.5f + 0.2f
                        )
                    }
                }

                Canvas(modifier = Modifier.fillMaxSize()) {
                    backgroundStars.forEach { star ->
                        drawCircle(
                            color = Color.White.copy(alpha = star.alpha),
                            radius = star.radius.dp.toPx(),
                            center = Offset(star.xFraction * size.width, star.yFraction * size.height)
                        )
                    }
                }

                ConstellationChart(
                    entries = entries,
                    startDate = startDate,
                    endDate = endDate
                )
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(32.dp)
                    .navigationBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    "Your Mood Constellation",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White
                )
                Text(
                    "(More activity = bigger star)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.7f)
                )
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedButton(
                        onClick = onExit,
                        border = BorderStroke(1.dp, Color.White),
                        shape = CircleShape
                    ) {
                        Text("Close", color = Color.White)
                    }
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                try {
                                    val bitmap = graphicsLayer.toImageBitmap().asAndroidBitmap()
                                    val success = ImageSaveHelper.saveToGallery(context, bitmap, "mood_constellation_${LocalDate.now()}")
                                    Toast.makeText(context, if (success) "Saved to gallery!" else "Failed to save", Toast.LENGTH_SHORT).show()
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Error saving: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        shape = CircleShape
                    ) {
                        Text("Save Image")
                    }
                }
            }
        }
    }
}

@Composable
private fun ConstellationChart(
    entries: List<MoodEntry>,
    startDate: LocalDate,
    endDate: LocalDate
) {
    val totalDaySpan = ChronoUnit.DAYS.between(startDate, endDate).toFloat()
    val textMeasurer = rememberTextMeasurer()
    val dateStyle = TextStyle(color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)

    Canvas(modifier = Modifier.fillMaxSize()) {
        val verticalPadding = 64.dp.toPx()
        val chartHeight = size.height - verticalPadding * 2

        val moodSpanWidth = size.width * 0.6f
        val horizontalOffset = (size.width - moodSpanWidth) / 2f

        val moodLabelStyle = TextStyle(color = Color.White.copy(alpha = 0.3f), fontSize = 10.sp)
        for (mood in 1..5) {
            val x = horizontalOffset + moodSpanWidth * ((mood - 1) / 4f)
            drawLine(
                color = Color.White.copy(alpha = 0.15f),
                start = Offset(x, 0f),
                end = Offset(x, size.height),
                strokeWidth = 1.dp.toPx()
            )
            drawText(
                textMeasurer = textMeasurer,
                text = mood.toString(),
                topLeft = Offset(x - 4.dp.toPx(), 8.dp.toPx()),
                style = moodLabelStyle
            )
        }

        val labelEvery = maxOf(1, entries.size / 10)
        
        val points = entries.map { entry ->
            val daysFromStart = ChronoUnit.DAYS.between(startDate, entry.date).toFloat()
            val fraction = if (totalDaySpan > 0f) daysFromStart / totalDaySpan else 0.5f
            val y = verticalPadding + chartHeight * fraction

            val x = horizontalOffset + moodSpanWidth * ((entry.mood - 1) / 4f)
            Offset(x, y)
        }

        for (i in 0 until points.size - 1) {
            drawLine(
                color = Color.White.copy(alpha = 0.4f),
                start = points[i],
                end = points[i + 1],
                strokeWidth = 1.5.dp.toPx()
            )
        }

        points.forEachIndexed { index, point ->
            val entry = entries[index]

            if (index % labelEvery == 0 || index == points.size - 1) {
                val label = entry.date.format(DateTimeFormatter.ofPattern("MMM d"))
                drawText(
                    textMeasurer = textMeasurer,
                    text = label,
                    topLeft = Offset(16.dp.toPx(), point.y - 8.dp.toPx()),
                    style = dateStyle
                )
            }

            val sliderAverage = (entry.energy + entry.productivity + entry.stress + entry.sleep) / 4f
            val sliderFactor = ((sliderAverage - 1f) / 4f).coerceIn(0f, 1f)
            val activityFactor = (entry.activities.size / 5f).coerceIn(0f, 1f)

            val rawEffort = (sliderFactor * 0.5f) + (activityFactor * 0.5f)
            val effort = rawEffort * rawEffort

            val outerRadius = (5 + effort * 18).dp.toPx()
            val innerRadius = outerRadius * 0.45f

            drawCircle(color = Color.White.copy(alpha = 0.08f), radius = outerRadius * 2.5f, center = point)
            drawCircle(color = Color.White.copy(alpha = 0.15f), radius = outerRadius * 1.5f, center = point)

            drawPath(path = starPath(point, outerRadius, innerRadius), color = Color.White)
        }
    }
}

private fun starPath(center: Offset, outerRadius: Float, innerRadius: Float): Path {
    val path = Path()
    val angleStep = Math.PI / 5
    for (i in 0 until 10) {
        val radius = if (i % 2 == 0) outerRadius else innerRadius
        val angle = -Math.PI / 2 + i * angleStep
        val x = center.x + (radius * cos(angle)).toFloat()
        val y = center.y + (radius * sin(angle)).toFloat()
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    return path
}