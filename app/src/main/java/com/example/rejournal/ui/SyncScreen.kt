package com.example.rejournal.ui

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.rejournal.data.CalendarInfo
import com.example.rejournal.data.CalendarSyncHelper
import com.example.rejournal.data.CalendarSyncPrefs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SyncScreen(viewModel: MoodViewModel, onBack: () -> Unit) {
    val context = LocalContext.current
    var hasPermission by remember { mutableStateOf(viewModel.hasCalendarPermission()) }
    var calendars by remember { mutableStateOf<List<CalendarInfo>>(emptyList()) }
    var selectedIds by remember { mutableStateOf(CalendarSyncPrefs.getSelectedCalendarIds(context)) }
    var lastSyncedMonth by remember { mutableStateOf(CalendarSyncPrefs.getLastSyncedMonth(context)) }
    var syncMessage by remember { mutableStateOf<String?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
        if (granted) calendars = CalendarSyncHelper.listCalendars(context)
    }

    if (hasPermission && calendars.isEmpty()) {
        calendars = CalendarSyncHelper.listCalendars(context)
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = { CenterAlignedTopAppBar(title = { Text("Sync") }, navigationIcon = { BackButton(onBack) }) }
    ) { padding: PaddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Sync your device calendar to automatically mark upcoming events this month as Important Days. Syncing happens automatically each time the app opens.",
                style = MaterialTheme.typography.bodyMedium
            )

            if (!hasPermission) {
                Button(
                    onClick = { permissionLauncher.launch(Manifest.permission.READ_CALENDAR) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Grant Calendar Access")
                }
            } else {
                if (calendars.isEmpty()) {
                    Text("No calendars found on this device.", style = MaterialTheme.typography.bodyMedium)
                } else {
                    Text("Calendars to sync", style = MaterialTheme.typography.titleMedium)
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        calendars.forEach { calendar ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = calendar.id in selectedIds,
                                    onCheckedChange = { checked ->
                                        selectedIds = if (checked) selectedIds + calendar.id else selectedIds - calendar.id
                                        CalendarSyncPrefs.setSelectedCalendarIds(context, selectedIds)
                                    }
                                )
                                Column {
                                    Text(calendar.displayName, style = MaterialTheme.typography.bodyMedium)
                                    if (calendar.accountName.isNotBlank()) {
                                        Text(calendar.accountName, style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            }
                        }
                    }

                    Button(
                        onClick = {
                            viewModel.syncCalendarNow(selectedIds) { count ->
                                syncMessage = "Added $count new important day${if (count == 1) "" else "s"}."
                                lastSyncedMonth = CalendarSyncPrefs.getLastSyncedMonth(context)
                            }
                        },
                        enabled = selectedIds.isNotEmpty(),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Sync Now")
                    }

                    lastSyncedMonth?.let {
                        Text("Last synced: $it", style = MaterialTheme.typography.bodySmall)
                    }
                    syncMessage?.let {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = softCardShape,
                            border = softCardBorder()
                        ) {
                            Text(it, modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }
}