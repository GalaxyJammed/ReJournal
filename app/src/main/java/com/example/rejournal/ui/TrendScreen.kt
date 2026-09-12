package com.example.rejournal.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.drawText
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.TextStyle
import com.example.rejournal.data.MoodEntry
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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
    val entries by viewModel.allEntries.collectAsState()

    val cutoff = LocalDate.now().minusDays(29)
    val recentEntries: List<MoodEntry> = entries
        .filter { !it.date.isBefore(cutoff) }
        .sortedBy { it.date }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Mood Trend (last 30 days)") }) }
    ) { padding: PaddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            if (recentEntries.size < 2) {
                Text(
                    "Log at least 2 days to see a trend line.",
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                MoodLineChart(entries = recentEntries)
            }
        }
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

    Canvas(
        modifier = Modifier
            .fillMaxSize()
    ) {
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
            drawLine(
                color = Color(0xFF6750A4),
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