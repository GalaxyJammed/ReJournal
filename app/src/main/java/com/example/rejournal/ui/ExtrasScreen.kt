package com.example.rejournal.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Sync

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExtrasScreen(
    onGoalsClick: () -> Unit,
    onPhotoAlbumClick: () -> Unit,
    onVoiceMemoAlbumClick: () -> Unit,
    onImportantDaysClick: () -> Unit,
    onFavoriteDaysClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onTimeCapsulesClick: () -> Unit,
    onAchievementsClick: () -> Unit,
    onProfileClick: () -> Unit,
    onSyncClick: () -> Unit
) {
    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = { CenterAlignedTopAppBar(title = { Text("Extras") }) }
    ) { padding: PaddingValues ->
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScrollbar(scrollState)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            ExtrasCard {
                ExtrasRow(Icons.Filled.EmojiEvents, "Goals", onGoalsClick)
                HorizontalDivider()
                ExtrasRow(Icons.Filled.History, "Time Capsules", onTimeCapsulesClick)
                HorizontalDivider()
                ExtrasRow(Icons.Filled.MilitaryTech, "Achievements", onAchievementsClick)
            }

            ExtrasCard {
                ExtrasRow(Icons.Filled.PhotoLibrary, "Photo Album", onPhotoAlbumClick)
                HorizontalDivider()
                ExtrasRow(Icons.Filled.Mic, "Voice Memos", onVoiceMemoAlbumClick)
            }

            ExtrasCard {
                ExtrasRow(Icons.Filled.Star, "Important Days", onImportantDaysClick)
                HorizontalDivider()
                ExtrasRow(Icons.Filled.Favorite, "Favorite Days", onFavoriteDaysClick)
            }

            ExtrasCard {
                ExtrasRow(Icons.Filled.Person, "Profile", onProfileClick)
                HorizontalDivider()
                ExtrasRow(Icons.Filled.Settings, "Settings", onSettingsClick)
                HorizontalDivider()
                ExtrasRow(Icons.Filled.Sync, "Sync", onSyncClick)
            }
        }
    }
}

@Composable
private fun ExtrasCard(content: @Composable ColumnScope.() -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column { content() }
    }
}

@Composable
private fun ExtrasRow(icon: ImageVector, label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null)
            Text(label, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(start = 16.dp))
        }
        Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}