package com.example.rejournal.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.rejournal.data.GoalCategory
import com.example.rejournal.data.GoalDefinitions
import com.example.rejournal.data.GoalProgressPrefs
import androidx.compose.material3.CenterAlignedTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalSuggestionsScreen(
    category: GoalCategory,
    onGoalSelected: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val suggestions = remember {
        GoalDefinitions.byCategory(category).filter { !GoalProgressPrefs.getState(context, it.id).isActive }
    }
    var showFullDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text(category.displayName) }, navigationIcon = { BackButton(onBack) }) }
    ) { padding: PaddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(category.description, style = MaterialTheme.typography.bodyMedium)

            if (suggestions.isEmpty()) {
                Text("No more suggestions in this category right now.")
            } else {
                suggestions.forEach { definition ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            if (GoalProgressPrefs.canStartNewGoal(context)) {
                                GoalProgressPrefs.startGoal(context, definition.id)
                                onGoalSelected()
                            } else {
                                showFullDialog = true
                            }
                        }
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(definition.title, style = MaterialTheme.typography.titleMedium)
                            Text(definition.description, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }

    if (showFullDialog) {
        AlertDialog(
            onDismissRequest = { showFullDialog = false },
            confirmButton = {
                TextButton(onClick = { showFullDialog = false }) { Text("OK") }
            },
            title = { Text("Goal slots full") },
            text = { Text("You already have ${GoalProgressPrefs.MAX_ACTIVE_GOALS} active goals. Complete or cancel one to pick a new goal.") }
        )
    }
}