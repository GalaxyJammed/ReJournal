package com.example.rejournal.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.rejournal.data.MoodEntry
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PositiveMemoryScreen(
    entry: MoodEntry,
    onGoToDay: (LocalDate) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("A Positive Memory") }, navigationIcon = { BackButton(onBack) }) }
    ) { padding: PaddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val dayOfWeek = entry.date.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
            val formattedDate = entry.date.format(DateTimeFormatter.ofPattern("MMMM d, yyyy"))

            Text("$dayOfWeek, $formattedDate", style = MaterialTheme.typography.headlineSmall)

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    androidx.compose.foundation.layout.Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                        MoodGlyph(entry.mood, size = 28.dp)
                        Column(modifier = Modifier.padding(start = 8.dp)) {
                            Text(
                                "Energy ${entry.energy} · Productivity ${entry.productivity} · Stress ${entry.stress}",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                "Sleep ${entry.sleep}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                    if (entry.activities.isNotEmpty()) {
                        Text("Activities: ${entry.activities.joinToString(", ")}", style = MaterialTheme.typography.bodyMedium)
                    }
                    if (entry.note.isNotBlank()) {
                        Text("\"${entry.note}\"", style = MaterialTheme.typography.bodyLarge)
                    } else {
                        Text("No notes were written that day.", style = MaterialTheme.typography.bodySmall)
                    }
                    if (entry.photoPaths.isNotEmpty()) {
                        Text("📷 ${entry.photoPaths.size} photo(s) from this day", style = MaterialTheme.typography.bodySmall)
                    }
                    if (entry.audioPaths.isNotEmpty()) {
                        Text("🎙️ ${entry.audioPaths.size} voice memo(s) from this day", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            Button(onClick = { onGoToDay(entry.date) }, modifier = Modifier.fillMaxWidth()) {
                Text("Open This Day")
            }
        }
    }
}