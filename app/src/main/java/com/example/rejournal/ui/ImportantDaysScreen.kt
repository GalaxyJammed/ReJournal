package com.example.rejournal.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.rejournal.data.ImportantDay
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportantDaysScreen(viewModel: MoodViewModel) {
    val importantDays by viewModel.allImportantDays.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var dayPendingDeletion by remember { mutableStateOf<ImportantDay?>(null) }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = { TopAppBar(title = { Text("Important Days") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Add important day")
            }
        }
    ) { padding: PaddingValues ->
        if (importantDays.isEmpty()) {
            Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
                Text("No important days marked yet. Tap + to add one.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(importantDays) { day ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { dayPendingDeletion = day }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("${day.date}", style = MaterialTheme.typography.titleMedium)
                                if (day.message.isNotBlank()) {
                                    Text(day.message, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                            Icon(Icons.Filled.Star, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddImportantDayDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { date, message ->
                viewModel.saveImportantDay(date, message)
                showAddDialog = false
            }
        )
    }

    dayPendingDeletion?.let { day ->
        AlertDialog(
            onDismissRequest = { dayPendingDeletion = null },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteImportantDay(day)
                    dayPendingDeletion = null
                }) { Text("Remove") }
            },
            dismissButton = {
                TextButton(onClick = { dayPendingDeletion = null }) { Text("Cancel") }
            },
            title = { Text("${day.date}") },
            text = { Text(if (day.message.isNotBlank()) day.message else "No message set. Remove this important day?") }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddImportantDayDialog(
    onDismiss: () -> Unit,
    onConfirm: (LocalDate, String) -> Unit
) {
    var dateText by remember { mutableStateOf(LocalDate.now().plusDays(1).toString()) }
    var message by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                try {
                    val date = LocalDate.parse(dateText.trim())
                    onConfirm(date, message.trim())
                } catch (e: Exception) {
                    error = "Enter a valid date as YYYY-MM-DD"
                }
            }) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
        title = { Text("New Important Day") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = dateText,
                    onValueChange = { dateText = it },
                    label = { Text("Date (YYYY-MM-DD)") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = message,
                    onValueChange = { message = it },
                    label = { Text("Message (optional)") }
                )
                error?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
            }
        }
    )
}