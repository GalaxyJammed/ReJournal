package com.example.rejournal.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.media.MediaRecorder
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.OpenInFull
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import com.example.rejournal.data.CharStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.rejournal.data.ActivityTagsPrefs
import com.example.rejournal.data.MediaFileHelper
import com.example.rejournal.data.MoodEntry
import java.io.File
import java.time.LocalDate
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.MoodBad
import androidx.compose.material.icons.filled.NoteAlt
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.WorkOutline
import androidx.compose.material.icons.filled.AddReaction
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.rejournal.data.ActivityIcons
import com.example.rejournal.data.moodEmojis

private const val TOP_TAG_COUNT = 3

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionnaireScreen(
    viewModel: MoodViewModel,
    date: LocalDate,
    onDone: () -> Unit
) {
    val context = LocalContext.current
    val allEntries by viewModel.allEntries.collectAsState()

    var existingEntry by remember { mutableStateOf<MoodEntry?>(null) }
    var hasLoaded by remember { mutableStateOf(false) }

    var selectedMood by remember { mutableStateOf<Int?>(null) }
    var energy by remember { mutableStateOf(3f) }
    var productivity by remember { mutableStateOf(3f) }
    var stress by remember { mutableStateOf(3f) }
    var sleep by remember { mutableStateOf(3f) }
    var selectedActivities by remember { mutableStateOf(setOf<String>()) }
    var noteValue by remember { mutableStateOf(RichNoteValue()) }
    var showExpandedNote by remember { mutableStateOf(false) }
    var availableTags by remember { mutableStateOf(ActivityTagsPrefs.getAllTags(context)) }
    var showAddTagDialog by remember { mutableStateOf(false) }
    var tagPendingDeletion by remember { mutableStateOf<String?>(null) }
    var tagsExpanded by remember { mutableStateOf(false) }

    var photoPaths by remember { mutableStateOf(listOf<String>()) }
    var audioPaths by remember { mutableStateOf(listOf<String>()) }
    var pendingPhotoFile by remember { mutableStateOf<File?>(null) }

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
            noteValue = richNoteValueFromRaw(entry.note)
            photoPaths = entry.photoPaths
            audioPaths = entry.audioPaths
        }
        hasLoaded = true
    }

    val tagUsageCounts = remember(allEntries) {
        allEntries.flatMap { it.activities }.groupingBy { it }.eachCount()
    }
    val topTags = remember(availableTags, tagUsageCounts) {
        availableTags.sortedByDescending { tagUsageCounts[it] ?: 0 }.take(TOP_TAG_COUNT)
    }
    val collapsedTags = remember(topTags, selectedActivities) {
        (topTags + selectedActivities.filter { it in availableTags }).distinct()
    }
    val remainingTags = remember(availableTags, collapsedTags) {
        availableTags.filterNot { it in collapsedTags }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        val file = pendingPhotoFile
        if (success && file != null) {
            photoPaths = photoPaths + file.absolutePath
        } else {
            file?.delete()
        }
        pendingPhotoFile = null
    }

    fun launchCamera() {
        val (file, uri) = MediaFileHelper.createPhotoFile(context)
        pendingPhotoFile = file
        cameraLauncher.launch(uri)
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
            SectionHeader(Icons.Filled.AddReaction, "Mood")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                (1..5).forEach { moodValue ->
                    FilterChip(
                        selected = selectedMood == moodValue,
                        onClick = { selectedMood = moodValue },
                        label = { MoodGlyph(moodValue) }
                    )
                }
            }

            SliderRow(icon = Icons.Filled.TrendingUp, label = "Energy", value = energy, onValueChange = { energy = it })
            SliderRow(icon = Icons.Filled.WorkOutline, label = "Productivity", value = productivity, onValueChange = { productivity = it })
            SliderRow(icon = Icons.Filled.Psychology, label = "Stress", value = stress, onValueChange = { stress = it })
            SliderRow(icon = Icons.Filled.Hotel, label = "Sleep", value = sleep, onValueChange = { sleep = it })

            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SectionHeader(Icons.Filled.DirectionsRun, "What did you do today?")
                    IconButton(onClick = { tagsExpanded = !tagsExpanded }) {
                        Icon(
                            if (tagsExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                            contentDescription = if (tagsExpanded) "Show fewer tags" else "Show more tags"
                        )
                    }
                }
                Text("Tip: hold a tag to delete it", style = MaterialTheme.typography.labelSmall)

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    collapsedTags.forEach { activity ->
                        DeletableActivityChip(
                            label = activity,
                            icon = ActivityIcons.resolve(activity, ActivityTagsPrefs.getIconIdForTag(context, activity)),
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
                }

                if (tagsExpanded) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        remainingTags.forEach { activity ->
                            DeletableActivityChip(
                                label = activity,
                                icon = ActivityIcons.resolve(activity, ActivityTagsPrefs.getIconIdForTag(context, activity)),
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
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                            modifier = Modifier.clickable { showAddTagDialog = true }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Icon(
                                    Icons.Filled.Add,
                                    contentDescription = "Add custom tag",
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    "Add",
                                    style = MaterialTheme.typography.labelLarge,
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            PhotosSection(
                photoPaths = photoPaths,
                onAddClick = { launchCamera() },
                onRemove = { path ->
                    MediaFileHelper.deleteFile(path)
                    photoPaths = photoPaths - path
                }
            )

            VoiceMemosSection(
                audioPaths = audioPaths,
                onMemoRecorded = { path -> audioPaths = audioPaths + path },
                onRemove = { path ->
                    MediaFileHelper.deleteFile(path)
                    audioPaths = audioPaths - path
                }
            )

            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SectionHeader(Icons.Filled.NoteAlt, "Notes (optional)")
                    TextButton(onClick = { showExpandedNote = true }) {
                        Icon(
                            Icons.Filled.OpenInFull,
                            contentDescription = null,
                            modifier = Modifier.padding(end = 4.dp).size(16.dp)
                        )
                        Text("Expand Note")
                    }
                }
                RichNoteEditor(
                    value = noteValue,
                    onValueChange = { noteValue = it },
                    minHeight = 120.dp
                )
            }

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
                                note = noteValue.toRawText(),
                                energy = energy.toInt(),
                                productivity = productivity.toInt(),
                                stress = stress.toInt(),
                                sleep = sleep.toInt(),
                                photoPaths = photoPaths,
                                audioPaths = audioPaths
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

    if (showExpandedNote) {
        Dialog(
            onDismissRequest = { showExpandedNote = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Edit Note") },
                        actions = {
                            TextButton(onClick = { showExpandedNote = false }) { Text("Done") }
                        }
                    )
                }
            ) { dialogPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(dialogPadding)
                        .padding(16.dp)
                ) {
                    RichNoteEditor(
                        value = noteValue,
                        onValueChange = { noteValue = it },
                        modifier = Modifier.fillMaxSize(),
                        fillAvailableSpace = true
                    )
                }
            }
        }
    }

    if (showAddTagDialog) {
        AddActivityDialog(
            onDismiss = { showAddTagDialog = false },
            onConfirm = { name, iconId ->
                if (name.isNotEmpty()) {
                    ActivityTagsPrefs.addCustomTag(context, name, iconId)
                    availableTags = ActivityTagsPrefs.getAllTags(context)
                    selectedActivities = selectedActivities + name
                }
                showAddTagDialog = false
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

@Composable
private fun PhotosSection(
    photoPaths: List<String>,
    onAddClick: () -> Unit,
    onRemove: (String) -> Unit
) {
    Column {
        SectionHeader(Icons.Filled.PhotoLibrary, "Photos")
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(top = 8.dp)
        ) {
            items(photoPaths) { path ->
                PhotoThumbnail(path = path, onRemove = { onRemove(path) })
            }
            if (photoPaths.size < MediaFileHelper.MAX_PHOTOS) {
                item {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                            .clickable(onClick = onAddClick),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.PhotoCamera, contentDescription = "Take photo")
                    }
                }
            }
        }
    }
}

@Composable
private fun PhotoThumbnail(path: String, onRemove: () -> Unit) {
    val bitmap = remember(path) {
        BitmapFactory.decodeFile(path)?.let { full ->
            Bitmap.createScaledBitmap(full, 200, 200, true)
        }
    }
    Box(modifier = Modifier.size(72.dp)) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Photo",
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
            )
        }
        Icon(
            Icons.Filled.Close,
            contentDescription = "Remove photo",
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(2.dp)
                .size(20.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                .clickable(onClick = onRemove)
                .padding(3.dp)
        )
    }
}

@Composable
private fun VoiceMemosSection(
    audioPaths: List<String>,
    onMemoRecorded: (String) -> Unit,
    onRemove: (String) -> Unit
) {
    val context = LocalContext.current
    var isRecording by remember { mutableStateOf(false) }
    var recorder by remember { mutableStateOf<MediaRecorder?>(null) }
    var pendingAudioFile by remember { mutableStateOf<File?>(null) }

    var currentlyPlayingPath by remember { mutableStateOf<String?>(null) }
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }

    val micPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) startRecording(context) { file, rec -> pendingAudioFile = file; recorder = rec; isRecording = true }
    }

    DisposableEffect(Unit) {
        onDispose {
            recorder?.release()
            mediaPlayer?.release()
        }
    }

    fun stopPlayback() {
        mediaPlayer?.release()
        mediaPlayer = null
        currentlyPlayingPath = null
    }

    fun playMemo(path: String) {
        stopPlayback()
        val player = MediaPlayer().apply {
            setDataSource(path)
            prepare()
            setOnCompletionListener { stopPlayback() }
            start()
        }
        mediaPlayer = player
        currentlyPlayingPath = path
    }

    Column {
        SectionHeader(Icons.Filled.Mic, "Voice Memos")
        Column(
            modifier = Modifier.padding(top = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            audioPaths.forEach { path ->
                VoiceMemoRow(
                    path = path,
                    isPlaying = currentlyPlayingPath == path,
                    onPlayToggle = {
                        if (currentlyPlayingPath == path) stopPlayback() else playMemo(path)
                    },
                    onRemove = {
                        if (currentlyPlayingPath == path) stopPlayback()
                        onRemove(path)
                    }
                )
            }

            if (audioPaths.size < MediaFileHelper.MAX_AUDIO_MEMOS) {
                Button(
                    onClick = {
                        if (isRecording) {
                            stopRecording(recorder)
                            recorder = null
                            isRecording = false
                            pendingAudioFile?.let { onMemoRecorded(it.absolutePath) }
                            pendingAudioFile = null
                        } else {
                            micPermissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        if (isRecording) Icons.Filled.Stop else Icons.Filled.Mic,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(if (isRecording) "Stop recording" else "Record a voice memo")
                }
            }
        }
    }
}

private fun startRecording(context: Context, onStarted: (File, MediaRecorder) -> Unit) {
    val file = MediaFileHelper.createAudioFile(context)
    val recorder = MediaRecorder(context).apply {
        setAudioSource(MediaRecorder.AudioSource.MIC)
        setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        setOutputFile(file.absolutePath)
        prepare()
        start()
    }
    onStarted(file, recorder)
}

private fun stopRecording(recorder: MediaRecorder?) {
    try {
        recorder?.stop()
    } catch (e: Exception) {
        // Recording was too short or failed.
    }
    recorder?.release()
}

@Composable
private fun VoiceMemoRow(
    path: String,
    isPlaying: Boolean,
    onPlayToggle: () -> Unit,
    onRemove: () -> Unit
) {
    val durationMs = remember(path) {
        try {
            MediaMetadataRetriever().use { retriever ->
                retriever.setDataSource(path)
                retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull() ?: 0L
            }
        } catch (e: Exception) {
            0L
        }
    }
    val seconds = durationMs / 1000

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onPlayToggle) {
                    Icon(
                        if (isPlaying) Icons.Filled.Stop else Icons.Filled.PlayArrow,
                        contentDescription = if (isPlaying) "Stop" else "Play"
                    )
                }
                Text("${seconds}s memo", style = MaterialTheme.typography.bodyMedium)
            }
            IconButton(onClick = onRemove) {
                Icon(Icons.Filled.Delete, contentDescription = "Delete memo")
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun DeletableActivityChip(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
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
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(start = 6.dp)
            )
        }
    }
}

@Composable
private fun SliderRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: Float, onValueChange: (Float) -> Unit) {
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

@Composable
fun SectionHeader(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp))
        Text(text, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(start = 8.dp))
    }
}