package com.example.rejournal.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.rejournal.data.GoalDefinitions
import com.example.rejournal.data.GoalProgressCalculator
import com.example.rejournal.data.GoalProgressPrefs
import androidx.compose.runtime.rememberCoroutineScope
import com.example.rejournal.widget.AppWidgetsUpdater
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalDetailScreen(
    viewModel: MoodViewModel,
    goalId: String
) {
    val context = LocalContext.current
    val entries by viewModel.allEntries.collectAsState()
    val definition = remember(goalId) { GoalDefinitions.byId(goalId) }
    val scope = rememberCoroutineScope()

    var state by remember(goalId) { mutableStateOf(GoalProgressPrefs.getState(context, goalId)) }

    LaunchedEffect(entries) {
        GoalProgressCalculator.checkAndCompleteActiveGoals(context, entries)
        state = GoalProgressPrefs.getState(context, goalId)
    }

    if (definition == null) {
        Text("Goal not found")
        return
    }

    val progress = GoalProgressCalculator.currentProgress(definition, state, entries)
    val successRate = if (state.attempts > 0) (state.completions * 100 / state.attempts) else null

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = { TopAppBar(title = { Text(definition.title) }) }
    ) { padding: PaddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(definition.description, style = MaterialTheme.typography.bodyMedium)

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Completed ${state.completions} time${if (state.completions == 1) "" else "s"}",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        successRate?.let { "Success rate: $it%" } ?: "Success rate: - (no attempts yet)",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            if (state.isActive) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("In progress: $progress / ${definition.target}", style = MaterialTheme.typography.titleMedium)
                        LinearProgressIndicator(
                            progress = { (progress.toFloat() / definition.target).coerceIn(0f, 1f) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            Button(
                onClick = {
                    if (state.isActive) {
                        GoalProgressPrefs.cancelGoal(context, goalId)
                    } else {
                        GoalProgressPrefs.startGoal(context, goalId)
                    }
                    state = GoalProgressPrefs.getState(context, goalId)
                    scope.launch { AppWidgetsUpdater.updateAll(context) }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(if (state.isActive) "Cancel Goal" else "Start Goal")
            }
        }
    }
}