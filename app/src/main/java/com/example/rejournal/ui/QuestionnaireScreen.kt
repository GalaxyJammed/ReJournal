package com.example.rejournal.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Mic
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.rejournal.data.ActivityTagsPrefs
import com.example.rejournal.data.MediaFileHelper
import com.example.rejournal.data.MoodEntry
import java.io.File
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
            note = entry.note
            photoPaths = entry.photoPaths
            audioPaths = entry.audioPaths
        }
        hasLoaded = true
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
                Text("Tip: hold a tag to delete it", style = MaterialTheme.typography.labelSmall)
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

@Composable
private fun PhotosSection(
    photoPaths: List<String>,
    onAddClick: () -> Unit,
    onRemove: (String) -> Unit
) {
    Column {
        Text("Photos", style = MaterialTheme.typography.titleMedium)
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
        Text("Voice Memos", style = MaterialTheme.typography.titleMedium)
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