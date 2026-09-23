package com.example.rejournal.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.rejournal.data.MicroWin
import com.example.rejournal.data.MicroWinPrefs
import com.example.rejournal.ui.components.ButterflyCardWrapper
import com.example.rejournal.ui.components.IconPill
import com.example.rejournal.ui.components.PastelIcon
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

private data class AggregatedMicroWinItem(
    val title: String,
    val count: Int,
    val date: LocalDate,
    val latestWin: MicroWin
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun MicroWinsScreen(
    viewModel: MoodViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val microWins by viewModel.allMicroWins.collectAsState()
    var winInputText by remember { mutableStateOf("") }
    var showAddedFeedback by remember { mutableStateOf(false) }
    var selectedMonthFilter by remember { mutableStateOf<YearMonth?>(null) }

    var quickAdditions by remember { mutableStateOf(MicroWinPrefs.getQuickAdditions(context)) }
    var showAddCustomDialog by remember { mutableStateOf(false) }
    var customAdditionInput by remember { mutableStateOf("") }

    val currentMonth = remember { YearMonth.now() }
    val availableMonths = remember(microWins) {
        microWins.map { YearMonth.from(it.date) }.distinct().sortedDescending()
    }

    val filteredWins = remember(microWins, selectedMonthFilter) {
        if (selectedMonthFilter == null) {
            microWins
        } else {
            microWins.filter { YearMonth.from(it.date) == selectedMonthFilter }
        }
    }

    val groupedByMonth = remember(filteredWins) {
        filteredWins.groupBy { YearMonth.from(it.date) }
    }

    Scaffold(
        containerColor = Color.Transparent,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Micro-Wins", fontWeight = FontWeight.Bold) },
                navigationIcon = { BackButton(onBack = onBack) }
            )
        }
    ) { padding ->
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .navigationBarsPadding()
                .verticalScrollbar(scrollState)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            ButterflyCardWrapper(seed = "LogMicroWin", indexOffset = 0, modifier = Modifier.fillMaxWidth()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = softCardShape,
                    border = softCardBorder()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconPill(icon = Icons.Filled.EmojiEvents)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    "Log a Micro-Win",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "A small victory is still a victory!",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        OutlinedTextField(
                            value = winInputText,
                            onValueChange = {
                                winInputText = it
                                showAddedFeedback = false
                            },
                            placeholder = { Text("e.g., Made my bed, Worked out...") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                            )
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Quick additions:",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            IconButton(
                                onClick = { showAddCustomDialog = true },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    Icons.Filled.AddCircle,
                                    contentDescription = "Add quick addition",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            quickAdditions.forEach { suggestion ->
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(start = 10.dp, top = 2.dp, bottom = 2.dp, end = 4.dp)
                                    ) {
                                        Text(
                                            text = suggestion,
                                            style = MaterialTheme.typography.bodySmall,
                                            modifier = Modifier
                                                .clickable {
                                                    viewModel.saveMicroWin(suggestion)
                                                    showAddedFeedback = true
                                                }
                                                .padding(vertical = 4.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        IconButton(
                                            onClick = {
                                                MicroWinPrefs.removeQuickAddition(context, suggestion)
                                                quickAdditions = MicroWinPrefs.getQuickAdditions(context)
                                            },
                                            modifier = Modifier.size(20.dp)
                                        ) {
                                            Icon(
                                                Icons.Filled.Close,
                                                contentDescription = "Remove quick addition",
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                                modifier = Modifier.size(12.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Button(
                            onClick = {
                                if (winInputText.isNotBlank()) {
                                    viewModel.saveMicroWin(winInputText)
                                    winInputText = ""
                                    showAddedFeedback = true
                                }
                            },
                            enabled = winInputText.isNotBlank(),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Icon(
                                Icons.Filled.AddCircle,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Log Micro-Win")
                        }

                        AnimatedVisibility(
                            visible = showAddedFeedback,
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp)
                            ) {
                                Icon(
                                    Icons.Filled.Check,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    "Micro-Win logged! Keep it up!",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }

            val winsInCurrentMonth = remember(microWins) {
                microWins.count { YearMonth.from(it.date) == currentMonth }
            }

            ButterflyCardWrapper(seed = "MonthEndRecap", indexOffset = 1, modifier = Modifier.fillMaxWidth()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = softCardShape,
                    border = softCardBorder(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconPill(icon = Icons.Filled.Star)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    "Month-End Reflection",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                val monthName = currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy"))
                                Text(
                                    "$monthName Progress",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Text(
                            text = if (winsInCurrentMonth > 0) {
                                "🎉 You've logged $winsInCurrentMonth micro-win${if (winsInCurrentMonth > 1) "s" else ""} so far this month! As the month ends, look back here to celebrate every small accomplishment."
                            } else {
                                "✨ At the end of each month, your small daily wins gather here as a reminder of everything you accomplished!"
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            ButterflyCardWrapper(seed = "ViewMicroWins", indexOffset = 2, modifier = Modifier.fillMaxWidth()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = softCardShape,
                    border = softCardBorder()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    "Your Micro-Wins",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.primaryContainer
                                ) {
                                    Text(
                                        text = "${microWins.size}",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        if (availableMonths.size > 1) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Filled.FilterList,
                                    contentDescription = "Filter",
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                FilterChip(
                                    selected = selectedMonthFilter == null,
                                    onClick = { selectedMonthFilter = null },
                                    label = { Text("All Months") }
                                )
                                availableMonths.take(3).forEach { yearMonth ->
                                    val labelText = yearMonth.format(DateTimeFormatter.ofPattern("MMM yyyy"))
                                    FilterChip(
                                        selected = selectedMonthFilter == yearMonth,
                                        onClick = {
                                            selectedMonthFilter = if (selectedMonthFilter == yearMonth) null else yearMonth
                                        },
                                        label = { Text(labelText) }
                                    )
                                }
                            }
                        }

                        if (filteredWins.isEmpty()) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                PastelIcon(
                                    icon = Icons.Filled.EmojiEvents,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    "No micro-wins logged yet",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "Add your first small accomplishment above!",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                    textAlign = TextAlign.Center
                                )
                            }
                        } else {
                            groupedByMonth.forEach { (yearMonth, monthWins) ->
                                val aggregatedMonthWins = remember(monthWins) {
                                    monthWins.groupBy { Pair(it.date, it.title) }
                                        .map { (key, list) ->
                                            AggregatedMicroWinItem(
                                                title = key.second,
                                                count = list.size,
                                                date = key.first,
                                                latestWin = list.maxByOrNull { it.id } ?: list.first()
                                            )
                                        }
                                        .sortedByDescending { it.latestWin.id }
                                }

                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text(
                                        text = yearMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(top = 8.dp)
                                    )

                                    aggregatedMonthWins.forEach { item ->
                                        MicroWinItemRow(
                                            item = item,
                                            onDeleteOne = { viewModel.deleteMicroWin(item.latestWin) }
                                        )
                                        HorizontalDivider(
                                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    if (showAddCustomDialog) {
        AlertDialog(
            onDismissRequest = { showAddCustomDialog = false },
            title = { Text("New Quick Addition", fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = customAdditionInput,
                    onValueChange = { customAdditionInput = it },
                    placeholder = { Text("e.g. 🧘 Meditated 10 mins") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (customAdditionInput.isNotBlank()) {
                            MicroWinPrefs.addQuickAddition(context, customAdditionInput)
                            quickAdditions = MicroWinPrefs.getQuickAdditions(context)
                            customAdditionInput = ""
                            showAddCustomDialog = false
                        }
                    },
                    enabled = customAdditionInput.isNotBlank()
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddCustomDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun MicroWinItemRow(
    item: AggregatedMicroWinItem,
    onDeleteOne: () -> Unit
) {
    val dateFormatter = remember { DateTimeFormatter.ofPattern("MMM d, yyyy") }
    val dateText = remember(item.date) {
        val today = LocalDate.now()
        when (item.date) {
            today -> "Today"
            today.minusDays(1) -> "Yesterday"
            else -> item.date.format(dateFormatter)
        }
    }

    val displayTitle = if (item.count > 1) "${item.title} (${item.count}x)" else item.title

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Filled.EmojiEvents,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = displayTitle,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = dateText,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        IconButton(onClick = onDeleteOne) {
            Icon(
                Icons.Filled.DeleteOutline,
                contentDescription = "Delete win",
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
