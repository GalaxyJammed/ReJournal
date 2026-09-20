package com.example.rejournal.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.rejournal.ui.components.ButterflyCardWrapper
import com.example.rejournal.data.MoodEntry
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteDaysScreen(
    viewModel: MoodViewModel,
    onDayClick: (LocalDate) -> Unit,
    onBack: () -> Unit
) {
    val entries by viewModel.allEntries.collectAsState()
    val favorites = remember(entries) { entries.filter { it.isFavorite }.sortedByDescending { it.date } }

    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("Favorite Days") }, navigationIcon = { BackButton(onBack) }) }
    ) { padding: PaddingValues ->
        if (favorites.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                Text("No favorite days yet. Tap the heart icon while logging a day to add one.")
            }
        } else {
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScrollbar(scrollState)
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                favorites.forEachIndexed { idx, entry ->
                    ButterflyCardWrapper(seed = "FavDay_${entry.date}", indexOffset = idx, modifier = Modifier.fillMaxWidth()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = softCardShape,
                            border = softCardBorder(),
                            onClick = { onDayClick(entry.date) }
                        ) {
                        androidx.compose.foundation.layout.Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                        ) {
                            Column {
                                Text("${entry.date}", style = MaterialTheme.typography.titleMedium)
                                if (entry.note.isNotBlank()) {
                                    Text(entry.note, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                            Icon(Icons.Filled.Favorite, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
                }
            }
        }
    }
}