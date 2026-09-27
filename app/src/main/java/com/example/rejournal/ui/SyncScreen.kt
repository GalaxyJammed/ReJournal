package com.example.rejournal.ui

import android.Manifest
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import com.example.rejournal.data.CalendarInfo
import com.example.rejournal.data.CalendarSyncHelper
import com.example.rejournal.data.CalendarSyncPrefs
import com.example.rejournal.data.HealthConnectSyncHelper
import com.example.rejournal.data.HealthConnectSyncPrefs
import com.example.rejournal.data.HealthConnectSyncWorker
import com.example.rejournal.ui.components.ButterflyCardWrapper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SyncScreen(viewModel: MoodViewModel, onBack: () -> Unit) {
    val context = LocalContext.current

    var isCalendarSyncEnabled by remember { mutableStateOf(CalendarSyncPrefs.isEnabled(context)) }
    var hasCalendarPermission by remember { mutableStateOf(viewModel.hasCalendarPermission()) }
    var calendars by remember { mutableStateOf<List<CalendarInfo>>(emptyList()) }
    var selectedIds by remember { mutableStateOf(CalendarSyncPrefs.getSelectedCalendarIds(context)) }
    var lastSyncedMonth by remember { mutableStateOf(CalendarSyncPrefs.getLastSyncedMonth(context)) }
    var syncMessage by remember { mutableStateOf<String?>(null) }

    val isSdkAvailable = remember { HealthConnectSyncHelper.isSdkAvailable(context) }
    var isHealthSyncEnabled by remember { mutableStateOf(HealthConnectSyncPrefs.isEnabled(context)) }
    var hasHealthPermissions by remember { mutableStateOf(false) }
    var syncSteps by remember { mutableStateOf(HealthConnectSyncPrefs.isStepsSyncEnabled(context)) }
    var syncWorkouts by remember { mutableStateOf(HealthConnectSyncPrefs.isWorkoutsSyncEnabled(context)) }
    var syncSleep by remember { mutableStateOf(HealthConnectSyncPrefs.isSleepSyncEnabled(context)) }
    var syncCalories by remember { mutableStateOf(HealthConnectSyncPrefs.isCaloriesSyncEnabled(context)) }

    var stepGoal by remember { mutableStateOf(HealthConnectSyncPrefs.getStepGoal(context)) }
    var workoutMinMinutes by remember { mutableStateOf(HealthConnectSyncPrefs.getWorkoutMinMinutes(context)) }
    var sleepGoalHours by remember { mutableStateOf(HealthConnectSyncPrefs.getSleepGoalHours(context)) }
    var calorieGoal by remember { mutableStateOf(HealthConnectSyncPrefs.getCalorieGoal(context)) }

    var notificationsEnabled by remember { mutableStateOf(HealthConnectSyncPrefs.isNotificationsEnabled(context)) }
    var lastHealthSyncedTime by remember { mutableStateOf(HealthConnectSyncPrefs.getLastSyncedTime(context)) }
    var healthSyncMessage by remember { mutableStateOf<String?>(null) }

    var showCustomStepDialog by remember { mutableStateOf(false) }
    var showCustomWorkoutMinDialog by remember { mutableStateOf(false) }
    var showCustomSleepDialog by remember { mutableStateOf(false) }
    var showCustomCalorieDialog by remember { mutableStateOf(false) }

    var customStepInput by remember { mutableStateOf("") }
    var customWorkoutMinInput by remember { mutableStateOf("") }
    var customSleepInput by remember { mutableStateOf("") }
    var customCalorieInput by remember { mutableStateOf("") }

    val stepPresets = listOf(5000, 8000, 10000, 12000, 15000)
    val workoutMinPresets = listOf(30, 45, 60, 90, 120)
    val sleepPresets = listOf(6, 7, 8, 9, 10, 12)
    val caloriePresets = listOf(300, 500, 800, 1000, 1200)

    val calendarPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCalendarPermission = granted
        if (granted) calendars = CalendarSyncHelper.listCalendars(context)
    }

    val providerPackage = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
        "com.google.android.healthconnect"
    } else {
        "com.google.android.apps.healthdata"
    }

    val healthConnectPermissionLauncher = rememberLauncherForActivityResult(
        PermissionController.createRequestPermissionResultContract(providerPackage)
    ) { grantedPermissions ->
        hasHealthPermissions = grantedPermissions.containsAll(HealthConnectSyncHelper.REQUIRED_PERMISSIONS)
        if (hasHealthPermissions) {
            HealthConnectSyncPrefs.setEnabled(context, true)
            isHealthSyncEnabled = true
            HealthConnectSyncWorker.schedulePeriodicSync(context)
        }
    }

    LaunchedEffect(Unit) {
        if (isSdkAvailable) {
            hasHealthPermissions = HealthConnectSyncHelper.hasPermissions(context)
        }
    }

    if (hasCalendarPermission && calendars.isEmpty()) {
        calendars = CalendarSyncHelper.listCalendars(context)
    }

    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("Sync") }, navigationIcon = { BackButton(onBack) }) }
    ) { padding: PaddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Calendar Sync", style = MaterialTheme.typography.titleLarge)
                    Text(
                        "Sync your device calendar to automatically mark upcoming events this month as Important Days.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Switch(
                    checked = isCalendarSyncEnabled,
                    onCheckedChange = { enabled ->
                        isCalendarSyncEnabled = enabled
                        CalendarSyncPrefs.setEnabled(context, enabled)
                    }
                )
            }

            if (isCalendarSyncEnabled) {
                if (!hasCalendarPermission) {
                    ButterflyCardWrapper(seed = "CalendarPermsCard", modifier = Modifier.fillMaxWidth()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = softCardShape,
                            border = softCardBorder()
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text(
                                    "Grant calendar access to select which calendars to sync.",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Button(
                                    onClick = { calendarPermissionLauncher.launch(Manifest.permission.READ_CALENDAR) },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Grant Calendar Access")
                                }
                            }
                        }
                    }
                } else {
                    if (calendars.isEmpty()) {
                        Text("No calendars found on this device.", style = MaterialTheme.typography.bodyMedium)
                    } else {
                        ButterflyCardWrapper(seed = "CalendarSettingsCard", modifier = Modifier.fillMaxWidth()) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = softCardShape,
                                border = softCardBorder()
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
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
                            Text("Sync Calendar Now")
                        }

                        lastSyncedMonth?.let {
                            Text("Last synced: $it", style = MaterialTheme.typography.bodySmall)
                        }

                        syncMessage?.let { msg ->
                            ButterflyCardWrapper(seed = "SyncMessageCard_$msg", modifier = Modifier.fillMaxWidth()) {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = softCardShape,
                                    border = softCardBorder()
                                ) {
                                    Text(msg, modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        }
                    }
                }
            }

            HorizontalDivider()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Health Connect Sync", style = MaterialTheme.typography.titleLarge)
                    Text(
                        "Auto-log fitness goals & Strava activities as Micro-Wins",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Switch(
                    checked = isHealthSyncEnabled,
                    onCheckedChange = { enabled ->
                        isHealthSyncEnabled = enabled
                        HealthConnectSyncPrefs.setEnabled(context, enabled)
                        if (enabled) {
                            HealthConnectSyncWorker.schedulePeriodicSync(context)
                        } else {
                            HealthConnectSyncWorker.cancelSync(context)
                        }
                    }
                )
            }

            if (isHealthSyncEnabled) {
                if (!isSdkAvailable) {
                    ButterflyCardWrapper(seed = "HealthSdkUnavailable", modifier = Modifier.fillMaxWidth()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = softCardShape,
                            border = softCardBorder()
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    "Health Connect is unavailable or not installed on this device.",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Button(
                                    onClick = { HealthConnectSyncHelper.openHealthConnectInPlayStore(context) },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Install / Update Health Connect")
                                }
                            }
                        }
                    }
                } else if (!hasHealthPermissions) {
                    ButterflyCardWrapper(seed = "HealthPermsCard", modifier = Modifier.fillMaxWidth()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = softCardShape,
                            border = softCardBorder()
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text(
                                    "Grant Health Connect access to read steps, workouts, sleep, and active calorie stats.",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Button(
                                    onClick = {
                                        try {
                                            val status = HealthConnectSyncHelper.getSdkStatus(context)
                                            if (status == HealthConnectClient.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED) {
                                                HealthConnectSyncHelper.openHealthConnectInPlayStore(context)
                                            } else {
                                                healthConnectPermissionLauncher.launch(HealthConnectSyncHelper.REQUIRED_PERMISSIONS)
                                            }
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                            Toast.makeText(context, "Opening Health Connect... Please ensure Health Connect is installed.", Toast.LENGTH_LONG).show()
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Grant Health Connect Permissions")
                                }
                            }
                        }
                    }
                } else {
                    ButterflyCardWrapper(seed = "HealthSettingsCard", modifier = Modifier.fillMaxWidth()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = softCardShape,
                            border = softCardBorder()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text("Target Goals & Categories", style = MaterialTheme.typography.titleMedium)

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = syncSteps,
                                        onCheckedChange = { checked ->
                                            syncSteps = checked
                                            HealthConnectSyncPrefs.setStepsSyncEnabled(context, checked)
                                        }
                                    )
                                    Text("👟 Daily Step Milestones", style = MaterialTheme.typography.bodyMedium)
                                }

                                if (syncSteps) {
                                    Column(modifier = Modifier.padding(start = 32.dp)) {
                                        Text("Daily Step Goal Target:", style = MaterialTheme.typography.bodySmall)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        GoalDropdownMenu(
                                            label = "Step Goal",
                                            selectedValue = stepGoal,
                                            presets = stepPresets,
                                            formatValue = { "%,d steps".format(Locale.getDefault(), it) },
                                            onSelectPreset = { preset ->
                                                stepGoal = preset
                                                HealthConnectSyncPrefs.setStepGoal(context, preset)
                                            },
                                            onSelectCustom = {
                                                customStepInput = stepGoal.toString()
                                                showCustomStepDialog = true
                                            }
                                        )
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = syncWorkouts,
                                        onCheckedChange = { checked ->
                                            syncWorkouts = checked
                                            HealthConnectSyncPrefs.setWorkoutsSyncEnabled(context, checked)
                                        }
                                    )
                                    Text("🏃 Workouts & Strava Activities", style = MaterialTheme.typography.bodyMedium)
                                }

                                if (syncWorkouts) {
                                    Column(modifier = Modifier.padding(start = 32.dp)) {
                                        Text("Minimum Workout Duration Target:", style = MaterialTheme.typography.bodySmall)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        GoalDropdownMenu(
                                            label = "Workout Duration",
                                            selectedValue = workoutMinMinutes,
                                            presets = workoutMinPresets,
                                            formatValue = { "$it mins" },
                                            onSelectPreset = { preset ->
                                                workoutMinMinutes = preset
                                                HealthConnectSyncPrefs.setWorkoutMinMinutes(context, preset)
                                            },
                                            onSelectCustom = {
                                                customWorkoutMinInput = workoutMinMinutes.toString()
                                                showCustomWorkoutMinDialog = true
                                            }
                                        )
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = syncSleep,
                                        onCheckedChange = { checked ->
                                            syncSleep = checked
                                            HealthConnectSyncPrefs.setSleepSyncEnabled(context, checked)
                                        }
                                    )
                                    Text("😴 Sleep Duration Goals", style = MaterialTheme.typography.bodyMedium)
                                }

                                if (syncSleep) {
                                    Column(modifier = Modifier.padding(start = 32.dp)) {
                                        Text("Sleep Hours Target:", style = MaterialTheme.typography.bodySmall)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        GoalDropdownMenu(
                                            label = "Sleep Goal",
                                            selectedValue = sleepGoalHours,
                                            presets = sleepPresets,
                                            formatValue = { "$it hours" },
                                            onSelectPreset = { preset ->
                                                sleepGoalHours = preset
                                                HealthConnectSyncPrefs.setSleepGoalHours(context, preset)
                                            },
                                            onSelectCustom = {
                                                customSleepInput = sleepGoalHours.toString()
                                                showCustomSleepDialog = true
                                            }
                                        )
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = syncCalories,
                                        onCheckedChange = { checked ->
                                            syncCalories = checked
                                            HealthConnectSyncPrefs.setCaloriesSyncEnabled(context, checked)
                                        }
                                    )
                                    Text("🔥 Active Calories Burned", style = MaterialTheme.typography.bodyMedium)
                                }

                                if (syncCalories) {
                                    Column(modifier = Modifier.padding(start = 32.dp)) {
                                        Text("Daily Active Calorie Target:", style = MaterialTheme.typography.bodySmall)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        GoalDropdownMenu(
                                            label = "Calorie Goal",
                                            selectedValue = calorieGoal,
                                            presets = caloriePresets,
                                            formatValue = { "%,d active kcal".format(Locale.getDefault(), it) },
                                            onSelectPreset = { preset ->
                                                calorieGoal = preset
                                                HealthConnectSyncPrefs.setCalorieGoal(context, preset)
                                            },
                                            onSelectCustom = {
                                                customCalorieInput = calorieGoal.toString()
                                                showCustomCalorieDialog = true
                                            }
                                        )
                                    }
                                }

                                HorizontalDivider()

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        "Notify when goal hit & Micro-Win logged",
                                        modifier = Modifier.weight(1f),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Switch(
                                        checked = notificationsEnabled,
                                        onCheckedChange = { enabled ->
                                            notificationsEnabled = enabled
                                            HealthConnectSyncPrefs.setNotificationsEnabled(context, enabled)
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Button(
                        onClick = {
                            viewModel.syncHealthConnectNow { count ->
                                healthSyncMessage = "Logged $count new Micro-Win${if (count == 1) "" else "s"}!"
                                lastHealthSyncedTime = HealthConnectSyncPrefs.getLastSyncedTime(context)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Sync Health Data Now")
                    }

                    if (lastHealthSyncedTime > 0L) {
                        val formattedDate = remember(lastHealthSyncedTime) {
                            SimpleDateFormat("MMM d, yyyy h:mm a", Locale.getDefault()).format(Date(lastHealthSyncedTime))
                        }
                        Text("Last health sync: $formattedDate", style = MaterialTheme.typography.bodySmall)
                    }

                    healthSyncMessage?.let { msg ->
                        ButterflyCardWrapper(seed = "HealthSyncMsg_$msg", modifier = Modifier.fillMaxWidth()) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = softCardShape,
                                border = softCardBorder()
                            ) {
                                Text(msg, modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showCustomStepDialog) {
        AlertDialog(
            onDismissRequest = { showCustomStepDialog = false },
            title = { Text("Custom Step Target") },
            text = {
                OutlinedTextField(
                    value = customStepInput,
                    onValueChange = { customStepInput = it.filter { char -> char.isDigit() } },
                    label = { Text("Steps Goal") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val value = customStepInput.toIntOrNull()
                        if (value != null && value > 0) {
                            stepGoal = value
                            HealthConnectSyncPrefs.setStepGoal(context, value)
                        }
                        showCustomStepDialog = false
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCustomStepDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showCustomWorkoutMinDialog) {
        AlertDialog(
            onDismissRequest = { showCustomWorkoutMinDialog = false },
            title = { Text("Custom Minimum Workout Duration") },
            text = {
                OutlinedTextField(
                    value = customWorkoutMinInput,
                    onValueChange = { customWorkoutMinInput = it.filter { char -> char.isDigit() } },
                    label = { Text("Duration Goal (minutes)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val value = customWorkoutMinInput.toIntOrNull()
                        if (value != null && value > 0) {
                            workoutMinMinutes = value
                            HealthConnectSyncPrefs.setWorkoutMinMinutes(context, value)
                        }
                        showCustomWorkoutMinDialog = false
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCustomWorkoutMinDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showCustomSleepDialog) {
        AlertDialog(
            onDismissRequest = { showCustomSleepDialog = false },
            title = { Text("Custom Sleep Target") },
            text = {
                OutlinedTextField(
                    value = customSleepInput,
                    onValueChange = { customSleepInput = it.filter { char -> char.isDigit() } },
                    label = { Text("Sleep Hours Goal") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val value = customSleepInput.toIntOrNull()
                        if (value != null && value > 0) {
                            sleepGoalHours = value
                            HealthConnectSyncPrefs.setSleepGoalHours(context, value)
                        }
                        showCustomSleepDialog = false
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCustomSleepDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showCustomCalorieDialog) {
        AlertDialog(
            onDismissRequest = { showCustomCalorieDialog = false },
            title = { Text("Custom Active Calorie Target") },
            text = {
                OutlinedTextField(
                    value = customCalorieInput,
                    onValueChange = { customCalorieInput = it.filter { char -> char.isDigit() } },
                    label = { Text("Active Calories (kcal) Goal") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val value = customCalorieInput.toIntOrNull()
                        if (value != null && value > 0) {
                            calorieGoal = value
                            HealthConnectSyncPrefs.setCalorieGoal(context, value)
                        }
                        showCustomCalorieDialog = false
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCustomCalorieDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun <T> GoalDropdownMenu(
    label: String,
    selectedValue: T,
    presets: List<T>,
    formatValue: (T) -> String,
    onSelectPreset: (T) -> Unit,
    onSelectCustom: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val isCustom = selectedValue !in presets

    val displayText = if (isCustom) {
        "${formatValue(selectedValue)} (Custom)"
    } else {
        formatValue(selectedValue)
    }

    Box(modifier = modifier) {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(displayText, style = MaterialTheme.typography.bodyMedium)
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Select $label"
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            presets.forEach { preset ->
                DropdownMenuItem(
                    text = { Text(formatValue(preset)) },
                    onClick = {
                        onSelectPreset(preset)
                        expanded = false
                    }
                )
            }
            DropdownMenuItem(
                text = { Text("+ Custom...") },
                onClick = {
                    expanded = false
                    onSelectCustom()
                }
            )
        }
    }
}
