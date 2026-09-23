package com.example.rejournal.ui

import android.widget.Toast
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rejournal.data.ActivityIcons
import com.example.rejournal.data.Mixtape
import com.example.rejournal.data.MixtapeCalculator
import com.example.rejournal.data.MoodAppearancePrefs
import com.example.rejournal.data.MoodEntry
import com.example.rejournal.ui.components.ButterflyCardWrapper
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmotionalMixtapeScreen(
    viewModel: MoodViewModel,
    onGoToDay: (LocalDate) -> Unit,
    onBack: () -> Unit
) {
    val entries by viewModel.allEntries.collectAsState()
    val availableYears = remember(entries) { MixtapeCalculator.getAvailableYears(entries) }
    var selectedYear by remember(availableYears) { mutableIntStateOf(availableYears.firstOrNull() ?: LocalDate.now().year) }
    var newestFirst by remember { mutableStateOf(true) }

    val allWeeksAscending = remember(entries, selectedYear) {
        MixtapeCalculator.calculateForYear(entries, selectedYear, newestFirst = false)
    }

    val shelvesAscending = remember(allWeeksAscending) {
        allWeeksAscending.chunked(10)
    }

    val displayedShelves = remember(shelvesAscending, newestFirst) {
        if (newestFirst) shelvesAscending.reversed() else shelvesAscending
    }

    var selectedMixtape by remember { mutableStateOf<Mixtape?>(null) }

    val context = LocalContext.current
    val activeEmojis = remember(context) { MoodAppearancePrefs.getActiveEmojis(context) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets.systemBars,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.Album,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text("Emotional Mixtapes", fontWeight = FontWeight.Bold)
                    }
                },
                navigationIcon = { BackButton(onBack = onBack) }
            )
        }
    ) { padding: PaddingValues ->
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .navigationBarsPadding()
                .verticalScrollbar(scrollState)
                .verticalScroll(scrollState)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CompactHeaderCard(
                availableYears = availableYears,
                selectedYear = selectedYear,
                allMixtapes = allWeeksAscending,
                newestFirst = newestFirst,
                onYearSelected = { selectedYear = it },
                onToggleSort = { newestFirst = !newestFirst }
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "YEARLY BOOKSHELF",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                )
                Text(
                    text = if (newestFirst) "Newest → Oldest" else "Oldest → Newest",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            displayedShelves.forEachIndexed { index, shelfTapes ->
                BookshelfRack(
                    shelfNumber = index + 1,
                    mixtapes = shelfTapes,
                    onMixtapeClick = { tape -> selectedMixtape = tape }
                )
            }
        }
    }

    selectedMixtape?.let { mixtape ->
        MixtapeDetailModal(
            mixtape = mixtape,
            activeEmojis = activeEmojis,
            onGoToDay = { date ->
                selectedMixtape = null
                onGoToDay(date)
            },
            onDismiss = { selectedMixtape = null }
        )
    }
}

@Composable
private fun CompactHeaderCard(
    availableYears: List<Int>,
    selectedYear: Int,
    allMixtapes: List<Mixtape>,
    newestFirst: Boolean,
    onYearSelected: (Int) -> Unit,
    onToggleSort: () -> Unit
) {
    val recordedCount = remember(allMixtapes) { allMixtapes.count { it.hasEntries } }
    val totalWeeks = allMixtapes.size
    val progress = if (totalWeeks > 0) recordedCount.toFloat() / totalWeeks else 0f

    val topVibe = remember(allMixtapes) {
        allMixtapes.filter { it.hasEntries }
            .groupBy { it.vibeGenre }
            .maxByOrNull { it.value.size }?.key ?: "Awaiting Tapes 🎧"
    }

    ButterflyCardWrapper(
        seed = "MixtapeHeaderCard_$selectedYear",
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        indexOffset = 0
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = softCardShape,
            border = softCardBorder(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val currentIndex = availableYears.indexOf(selectedYear)
                        val canGoOlder = currentIndex < availableYears.size - 1
                        val canGoNewer = currentIndex > 0

                        IconButton(
                            onClick = { if (canGoOlder) onYearSelected(availableYears[currentIndex + 1]) },
                            enabled = canGoOlder,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Filled.ChevronLeft, contentDescription = "Older Year", modifier = Modifier.size(20.dp))
                        }

                        Text(
                            text = "$selectedYear",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )

                        IconButton(
                            onClick = { if (canGoNewer) onYearSelected(availableYears[currentIndex - 1]) },
                            enabled = canGoNewer,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Filled.ChevronRight, contentDescription = "Newer Year", modifier = Modifier.size(20.dp))
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = "$recordedCount / $totalWeeks Recorded",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }

                        IconButton(
                            onClick = onToggleSort,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Sort,
                                contentDescription = "Toggle Sort Order",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(CircleShape),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceContainerHigh
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Vibe:",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = topVibe,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }

                    Text(
                        text = if (newestFirst) "Top: Newest Weeks" else "Top: Oldest Weeks",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

@Composable
private fun BookshelfRack(
    shelfNumber: Int,
    mixtapes: List<Mixtape>,
    onMixtapeClick: (Mixtape) -> Unit
) {
    val minWeek = remember(mixtapes) { mixtapes.minOfOrNull { it.weekNumber } ?: 0 }
    val maxWeek = remember(mixtapes) { mixtapes.maxOfOrNull { it.weekNumber } ?: 0 }
    val recordedInShelf = remember(mixtapes) { mixtapes.count { it.hasEntries } }
    val totalInShelf = mixtapes.size
    val weekRangeText = if (minWeek > 0) "Weeks $minWeek – $maxWeek" else ""

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
            ) {
                Text(
                    text = "SHELF $shelfNumber ($recordedInShelf/$totalInShelf)",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
            Text(
                text = weekRangeText,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(4.dp, RoundedCornerShape(12.dp))
                .clip(RoundedCornerShape(12.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF2C1B18),
                            Color(0xFF1E1210),
                            Color(0xFF38231F)
                        )
                    )
                )
                .border(
                    width = 2.dp,
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF5D3A34),
                            Color(0xFF8B5A51),
                            Color(0xFF5D3A34)
                        )
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(top = 10.dp, bottom = 6.dp, start = 8.dp, end = 8.dp)
        ) {
            val horizontalScrollState = rememberScrollState()

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp)
                    .horizontalScrollbar(horizontalScrollState, color = Color(0xFFFFB74D).copy(alpha = 0.75f))
                    .horizontalScroll(horizontalScrollState),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                mixtapes.forEach { mixtape ->
                    CassetteTapeItem(
                        mixtape = mixtape,
                        onClick = { onMixtapeClick(mixtape) }
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF8B5A51),
                            Color(0xFF2C1B18)
                        )
                    ),
                    shape = RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp)
                )
        )
    }
}

@Composable
private fun CassetteTapeItem(
    mixtape: Mixtape,
    onClick: () -> Unit
) {
    val style = mixtape.cassetteStyle
    val isRecorded = mixtape.hasEntries

    Box(
        modifier = Modifier
            .width(118.dp)
            .height(82.dp)
            .shadow(if (isRecorded) 6.dp else 2.dp, RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (isRecorded) {
                    Brush.linearGradient(
                        colors = listOf(
                            style.primaryColor.copy(alpha = 0.9f),
                            style.secondaryColor.copy(alpha = 0.95f)
                        )
                    )
                } else {
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF333333),
                            Color(0xFF222222)
                        )
                    )
                }
            )
            .border(
                width = 1.5.dp,
                color = if (isRecorded) style.accentColor.copy(alpha = 0.8f) else Color(0xFF555555),
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick)
            .padding(4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (isRecorded) Color.White.copy(alpha = 0.9f) else Color(0xFF444444))
                    .padding(horizontal = 4.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "WK ${mixtape.weekNumber}",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isRecorded) Color.Black else Color.LightGray
                )
                Text(
                    text = if (isRecorded) "SIDE A" else "BLANK",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isRecorded) style.secondaryColor else Color.Gray
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(26.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.Black.copy(alpha = 0.75f))
                    .border(0.5.dp, Color.Gray.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                    .padding(horizontal = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CassetteSpoolWheel(isRecorded = isRecorded, accentColor = style.accentColor)

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(10.dp)
                            .padding(horizontal = 4.dp)
                            .background(Color(0xFF2A1B14), RoundedCornerShape(2.dp))
                    )

                    CassetteSpoolWheel(isRecorded = isRecorded, accentColor = style.accentColor)
                }
            }

            Text(
                text = if (isRecorded) mixtape.title else "Unrecorded",
                style = MaterialTheme.typography.labelSmall,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = if (isRecorded) Color.White else Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 2.dp)
            )
        }
    }
}

@Composable
private fun CassetteSpoolWheel(
    isRecorded: Boolean,
    accentColor: Color
) {
    Box(
        modifier = Modifier
            .size(16.dp)
            .clip(CircleShape)
            .background(if (isRecorded) accentColor else Color(0xFF666666)),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(Color.Black)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun MixtapeDetailModal(
    mixtape: Mixtape,
    activeEmojis: List<String>,
    onGoToDay: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    var isPlaying by remember { mutableStateOf(true) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            RetroCassettePlayer(
                mixtape = mixtape,
                isPlaying = isPlaying,
                onTogglePlay = { isPlaying = !isPlaying }
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = mixtape.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center
                )

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = mixtape.cassetteStyle.primaryColor.copy(alpha = 0.2f),
                    border = softCardBorder()
                ) {
                    Text(
                        text = mixtape.vibeGenre,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }

                val dateRangeFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy")
                Text(
                    text = "${mixtape.startDate.format(dateRangeFormatter)} – ${mixtape.endDate.format(dateRangeFormatter)} • Week ${mixtape.weekNumber}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (mixtape.hasEntries) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = softCardShape,
                    border = softCardBorder(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "WEEKLY METRICS BREAKDOWN",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 1.sp
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val moodInt = mixtape.averageMood.toInt().coerceIn(1, 5)
                            val moodEmoji = activeEmojis.getOrElse(moodInt - 1) { "🙂" }

                            Text(
                                text = "Avg Mood: $moodEmoji ${String.format("%.1f", mixtape.averageMood)} / 5.0",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        MetricBarRow("⚡ Energy", mixtape.averageEnergy, Color(0xFFFFB74D))
                        MetricBarRow("🎯 Productivity", mixtape.averageProductivity, Color(0xFF81C784))
                        MetricBarRow("🧘 Stress", mixtape.averageStress, Color(0xFFE57373))
                        MetricBarRow("😴 Sleep", mixtape.averageSleep, Color(0xFF64B5F6))
                    }
                }

                if (mixtape.topActivities.isNotEmpty()) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "TOP FEATURES & ACTIVITIES",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 1.sp
                        )

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            mixtape.topActivities.take(6).forEach { (tag, count) ->
                                val icon = ActivityIcons.resolve(tag, null)
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.secondaryContainer
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            imageVector = icon,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp),
                                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                                        )
                                        Text(
                                            text = "$tag ($count)",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "TRACKLIST (${mixtape.entries.size} LOGS)",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.sp
                    )

                    mixtape.entries.forEachIndexed { index, entry ->
                        TrackRowItem(
                            trackNumber = index + 1,
                            entry = entry,
                            activeEmojis = activeEmojis,
                            onClick = { onGoToDay(entry.date) }
                        )
                    }
                }
            } else {
                Text(
                    text = "No entries logged for this week yet. Log your daily entries to record this tape!",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (mixtape.hasEntries) {
                    OutlinedButton(
                        onClick = {
                            val shareText = "📼 Emotional Mixtape: ${mixtape.title}\n" +
                                    "✨ Vibe: ${mixtape.vibeGenre}\n" +
                                    "📊 Avg Mood: ${String.format("%.1f", mixtape.averageMood)}/5.0\n" +
                                    "🗓️ Week ${mixtape.weekNumber}, ${mixtape.year}\n" +
                                    "Preserved in ReJournal! 📖"
                            clipboardManager.setText(AnnotatedString(shareText))
                            Toast.makeText(context, "Mixtape summary copied to clipboard!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Filled.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Share")
                    }
                }

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Close")
                }
            }
        }
    }
}

@Composable
private fun RetroCassettePlayer(
    mixtape: Mixtape,
    isPlaying: Boolean,
    onTogglePlay: () -> Unit
) {
    val style = mixtape.cassetteStyle

    val infiniteTransition = rememberInfiniteTransition(label = "SpoolRotation")
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, easing = LinearEasing)
        ),
        label = "RotationAngle"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .shadow(8.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        style.primaryColor,
                        style.secondaryColor
                    )
                )
            )
            .border(2.dp, style.accentColor, RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White.copy(alpha = 0.9f))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "REJOURNAL CASSETTE 📼",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.Black
                )
                Text(
                    text = "HIGH BIAS / TYPE II",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = style.secondaryColor
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF151515))
                    .border(1.dp, Color.Gray.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AnimatedSpool(
                        angle = if (isPlaying && mixtape.hasEntries) rotationAngle else 0f,
                        accentColor = style.accentColor
                    )

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        AnimatedEqualizerBars(
                            isPlaying = isPlaying && mixtape.hasEntries,
                            barColor = style.accentColor
                        )
                    }

                    AnimatedSpool(
                        angle = if (isPlaying && mixtape.hasEntries) rotationAngle else 0f,
                        accentColor = style.accentColor
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (mixtape.hasEntries) "SIDE A • ${mixtape.entries.size} TRACKS" else "EMPTY TAPE",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.9f)
                )

                if (mixtape.hasEntries) {
                    IconButton(
                        onClick = onTogglePlay,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(style.accentColor)
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = Color.Black
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AnimatedSpool(
    angle: Float,
    accentColor: Color
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .rotate(angle)
            .clip(CircleShape)
            .background(accentColor)
            .border(2.dp, Color.White.copy(alpha = 0.8f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = size.width / 2
            for (i in 0 until 3) {
                val rad = Math.toRadians((i * 120).toDouble())
                val endX = center.x + (radius - 6) * Math.cos(rad).toFloat()
                val endY = center.y + (radius - 6) * Math.sin(rad).toFloat()
                drawLine(
                    color = Color.Black,
                    start = center,
                    end = Offset(endX, endY),
                    strokeWidth = 3f
                )
            }
        }
        Box(
            modifier = Modifier
                .size(16.dp)
                .clip(CircleShape)
                .background(Color.Black)
        )
    }
}

@Composable
private fun AnimatedEqualizerBars(
    isPlaying: Boolean,
    barColor: Color
) {
    val infiniteTransition = rememberInfiniteTransition(label = "Equalizer")
    val heights = (0..5).map { index ->
        val duration = 300 + index * 120
        infiniteTransition.animateFloat(
            initialValue = 0.2f,
            targetValue = 1.0f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = duration, easing = LinearEasing)
            ),
            label = "BarHeight$index"
        )
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.Bottom,
        modifier = Modifier.height(28.dp)
    ) {
        heights.forEach { anim ->
            val factor = if (isPlaying) anim.value else 0.2f
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .fillMaxHeight(factor)
                    .clip(RoundedCornerShape(3.dp))
                    .background(barColor)
            )
        }
    }
}

@Composable
private fun MetricBarRow(
    label: String,
    value: Double,
    barColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.width(110.dp)
        )
        LinearProgressIndicator(
            progress = { (value / 5.0).toFloat().coerceIn(0f, 1f) },
            modifier = Modifier
                .weight(1f)
                .height(8.dp)
                .clip(CircleShape),
            color = barColor,
            trackColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
        Text(
            text = String.format("%.1f", value),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(36.dp),
            textAlign = TextAlign.End
        )
    }
}

@Composable
private fun TrackRowItem(
    trackNumber: Int,
    entry: MoodEntry,
    activeEmojis: List<String>,
    onClick: () -> Unit
) {
    val moodInt = entry.mood.coerceIn(1, 5)
    val moodEmoji = activeEmojis.getOrElse(moodInt - 1) { "🙂" }
    val dayFormatter = DateTimeFormatter.ofPattern("EEE, MMM d")

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        border = softCardBorder()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = String.format("%02d", trackNumber),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(end = 12.dp)
                )

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = entry.date.format(dayFormatter),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = " $moodEmoji",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    if (entry.note.isNotBlank()) {
                        Text(
                            text = entry.note,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Icon(
                Icons.Filled.ChevronRight,
                contentDescription = "View Day Entry",
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        }
    }
}
