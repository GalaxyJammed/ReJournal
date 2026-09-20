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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import com.example.rejournal.ui.components.ButterflyCardWrapper
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.unit.dp
import com.example.rejournal.data.CapsuleType
import com.example.rejournal.data.TimeCapsule

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeCapsulesScreen(
    viewModel: MoodViewModel,
    onCreateClick: () -> Unit,
    onBack: () -> Unit
) {
    val capsules by viewModel.allTimeCapsules.collectAsState()
    var pendingDeletion by remember { mutableStateOf<TimeCapsule?>(null) }

    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("Time Capsules") }, navigationIcon = { BackButton(onBack) }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateClick) {
                Icon(Icons.Filled.Add, contentDescription = "New time capsule")
            }
        }
    ) { padding: PaddingValues ->
        if (capsules.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                Text("No time capsules yet. Tap + to write one to your future self.")
            }
        } else {
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScrollbar(scrollState)
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                capsules.forEachIndexed { idx, capsule ->
                    ButterflyCardWrapper(seed = "TimeCapsule_${capsule.id}", indexOffset = idx, modifier = Modifier.fillMaxWidth()) {
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
                                Column {
                                    Text(
                                        if (capsule.type == CapsuleType.MOOD) "Mood Capsule" else "Time Capsule",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(capsuleSubtitle(capsule), style = MaterialTheme.typography.bodySmall)
                                    Text(
                                        when {
                                            capsule.opened -> "Delivered — opened"
                                            capsule.delivered -> "Delivered — waiting for you to view it"
                                            else -> "Waiting to trigger"
                                        },
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                                IconButton(onClick = { pendingDeletion = capsule }) {
                                    Icon(Icons.Filled.Delete, contentDescription = "Delete capsule")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    pendingDeletion?.let { capsule ->
        AlertDialog(
            onDismissRequest = { pendingDeletion = null },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteTimeCapsule(capsule)
                    pendingDeletion = null
                }) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { pendingDeletion = null }) { Text("Cancel") }
            },
            title = { Text("Delete this capsule?") },
            text = { Text("This can't be undone.") }
        )
    }
}

private fun capsuleSubtitle(capsule: TimeCapsule): String = when (capsule.type) {
    CapsuleType.MOOD -> "Triggers next time you log mood ${capsule.targetMood}"
    CapsuleType.TIME -> "Triggers on ${capsule.targetDate}"
}
