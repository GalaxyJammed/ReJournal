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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.rejournal.ui.components.ButterflyCardWrapper
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Quiz

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExtrasScreen(
    viewModel: MoodViewModel,
    onGoalsClick: () -> Unit,
    onPhotoAlbumClick: () -> Unit,
    onVoiceMemoAlbumClick: () -> Unit,
    onImportantDaysClick: () -> Unit,
    onFavoriteDaysClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onTimeCapsulesClick: () -> Unit,
    onAchievementsClick: () -> Unit,
    onProfileClick: () -> Unit,
    onSyncClick: () -> Unit,
    onAboutClick: () -> Unit,
    onTestsClick: () -> Unit
) {
    val activeGoalsCount by viewModel.activeGoalsCount.collectAsState()
    val timeCapsulesCount by viewModel.timeCapsulesCount.collectAsState()
    val photoAlbumCount by viewModel.photoAlbumCount.collectAsState()
    val voiceMemosCount by viewModel.voiceMemosCount.collectAsState()
    val importantDaysCount by viewModel.importantDaysCount.collectAsState()
    val favoriteDaysCount by viewModel.favoriteDaysCount.collectAsState()

    Scaffold(
        containerColor = Color.Transparent,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = { CenterAlignedTopAppBar(title = { Text("More") }) }
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
            ExtrasCard(titleSeed = "Goals", index = 0) {
                ExtrasRow(Icons.Filled.EmojiEvents, "Goals", onGoalsClick, count = activeGoalsCount)
                HorizontalDivider()
                ExtrasRow(Icons.Filled.History, "Time Capsules", onTimeCapsulesClick, count = timeCapsulesCount)
                HorizontalDivider()
                ExtrasRow(Icons.Filled.MilitaryTech, "Achievements", onAchievementsClick)
                HorizontalDivider()
                ExtrasRow(Icons.Filled.Quiz, "Tests", onTestsClick)
            }

            ExtrasCard(titleSeed = "Media", index = 1) {
                ExtrasRow(Icons.Filled.PhotoLibrary, "Photo Album", onPhotoAlbumClick, count = photoAlbumCount)
                HorizontalDivider()
                ExtrasRow(Icons.Filled.Mic, "Voice Memos", onVoiceMemoAlbumClick, count = voiceMemosCount)
            }

            ExtrasCard(titleSeed = "Favorites", index = 2) {
                ExtrasRow(Icons.Filled.Star, "Important Days", onImportantDaysClick, count = importantDaysCount)
                HorizontalDivider()
                ExtrasRow(Icons.Filled.Favorite, "Favorite Days", onFavoriteDaysClick, count = favoriteDaysCount)
            }

            ExtrasCard(titleSeed = "ProfileSync", index = 3) {
                ExtrasRow(Icons.Filled.Person, "Profile", onProfileClick)
                HorizontalDivider()
                ExtrasRow(Icons.Filled.Settings, "Settings", onSettingsClick)
                HorizontalDivider()
                ExtrasRow(Icons.Filled.Sync, "Sync", onSyncClick)
            }

            ExtrasCard(titleSeed = "About", index = 4) {
                ExtrasRow(Icons.Filled.Info, "About", onAboutClick)
            }
        }
    }
}

@Composable
private fun ExtrasCard(titleSeed: String = "Extras", index: Int = 0, content: @Composable ColumnScope.() -> Unit) {
    ButterflyCardWrapper(seed = titleSeed, indexOffset = index, modifier = Modifier.fillMaxWidth()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = softCardShape,
            border = softCardBorder()
        ) {
            Column { content() }
        }
    }
}

@Composable
private fun ExtrasRow(icon: ImageVector, label: String, onClick: () -> Unit, count: Int? = null) {
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
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (count != null) {
                Text(
                    text = "($count)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
            Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
