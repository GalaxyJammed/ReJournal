package com.example.rejournal.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.rejournal.data.NotificationTroubleshootHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationTroubleshootScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var notificationsEnabled by remember { mutableStateOf(NotificationTroubleshootHelper.areNotificationsEnabled(context)) }
    var batteryOptimizationIgnored by remember { mutableStateOf(NotificationTroubleshootHelper.isIgnoringBatteryOptimizations(context)) }

    val lifecycleOwner = LocalLifecycleOwner.current
    androidx.compose.runtime.DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                notificationsEnabled = NotificationTroubleshootHelper.areNotificationsEnabled(context)
                batteryOptimizationIgnored = NotificationTroubleshootHelper.isIgnoringBatteryOptimizations(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = { CenterAlignedTopAppBar(title = { Text("Notifications Not Working?") }, navigationIcon = { BackButton(onBack) }) }
    ) { padding: PaddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "If reminders, important days, or time capsules aren't notifying you, check these device settings.",
                style = MaterialTheme.typography.bodyMedium
            )

            TroubleshootItem(
                title = "Notification Permission",
                description = "Notifications must be allowed for ReJournal in system settings.",
                isOk = notificationsEnabled,
                buttonLabel = "Open Notification Settings",
                onClick = { NotificationTroubleshootHelper.openNotificationSettings(context) }
            )

            TroubleshootItem(
                title = "Battery Optimization",
                description = "If the system is allowed to optimize (restrict) ReJournal's battery use, scheduled notifications may be delayed or blocked.",
                isOk = batteryOptimizationIgnored,
                buttonLabel = "Disable Battery Optimization",
                onClick = { NotificationTroubleshootHelper.requestIgnoreBatteryOptimizations(context) }
            )

            TroubleshootItem(
                title = "Autostart",
                description = "Some phone brands (Xiaomi, Huawei, Oppo, Vivo, and others) block apps from running in the background unless Autostart is manually enabled. This can't be checked automatically - tap below to check if your device has this setting.",
                isOk = null,
                buttonLabel = "Open Autostart Settings",
                onClick = { NotificationTroubleshootHelper.openAutostartSettings(context) }
            )
        }
    }
}

@Composable
private fun TroubleshootItem(
    title: String,
    description: String,
    isOk: Boolean?,
    buttonLabel: String,
    onClick: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isOk != null) {
                    Icon(
                        if (isOk) Icons.Filled.CheckCircle else Icons.Filled.Warning,
                        contentDescription = if (isOk) "OK" else "Needs attention",
                        tint = if (isOk) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                    )
                }
                Text(
                    title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(start = if (isOk != null) 8.dp else 0.dp)
                )
            }
            Text(description, style = MaterialTheme.typography.bodySmall)
            if (isOk != true) {
                Button(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
                    Text(buttonLabel)
                }
            }
        }
    }
}