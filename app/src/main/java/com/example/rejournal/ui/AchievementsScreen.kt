package com.example.rejournal.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.rejournal.ui.components.ButterflyCardWrapper
import com.example.rejournal.ui.components.IconPill
import com.example.rejournal.ui.components.getPastelColor
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.rejournal.data.AchievementCalculator
import com.example.rejournal.data.AchievementDefinitions
import com.example.rejournal.data.AchievementGroup
import com.example.rejournal.data.AchievementPrefs
import com.example.rejournal.data.GoalProgressPrefs
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.drawscope.clipRect
import com.example.rejournal.ui.verticalScrollbar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AchievementsScreen(viewModel: MoodViewModel, onBack: () -> Unit) {
    val context = LocalContext.current
    val entries by viewModel.allEntries.collectAsState()
    val timeCapsules by viewModel.allTimeCapsules.collectAsState()
    val goalCompletions = remember(entries) { GoalProgressPrefs.totalCompletions(context) }

    var unlockedIds by remember { mutableStateOf(AchievementPrefs.getUnlockedIds(context)) }

    LaunchedEffect(entries, timeCapsules) {
        val newly = AchievementCalculator.computeNewlyUnlockedTierIds(context, entries, goalCompletions, timeCapsules.size)
        if (newly.isNotEmpty()) {
            AchievementPrefs.markUnlocked(context, newly)
            unlockedIds = AchievementPrefs.getUnlockedIds(context)
        }
    }

    val groupsCompleted = AchievementDefinitions.groups.count { group ->
        val value = AchievementCalculator.currentValue(group.metric, entries, goalCompletions, timeCapsules.size)
        AchievementCalculator.currentTierIndex(group, value) == group.tiers.lastIndex
    }

    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("Achievements") }, navigationIcon = { BackButton(onBack) }) }
    ) { padding: PaddingValues ->
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScrollbar(scrollState)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ButterflyCardWrapper(seed = "AchievementsHeader", modifier = Modifier.fillMaxWidth()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = softCardShape,
                    border = softCardBorder(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("$groupsCompleted / ${AchievementDefinitions.groups.size}", style = MaterialTheme.typography.headlineLarge)
                        Text("Fully Completed", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AchievementDefinitions.groups.forEachIndexed { index, group ->
                    val value = AchievementCalculator.currentValue(group.metric, entries, goalCompletions, timeCapsules.size)
                    val tierIndex = AchievementCalculator.currentTierIndex(group, value)
                    AchievementGroupRow(group = group, currentValue = value, tierIndex = tierIndex, index = index + 1)
                }
            }
        }
    }
}

@Composable
private fun AchievementGroupRow(group: AchievementGroup, currentValue: Int, tierIndex: Int, index: Int) {
    val isMaxed = tierIndex == group.tiers.lastIndex
    val completedTier = if (tierIndex >= 0) group.tiers[tierIndex] else null
    val nextTier = if (!isMaxed) group.tiers[tierIndex + 1] else null
    val headlineTier = nextTier ?: group.tiers.last()

    ButterflyCardWrapper(seed = "Achievement_${group.groupId}", indexOffset = index, modifier = Modifier.fillMaxWidth()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = softCardShape,
            border = softCardBorder()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        headlineTier.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = if (isMaxed) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (group.tiers.size > 1) {
                        Text(
                            " (${(tierIndex + 1).coerceAtLeast(0)}/${group.tiers.size})",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Text(headlineTier.description, style = MaterialTheme.typography.bodySmall)

                if (nextTier != null) {
                    LinearProgressIndicator(
                        progress = { (currentValue.toFloat() / nextTier.target).coerceIn(0f, 1f) },
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        gapSize = 0.dp,
                        drawStopIndicator = {}
                    )
                    Text(
                        "$currentValue / ${nextTier.target}",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    if (completedTier != null) {
                        Text(
                            "Completed: ${completedTier.title}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                } else {
                    Text(
                        "Fully completed!",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
            val fillFraction = ((tierIndex + 1).toFloat() / group.tiers.size).coerceIn(0f, 1f)
            AchievementTrophy(fillFraction = fillFraction)
            }
        }
    }
}

@Composable
private fun AchievementTrophy(fillFraction: Float) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .background(MaterialTheme.colorScheme.surfaceContainerHigh, RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        Box(modifier = Modifier.size(28.dp)) {
            Icon(
                Icons.Filled.EmojiEvents,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                modifier = Modifier.size(28.dp)
            )
            if (fillFraction > 0f) {
                Icon(
                    Icons.Filled.EmojiEvents,
                    contentDescription = null,
                    tint = getPastelColor(),
                    modifier = Modifier
                        .size(28.dp)
                        .drawWithContent {
                            val visibleTop = size.height * (1f - fillFraction)
                            clipRect(top = visibleTop) {
                                this@drawWithContent.drawContent()
                            }
                        }
                )
            }
        }
    }
}