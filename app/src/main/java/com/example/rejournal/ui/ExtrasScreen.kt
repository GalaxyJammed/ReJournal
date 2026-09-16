package com.example.rejournal.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.filled.Star

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExtrasScreen(
    onGoalsClick: () -> Unit,
    onPhotoAlbumClick: () -> Unit,
    onVoiceMemoAlbumClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onImportantDaysClick: () -> Unit
) {
    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = { TopAppBar(title = { Text("Extras") }) }
    ) { padding: PaddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = onGoalsClick,
                modifier = Modifier.fillMaxWidth().height(64.dp)
            ) {
                Icon(Icons.Filled.EmojiEvents, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                Text("Goals", style = MaterialTheme.typography.titleMedium)
            }
            Button(
                onClick = onPhotoAlbumClick,
                modifier = Modifier.fillMaxWidth().height(64.dp)
            ) {
                Icon(Icons.Filled.PhotoLibrary, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                Text("Photo Album", style = MaterialTheme.typography.titleMedium)
            }
            Button(
                onClick = onVoiceMemoAlbumClick,
                modifier = Modifier.fillMaxWidth().height(64.dp)
            ) {
                Icon(Icons.Filled.Mic, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                Text("Voice Memos", style = MaterialTheme.typography.titleMedium)
            }
            Button(
                onClick = onImportantDaysClick,
                modifier = Modifier.fillMaxWidth().height(64.dp)
            ) {
                Icon(Icons.Filled.Star, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                Text("Important Days", style = MaterialTheme.typography.titleMedium)
            }
            Button(
                onClick = onSettingsClick,
                modifier = Modifier.fillMaxWidth().height(64.dp)
            ) {
                Icon(Icons.Filled.Settings, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                Text("Settings", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}