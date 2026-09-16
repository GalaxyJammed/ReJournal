package com.example.rejournal.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.rejournal.data.MoodDetailStatsCalculator
import com.example.rejournal.data.moodEmojis
import java.time.format.TextStyle
import java.util.Locale
import androidx.compose.material3.CenterAlignedTopAppBar

private val moodLabels = listOf("Rough", "Meh", "Neutral", "Good", "Great")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoodDetailScreen(viewModel: MoodViewModel, moodValue: Int, onBack: () -> Unit) {
    val entries by viewModel.allEntries.collectAsState()
    val stats = remember(entries, moodValue) {
        MoodDetailStatsCalculator.calculate(entries, moodValue)
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            CenterAlignedTopAppBar(title = { Text("${moodEmojis[moodValue - 1]} ${moodLabels[moodValue - 1]} Days") }, navigationIcon = { BackButton(onBack) })
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
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "${stats.totalOccurrences} day${if (stats.totalOccurrences == 1) "" else "s"} total",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        "Across your entire history",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (stats.totalOccurrences == 0) {
                Text("No days logged with this mood yet.")
                return@Column
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Averages on these days", style = MaterialTheme.typography.titleMedium)
                    Text("Energy: ${String.format("%.1f", stats.averageEnergy)} / 5", style = MaterialTheme.typography.bodyMedium)
                    Text("Productivity: ${String.format("%.1f", stats.averageProductivity)} / 5", style = MaterialTheme.typography.bodyMedium)
                    Text("Stress: ${String.format("%.1f", stats.averageStress)} / 5", style = MaterialTheme.typography.bodyMedium)
                    Text("Sleep: ${String.format("%.1f", stats.averageSleep)} / 5", style = MaterialTheme.typography.bodyMedium)
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Most common day of week", style = MaterialTheme.typography.titleMedium)
                    Text(
                        stats.mostCommonDaysOfWeek.joinToString(", ") {
                            it.getDisplayName(TextStyle.FULL, Locale.getDefault())
                        }.ifEmpty { "—" },
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            if (stats.topTags.isNotEmpty()) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Most common activities", style = MaterialTheme.typography.titleMedium)
                        stats.topTags.forEach { tagFreq ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(tagFreq.tag, style = MaterialTheme.typography.bodyMedium)
                                Text("${tagFreq.count}x", style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }
        }
    }
}