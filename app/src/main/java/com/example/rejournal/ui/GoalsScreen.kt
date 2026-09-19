package com.example.rejournal.ui

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.rejournal.data.GoalDefinition
import com.example.rejournal.data.GoalProgressCalculator
import com.example.rejournal.data.GoalProgressPrefs
import androidx.compose.material3.CenterAlignedTopAppBar
import com.example.rejournal.ui.verticalScrollbar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalsScreen(
    viewModel: MoodViewModel,
    onGoalClick: (String) -> Unit,
    onFindGoalClick: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val entries by viewModel.allEntries.collectAsState()

    var totalCompletions by remember { mutableStateOf(GoalProgressPrefs.totalCompletions(context)) }
    var activeGoals by remember { mutableStateOf(GoalProgressPrefs.activeGoals(context)) }

    LaunchedEffect(entries) {
        GoalProgressCalculator.checkAndCompleteActiveGoals(context, entries)
        totalCompletions = GoalProgressPrefs.totalCompletions(context)
        activeGoals = GoalProgressPrefs.activeGoals(context)
    }

    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("Goals") }, navigationIcon = { BackButton(onBack) }) }
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
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = softCardShape,
                border = softCardBorder(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("$totalCompletions", style = MaterialTheme.typography.headlineLarge)
                    Text("Total Completed Goals", style = MaterialTheme.typography.bodyMedium)
                }
            }

            Text(
                "Current Goals (${activeGoals.size}/${GoalProgressPrefs.MAX_ACTIVE_GOALS})",
                style = MaterialTheme.typography.titleMedium
            )

            if (activeGoals.isEmpty()) {
                Text("You don't have any active goals yet.", style = MaterialTheme.typography.bodyMedium)
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    activeGoals.forEach { definition ->
                        GoalRow(definition = definition, context = context, onClick = { onGoalClick(definition.id) })
                    }
                }
            }

            if (activeGoals.size < GoalProgressPrefs.MAX_ACTIVE_GOALS) {
                Button(
                    onClick = onFindGoalClick,
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                ) {
                    Text("Find a goal that suits you")
                }
            } else {
                Text(
                    "Complete or cancel a goal to pick a new one.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GoalRow(definition: GoalDefinition, context: Context, onClick: () -> Unit) {
    val state = GoalProgressPrefs.getState(context, definition.id)
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = softCardShape,
        border = softCardBorder(),
        onClick = onClick
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(definition.title, style = MaterialTheme.typography.titleMedium)
                    Text(definition.description, style = MaterialTheme.typography.bodySmall)
                }
                if (state.isActive) {
                    Text("In progress", style = MaterialTheme.typography.bodySmall)
                }
            }
            HorizontalDivider()
            Text(
                "Completed ${state.completions} time${if (state.completions == 1) "" else "s"}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }
}