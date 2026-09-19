package com.example.rejournal.ui

import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.widget.Toast
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.rejournal.data.AudioSaveHelper
import com.example.rejournal.data.MediaGalleryHelper
import com.example.rejournal.data.MediaItem
import java.io.File
import java.time.LocalDate
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceMemoAlbumScreen(
    viewModel: MoodViewModel,
    onGoToDay: (LocalDate) -> Unit,
    onBack: () -> Unit
) {
    val entries by viewModel.allEntries.collectAsState()
    val memos = remember(entries) { MediaGalleryHelper.allAudioMemos(entries) }

    var currentlyPlayingPath by remember { mutableStateOf<String?>(null) }
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }

    DisposableEffect(Unit) {
        onDispose { mediaPlayer?.release() }
    }

    fun stopPlayback() {
        mediaPlayer?.release()
        mediaPlayer = null
        currentlyPlayingPath = null
    }

    fun play(path: String) {
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

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = { CenterAlignedTopAppBar(title = { Text("Voice Memos") }, navigationIcon = { BackButton(onBack) }) }
    ) { padding: PaddingValues ->
        if (memos.isEmpty()) {
            Text(
                "No voice memos yet. Add some from a day's entry.",
                modifier = Modifier.padding(padding).padding(16.dp)
            )
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
                memos.forEach { item ->
                    VoiceMemoAlbumRow(
                        item = item,
                        isPlaying = currentlyPlayingPath == item.path,
                        onPlayToggle = {
                            if (currentlyPlayingPath == item.path) stopPlayback() else play(item.path)
                        },
                        onGoToDay = {
                            if (currentlyPlayingPath == item.path) stopPlayback()
                            onGoToDay(item.date)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun VoiceMemoAlbumRow(
    item: MediaItem,
    isPlaying: Boolean,
    onPlayToggle: () -> Unit,
    onGoToDay: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val durationMs = remember(item.path) {
        try {
            MediaMetadataRetriever().use { retriever ->
                retriever.setDataSource(item.path)
                retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull() ?: 0L
            }
        } catch (e: Exception) {
            0L
        }
    }
    val seconds = durationMs / 1000

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
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
                    Column {
                        Text("${seconds}s memo", style = MaterialTheme.typography.bodyMedium)
                        Text("${item.date}", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
            OutlinedButton(onClick = onGoToDay, modifier = Modifier.fillMaxWidth()) {
                Text("Go to day memo was taken")
            }
            OutlinedButton(
                onClick = {
                    scope.launch {
                        val saved = withContext(Dispatchers.IO) {
                            AudioSaveHelper.saveToDownloads(context, File(item.path), "rejournal_memo_${item.date}")
                        }
                        Toast.makeText(context, if (saved) "Saved to Files" else "Couldn't save memo", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Filled.Download, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                Text("Save to Files")
            }
        }
    }
}