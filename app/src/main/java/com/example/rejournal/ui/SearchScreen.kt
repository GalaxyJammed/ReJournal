package com.example.rejournal.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.filled.AddReaction
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.WorkOutline
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.rejournal.data.ActivityTagsPrefs
import com.example.rejournal.data.MoodEntry
import java.time.LocalDate
import androidx.compose.foundation.lazy.rememberLazyListState

private val moodEmojis = listOf("😞", "😕", "😐", "🙂", "😄")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: MoodViewModel,
    onResultClick: (LocalDate) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val entries by viewModel.allEntries.collectAsState()
    val availableTags = remember { ActivityTagsPrefs.getAllTags(context) }

    var selectedMoods by remember { mutableStateOf(setOf<Int>()) }
    var energyFilter by remember { mutableStateOf<Int?>(null) }
    var productivityFilter by remember { mutableStateOf<Int?>(null) }
    var stressFilter by remember { mutableStateOf<Int?>(null) }
    var sleepFilter by remember { mutableStateOf<Int?>(null) }
    var selectedTags by remember { mutableStateOf(setOf<String>()) }
    var favoritesOnly by remember { mutableStateOf(false) }

    val hasFilters = selectedMoods.isNotEmpty() ||
            selectedTags.isNotEmpty() ||
            favoritesOnly ||
            energyFilter != null ||
            productivityFilter != null ||
            stressFilter != null ||
            sleepFilter != null

    val results: List<MoodEntry> = remember(
        entries, selectedMoods, energyFilter, productivityFilter, stressFilter, sleepFilter, selectedTags, favoritesOnly
    ) {
        if (!hasFilters) {
            emptyList()
        } else {
            entries.filter { entry ->
                (selectedMoods.isEmpty() || entry.mood in selectedMoods) &&
                        (energyFilter == null || entry.energy == energyFilter) &&
                        (productivityFilter == null || entry.productivity == productivityFilter) &&
                        (stressFilter == null || entry.stress == stressFilter) &&
                        (sleepFilter == null || entry.sleep == sleepFilter) &&
                        (!favoritesOnly || entry.isFavorite) &&
                        (selectedTags.isEmpty() || selectedTags.all { it in entry.activities })
            }.sortedByDescending { it.date }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Search") },
                actions = {
                    if (hasFilters) {
                        TextButton(onClick = {
                            selectedMoods = emptySet()
                            selectedTags = emptySet()
                            favoritesOnly = false
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
        val listState = rememberLazyListState()
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding)
                .verticalScrollbar(listState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.AddReaction, contentDescription = null, modifier = Modifier.size(20.dp))
                        Text("Mood", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(start = 8.dp))
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        moodEmojis.forEachIndexed { index, emoji ->
                            val moodValue = index + 1
                            FilterChip(
                                selected = moodValue in selectedMoods,
                                onClick = {
                                    selectedMoods = if (moodValue in selectedMoods) {
                                        selectedMoods - moodValue
                                    } else {
                                        selectedMoods + moodValue
                                    }
                                },
                                label = { Text(emoji, style = MaterialTheme.typography.headlineSmall) }
                            )
                        }
                    }

                    FilterChip(
                        selected = favoritesOnly,
                        onClick = { favoritesOnly = !favoritesOnly },
                        label = { Text("Favorites") },
                        leadingIcon = {
                            Icon(
                                if (favoritesOnly) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    )

                    SingleValueFilter(icon = Icons.Filled.TrendingUp, label = "Energy", value = energyFilter, onValueChange = { energyFilter = it })
                    SingleValueFilter(icon = Icons.Filled.WorkOutline, label = "Productivity", value = productivityFilter, onValueChange = { productivityFilter = it })
                    SingleValueFilter(icon = Icons.Filled.Psychology, label = "Stress", value = stressFilter, onValueChange = { stressFilter = it })
                    SingleValueFilter(icon = Icons.Filled.Hotel, label = "Sleep", value = sleepFilter, onValueChange = { sleepFilter = it })

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
                                        com.example.rejournal.data.ActivityIcons.resolve(tag, com.example.rejournal.data.ActivityTagsPrefs.getIconIdForTag(context, tag)),
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            )
                        }
                    }

                    if (hasFilters) {
                        Text(
                            "${results.size} result${if (results.size == 1) "" else "s"}",
                            style = MaterialTheme.typography.titleMedium
                        )
                        if (results.isEmpty()) {
                            Text("No days match these filters.")
                        }
                    } else {
                        Text("Select a filter above to search your entries.")
                    }
                }
            }

            items(results) { entry ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onResultClick(entry.date) }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "${moodEmojis[entry.mood - 1]}  ${entry.date}",
                                style = MaterialTheme.typography.titleMedium
                            )
                            if (entry.isFavorite) {
                                Icon(Icons.Filled.Favorite, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                        if (entry.activities.isNotEmpty()) {
                            Text(
                                entry.activities.joinToString(", "),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        if (entry.note.isNotBlank()) {
                            Text(entry.note, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SingleValueFilter(
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