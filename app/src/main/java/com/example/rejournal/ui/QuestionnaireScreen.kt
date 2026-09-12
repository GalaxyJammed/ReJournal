package com.example.rejournal.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.rejournal.data.ActivityTagsPrefs
import com.example.rejournal.data.MoodEntry
import java.time.LocalDate

private val moodEmojis = listOf("😞", "😕", "😐", "🙂", "😄")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionnaireScreen(
    viewModel: MoodViewModel,
    date: LocalDate,
    onDone: () -> Unit
) {
    val context = LocalContext.current

    var existingEntry by remember { mutableStateOf<MoodEntry?>(null) }
    var hasLoaded by remember { mutableStateOf(false) }

    var selectedMood by remember { mutableStateOf<Int?>(null) }
    var energy by remember { mutableStateOf(3f) }
    var productivity by remember { mutableStateOf(3f) }
    var stress by remember { mutableStateOf(3f) }
    var sleep by remember { mutableStateOf(3f) }
    var selectedActivities by remember { mutableStateOf(setOf<String>()) }
    var note by remember { mutableStateOf("") }
    var availableTags by remember { mutableStateOf(ActivityTagsPrefs.getAllTags(context)) }
    var showAddTagDialog by remember { mutableStateOf(false) }
    var newTagText by remember { mutableStateOf("") }
    var tagPendingDeletion by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(date) {
        val entry = viewModel.getEntryForDate(date)
        existingEntry = entry
        if (entry != null) {
            selectedMood = entry.mood
            energy = entry.energy.toFloat()
            productivity = entry.productivity.toFloat()
            stress = entry.stress.toFloat()
            sleep = entry.sleep.toFloat()
            selectedActivities = entry.activities.toSet()
            note = entry.note
        }
        hasLoaded = true
    }

    if (!hasLoaded) return

    Scaffold(
        topBar = { TopAppBar(title = { Text("$date") }) }
    ) { padding: PaddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text("Mood", style = MaterialTheme.typography.titleMedium)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                moodEmojis.forEachIndexed { index, emoji ->
                    val moodValue = index + 1
                    FilterChip(
                        selected = selectedMood == moodValue,
                        onClick = { selectedMood = moodValue },
                        label = { Text(emoji, style = MaterialTheme.typography.headlineSmall) }
                    )
                }
            }

            SliderRow(label = "Energy", value = energy, onValueChange = { energy = it })
            SliderRow(label = "Productivity", value = productivity, onValueChange = { productivity = it })
            SliderRow(label = "Stress", value = stress, onValueChange = { stress = it })
            SliderRow(label = "Sleep", value = sleep, onValueChange = { sleep = it })

            Column {
                Text("What did you do today?", style = MaterialTheme.typography.titleMedium)
                Text(
                    "Tip: hold a tag to delete it",
                    style = MaterialTheme.typography.labelSmall
                )
            }
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(availableTags) { activity ->
                    DeletableActivityChip(
                        label = activity,
                        selected = activity in selectedActivities,
                        onClick = {
                            selectedActivities = if (activity in selectedActivities) {
                                selectedActivities - activity
                            } else {
                                selectedActivities + activity
                            }
                        },
                        onLongClick = { tagPendingDeletion = activity }
                    )
                }
                item {
                    FilterChip(
                        selected = false,
                        onClick = { showAddTagDialog = true },
                        label = { Text("Add") },
                        leadingIcon = { Icon(Icons.Filled.Add, contentDescription = "Add custom tag") }
                    )
                }
            }

            Text("Notes (optional)", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                placeholder = { Text("Anything on your mind...") }
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (existingEntry != null) {
                    Button(
                        onClick = {
                            viewModel.deleteEntry(existingEntry!!)
                            onDone()
                        },
                        modifier = Modifier.weight(1f)
                    ) { Text("Delete") }
                }

                Button(
                    onClick = onDone,
                    modifier = Modifier.weight(1f)
                ) { Text("Cancel") }

                Button(
                    onClick = {
                        selectedMood?.let { mood ->
                            viewModel.saveEntry(
                                date = date,
                                mood = mood,
                                activities = selectedActivities.toList(),
                                note = note,
                                energy = energy.toInt(),
                                productivity = productivity.toInt(),
                                stress = stress.toInt(),
                                sleep = sleep.toInt()
                            )
                            onDone()
                        }
                    },
                    enabled = selectedMood != null,
                    modifier = Modifier.weight(1f)
                ) { Text("Save") }
            }
        }
    }

    if (showAddTagDialog) {
        AlertDialog(
            onDismissRequest = { showAddTagDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    val tag = newTagText.trim()
                    if (tag.isNotEmpty()) {
                        ActivityTagsPrefs.addCustomTag(context, tag)
                        availableTags = ActivityTagsPrefs.getAllTags(context)
                        selectedActivities = selectedActivities + tag
                    }
                    newTagText = ""
                    showAddTagDialog = false
                }) { Text("Add") }
            },
            dismissButton = {
                TextButton(onClick = {
                    newTagText = ""
                    showAddTagDialog = false
                }) { Text("Cancel") }
            },
            title = { Text("New activity tag") },
            text = {
                OutlinedTextField(
                    value = newTagText,
                    onValueChange = { newTagText = it },
                    placeholder = { Text("e.g. Meditation") },
                    singleLine = true
                )
            }
        )
    }

    tagPendingDeletion?.let { tag ->
        AlertDialog(
            onDismissRequest = { tagPendingDeletion = null },
            confirmButton = {
                TextButton(onClick = {
                    ActivityTagsPrefs.removeTag(context, tag)
                    viewModel.removeTagEverywhere(tag)
                    availableTags = ActivityTagsPrefs.getAllTags(context)
                    selectedActivities = selectedActivities - tag
                    tagPendingDeletion = null
                }) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { tagPendingDeletion = null }) { Text("Cancel") }
            },
            title = { Text("Delete \"$tag\"?") },
            text = { Text("This removes it from your list of options AND from every day you've already tagged with it. This can't be undone.") }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun DeletableActivityChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val containerColor = if (selected) {
        MaterialTheme.colorScheme.secondaryContainer
    } else {
        MaterialTheme.colorScheme.surface
    }
    val borderColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = containerColor,
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
        modifier = Modifier.combinedClickable(
            onClick = onClick,
            onLongClick = onLongClick
        )
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Composable
private fun SliderRow(label: String, value: Float, onValueChange: (Float) -> Unit) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, style = MaterialTheme.typography.titleMedium)
            Text(value.toInt().toString(), style = MaterialTheme.typography.titleMedium)
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = 1f..5f,
            steps = 3
        )
    }
}