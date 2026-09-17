package com.example.rejournal.ui

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.WorkOutline
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
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
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.random.Random
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.ui.graphics.Path
import kotlin.math.cos
import kotlin.math.sin

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
    var showConstellation by remember { mutableStateOf(false) }

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
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Mood Trend (last 30 days)") },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (recentEntries.size < 2) {
                Text(
                    if (hasFilters) "Not enough matching days to draw a trend." else "Log at least 2 days to see a trend line.",
                    modifier = Modifier.padding(vertical = 24.dp)
                )
            } else {
                Box(modifier = Modifier.fillMaxWidth().height(280.dp)) {
                    MoodLineChart(entries = recentEntries)
                }
            }

            OutlinedButton(
                onClick = { showConstellation = true },
                modifier = Modifier.fillMaxWidth(),
                enabled = recentEntries.size >= 2
            ) {
                Text("Create Constellation")
            }

            TrendSliderFilter(icon = Icons.Filled.TrendingUp, label = "Energy", value = energyFilter, onValueChange = { energyFilter = it })
            TrendSliderFilter(icon = Icons.Filled.WorkOutline, label = "Productivity", value = productivityFilter, onValueChange = { productivityFilter = it })
            TrendSliderFilter(icon = Icons.Filled.Psychology, label = "Stress", value = stressFilter, onValueChange = { stressFilter = it })
            TrendSliderFilter(icon = Icons.Filled.Hotel, label = "Sleep", value = sleepFilter, onValueChange = { sleepFilter = it })

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.DirectionsRun, contentDescription = null, modifier = Modifier.size(20.dp))
                Text("Activities (all selected must match)", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(start = 8.dp))
            }
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(availableTags) { tag ->
                    FilterChip(
                        selected = tag in selectedTags,
                        onClick = {
                            selectedTags = if (tag in selectedTags) selectedTags - tag else selectedTags + tag
                        },
                        label = { Text(tag) },
                        leadingIcon = {
                            Icon(
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

    if (showConstellation) {
        ConstellationView(
            entries = recentEntries,
            onExit = { showConstellation = false }
        )
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
                Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp))
                Text(label, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(start = 8.dp))
            }
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
    val moodColors = moodColorList()
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
            drawLine(color = Color.LightGray, start = Offset(leftPadding, y), end = Offset(size.width, y), strokeWidth = 1.dp.toPx())
            drawText(textMeasurer = textMeasurer, text = mood.toString(), topLeft = Offset(0f, y - 8.dp.toPx()), style = axisLabelStyle)
        }

        val points = entries.map { entry ->
            val daysFromStart = java.time.temporal.ChronoUnit.DAYS.between(firstDate, entry.date).toFloat()
            val fraction = if (totalDaySpan > 0f) daysFromStart / totalDaySpan else 0f
            Offset(leftPadding + chartWidth * fraction, topPadding + chartHeight * (1f - (entry.mood - 1) / 4f))
        }

        for (i in 0 until points.size - 1) {
            val startColor = moodColors[entries[i].mood - 1]
            val endColor = moodColors[entries[i + 1].mood - 1]
            drawLine(
                brush = androidx.compose.ui.graphics.Brush.linearGradient(listOf(startColor, endColor), points[i], points[i + 1]),
                start = points[i], end = points[i + 1], strokeWidth = 3.dp.toPx()
            )
        }

        val labelEvery = maxOf(1, entries.size / 6)
        points.forEachIndexed { index, point ->
            drawCircle(color = moodColors[entries[index].mood - 1], radius = 6.dp.toPx(), center = point)
            drawCircle(color = Color.Black, radius = 6.dp.toPx(), center = point, style = Stroke(width = 1.dp.toPx()))
            if (index % labelEvery == 0 || index == points.size - 1) {
                val label = entries[index].date.format(dateFormatter)
                val labelWidth = textMeasurer.measure(label, style = axisLabelStyle).size.width
                val x = (point.x - labelWidth / 2f).coerceIn(0f, size.width - labelWidth)
                drawText(textMeasurer = textMeasurer, text = label, topLeft = Offset(x, size.height - bottomPadding + 4.dp.toPx()), style = axisLabelStyle)
            }
        }
    }
}

private data class BackgroundStar(val xFraction: Float, val yFraction: Float, val radius: Float, val alpha: Float)

@Composable
private fun ConstellationView(
    entries: List<MoodEntry>,
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

                ConstellationChart(entries = entries)
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
private fun ConstellationChart(entries: List<MoodEntry>) {
    val firstDate = entries.first().date
    val lastDate = entries.last().date
    val totalDaySpan = java.time.temporal.ChronoUnit.DAYS.between(firstDate, lastDate).toFloat()

    Canvas(modifier = Modifier.fillMaxSize()) {
        val horizontalPadding = 32.dp.toPx()
        val chartWidth = size.width - horizontalPadding * 2

        val constellationHeight = size.height * 0.35f
        val verticalOffset = (size.height - constellationHeight) / 2.2f

        val points = entries.map { entry ->
            val daysFromStart = java.time.temporal.ChronoUnit.DAYS.between(firstDate, entry.date).toFloat()
            val fraction = if (totalDaySpan > 0f) daysFromStart / totalDaySpan else 0f
            val x = horizontalPadding + chartWidth * fraction
            val y = verticalOffset + constellationHeight * (1f - (entry.mood - 1) / 4f)
            Offset(x, y)
        }

        for (i in 0 until points.size - 1) {
            drawLine(
                color = Color.White.copy(alpha = 0.5f),
                start = points[i],
                end = points[i + 1],
                strokeWidth = 1.5.dp.toPx()
            )
        }

        points.forEachIndexed { index, point ->
            val entry = entries[index]


            val sliderAverage = (entry.energy + entry.productivity + entry.stress + entry.sleep) / 4f
            val sliderFactor = ((sliderAverage - 1f) / 4f).coerceIn(0f, 1f)
            val activityFactor = (entry.activities.size / 5f).coerceIn(0f, 1f)

            val rawEffort = (sliderFactor * 0.5f) + (activityFactor * 0.5f)
            val effort = rawEffort * rawEffort

            val outerRadius = (4 + effort * 28).dp.toPx()
            val innerRadius = outerRadius * 0.45f

            drawCircle(color = Color.White.copy(alpha = 0.10f), radius = outerRadius * 2.5f, center = point)
            drawCircle(color = Color.White.copy(alpha = 0.20f), radius = outerRadius * 1.6f, center = point)
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
