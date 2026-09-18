package com.example.rejournal.ui

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.biometric.BiometricManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.rejournal.data.ExportHelper
import com.example.rejournal.data.LockPrefs
import com.example.rejournal.notifications.ReminderPrefs
import com.example.rejournal.notifications.ReminderScheduler
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.graphics.Color
import com.example.rejournal.data.AppTheme
import com.example.rejournal.data.ThemePrefs
import com.example.rejournal.ui.theme.ThemeState
import com.example.rejournal.ui.theme.previewColorFor
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import com.example.rejournal.data.BackupHelper
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.foundation.border
import com.example.rejournal.data.MoodAppearancePrefs
import com.example.rejournal.data.MoodDisplayMode
import com.example.rejournal.data.MoodPalettes
import com.example.rejournal.ui.theme.MoodVisualsState
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CenterAlignedTopAppBar
import com.example.rejournal.ui.verticalScrollbar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: MoodViewModel, onBack: () -> Unit) {
    val context = LocalContext.current
    val entries by viewModel.allEntries.collectAsState()
    var selectedTheme by remember { mutableStateOf(ThemePrefs.getTheme(context)) }
    var darkMode by remember { mutableStateOf(ThemePrefs.isDarkMode(context)) }
    var enabled by remember { mutableStateOf(ReminderPrefs.isEnabled(context)) }
    var hour by remember { mutableStateOf(ReminderPrefs.getHour(context)) }
    var minute by remember { mutableStateOf(ReminderPrefs.getMinute(context)) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showExactAlarmDialog by remember { mutableStateOf(false) }

    var fingerprintEnabled by remember { mutableStateOf(LockPrefs.isFingerprintEnabled(context)) }
    var pinEnabled by remember { mutableStateOf(LockPrefs.isPinEnabled(context)) }
    var showPinSetupDialog by remember { mutableStateOf(false) }
    var pinInput by remember { mutableStateOf("") }
    var pinConfirmInput by remember { mutableStateOf("") }
    var pinError by remember { mutableStateOf<String?>(null) }

    var showImportConfirmDialog by remember { mutableStateOf(false) }
    var pendingImportUri by remember { mutableStateOf<android.net.Uri?>(null) }
    var importResultCount by remember { mutableStateOf<Int?>(null) }

    var moodMode by remember { mutableStateOf(MoodAppearancePrefs.getMode(context)) }
    var paletteName by remember { mutableStateOf(MoodAppearancePrefs.getPaletteName(context)) }
    var customColors by remember { mutableStateOf(MoodAppearancePrefs.getCustomColors(context)) }
    var editingColorIndex by remember { mutableStateOf<Int?>(null) }
    var hexInput by remember { mutableStateOf("") }

    fun applyPalette(name: String) {
        paletteName = name
        MoodAppearancePrefs.setPaletteName(context, name)
        val colors = if (name == "Custom") customColors else MoodPalettes.presets[name] ?: MoodPalettes.default
        MoodVisualsState.colors.value = colors
    }

    val importLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            pendingImportUri = uri
            showImportConfirmDialog = true
        }
    }

    val biometricAvailable = remember {
        BiometricManager.from(context).canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK) ==
                BiometricManager.BIOMETRIC_SUCCESS
    }

    fun saveAndSchedule() {
        ReminderPrefs.save(context, enabled, hour, minute)
        if (enabled) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (ReminderScheduler.canScheduleExact(alarmManager)) {
                ReminderScheduler.schedule(context, hour, minute)
            } else {
                showExactAlarmDialog = true
            }
        } else {
            ReminderScheduler.cancel(context)
        }
    }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        enabled = granted
        saveAndSchedule()
    }

    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("Settings") }, navigationIcon = { BackButton(onBack) }) }
    ) { padding: PaddingValues ->
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .verticalScrollbar(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            CategoryLabel("Reminders")
            SettingsCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Daily reminder", style = MaterialTheme.typography.titleMedium)
                    Switch(
                        checked = enabled,
                        onCheckedChange = { checked ->
                            if (checked && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            } else {
                                enabled = checked
                                saveAndSchedule()
                            }
                        }
                    )
                }

                if (enabled) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Reminder time", style = MaterialTheme.typography.titleMedium)
                        TextButton(onClick = { showTimePicker = true }, contentPadding = PaddingValues(0.dp)) {
                            Text(String.format("%02d:%02d", hour, minute))
                        }
                    }
                }
            }

            CategoryLabel("App Lock", topPadding = 20.dp)
            SettingsCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Fingerprint", style = MaterialTheme.typography.titleMedium)
                        if (!biometricAvailable) {
                            Text(
                                "Not available on this device",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Switch(
                        checked = fingerprintEnabled,
                        enabled = biometricAvailable,
                        onCheckedChange = { checked ->
                            fingerprintEnabled = checked
                            LockPrefs.setFingerprintEnabled(context, checked)
                        }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("PIN", style = MaterialTheme.typography.titleMedium)
                    Switch(
                        checked = pinEnabled,
                        onCheckedChange = { checked ->
                            if (checked) {
                                pinInput = ""
                                pinConfirmInput = ""
                                pinError = null
                                showPinSetupDialog = true
                            } else {
                                pinEnabled = false
                                LockPrefs.setPinEnabled(context, false)
                                LockPrefs.clearPin(context)
                            }
                        }
                    )
                }
                if (pinEnabled) {
                    TextButton(
                        onClick = {
                            pinInput = ""
                            pinConfirmInput = ""
                            pinError = null
                            showPinSetupDialog = true
                        },
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("Change PIN")
                    }
                }
            }

            CategoryLabel("Mood Appearance", topPadding = 20.dp)
            SettingsCard {
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    MoodDisplayMode.entries.forEachIndexed { index, mode ->
                        SegmentedButton(
                            shape = SegmentedButtonDefaults.itemShape(index = index, count = MoodDisplayMode.entries.size),
                            selected = moodMode == mode,
                            onClick = {
                                moodMode = mode
                                MoodAppearancePrefs.setMode(context, mode)
                                MoodVisualsState.mode.value = mode
                            }
                        ) {
                            Text(if (mode == MoodDisplayMode.EMOJI) "Emoji" else "Circle")
                        }
                    }
                }

                if (moodMode == MoodDisplayMode.CIRCLE) {
                    Text("Color palette", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 12.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        (MoodPalettes.presets.keys + "Custom").forEach { name ->
                            val previewColors = if (name == "Custom") customColors else MoodPalettes.presets[name]!!
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { applyPalette(name) },
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    previewColors.forEach { colorLong ->
                                        Box(
                                            modifier = Modifier
                                                .size(20.dp)
                                                .background(Color(colorLong), CircleShape)
                                        )
                                    }
                                }
                                Text(name, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                                if (paletteName == name) {
                                    Icon(Icons.Filled.Check, contentDescription = "Selected")
                                }
                            }
                        }
                    }

                    if (paletteName == "Custom") {
                        Text("Tap a color to set a hex value", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.padding(top = 8.dp)) {
                            customColors.forEachIndexed { index, colorLong ->
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(Color(colorLong), CircleShape)
                                        .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                                        .clickable {
                                            editingColorIndex = index
                                            hexInput = String.format("%06X", colorLong and 0xFFFFFF)
                                        }
                                )
                            }
                        }
                    }
                }
            }

            CategoryLabel("App Appearance", topPadding = 20.dp)
            SettingsCard {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(AppTheme.entries) { theme ->
                        ThemeSwatch(
                            theme = theme,
                            selected = theme == selectedTheme,
                            onClick = {
                                selectedTheme = theme
                                ThemePrefs.setTheme(context, theme)
                                ThemeState.current.value = theme
                            }
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Dark Mode", style = MaterialTheme.typography.titleMedium)
                    Switch(
                        checked = darkMode,
                        onCheckedChange = { checked ->
                            darkMode = checked
                            ThemePrefs.setDarkMode(context, checked)
                            ThemeState.darkMode.value = checked
                        }
                    )
                }
            }


            CategoryLabel("Backup", topPadding = 20.dp)
            SettingsCard {
                Button(
                    onClick = {
                        val intent = BackupHelper.exportFullBackup(context, entries)
                        context.startActivity(Intent.createChooser(intent, "Export full backup"))
                    },
                    enabled = entries.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (entries.isEmpty()) "No entries to back up" else "Export Full Backup (.zip)")
                }
                Button(
                    onClick = { importLauncher.launch(arrayOf("application/zip", "application/octet-stream", "*/*")) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Import Backup")
                }
                Button(
                    onClick = {
                        val intent = ExportHelper.exportToCsv(context, entries)
                        context.startActivity(Intent.createChooser(intent, "Export mood data"))
                    },
                    enabled = entries.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (entries.isEmpty()) "No entries to export" else "Export data as CSV")
                }
            }
        }
    }

    if (showTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = hour,
            initialMinute = minute,
            is24Hour = false
        )
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    hour = timePickerState.hour
                    minute = timePickerState.minute
                    showTimePicker = false
                    saveAndSchedule()
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) { Text("Cancel") }
            },
            text = { TimePicker(state = timePickerState) }
        )
    }

    if (showExactAlarmDialog) {
        AlertDialog(
            onDismissRequest = { showExactAlarmDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    showExactAlarmDialog = false
                    val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                        data = Uri.parse("package:${context.packageName}")
                    }
                    context.startActivity(intent)
                }) { Text("Open Settings") }
            },
            dismissButton = {
                TextButton(onClick = { showExactAlarmDialog = false }) { Text("Cancel") }
            },
            title = { Text("Allow exact alarms") },
            text = { Text("To send reminders at an exact time, please allow \"Alarms & reminders\" for this app in system settings.") }
        )
    }

    if (showPinSetupDialog) {
        AlertDialog(
            onDismissRequest = {
                if (!LockPrefs.hasPin(context)) pinEnabled = false
                showPinSetupDialog = false
            },
            confirmButton = {
                TextButton(onClick = {
                    when {
                        pinInput.length != 4 -> pinError = "PIN must be 4 digits"
                        pinInput != pinConfirmInput -> pinError = "PINs don't match"
                        else -> {
                            LockPrefs.setPin(context, pinInput)
                            LockPrefs.setPinEnabled(context, true)
                            pinEnabled = true
                            showPinSetupDialog = false
                        }
                    }
                }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = {
                    if (!LockPrefs.hasPin(context)) pinEnabled = false
                    showPinSetupDialog = false
                }) { Text("Cancel") }
            },
            title = { Text("Set a 4-digit PIN") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = pinInput,
                        onValueChange = { if (it.length <= 4 && it.all(Char::isDigit)) pinInput = it },
                        label = { Text("New PIN") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = pinConfirmInput,
                        onValueChange = { if (it.length <= 4 && it.all(Char::isDigit)) pinConfirmInput = it },
                        label = { Text("Confirm PIN") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true
                    )
                    pinError?.let {
                        Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        )
    }

    if (showImportConfirmDialog) {
        AlertDialog(
            onDismissRequest = {
                showImportConfirmDialog = false
                pendingImportUri = null
            },
            confirmButton = {
                TextButton(onClick = {
                    val uri = pendingImportUri
                    showImportConfirmDialog = false
                    if (uri != null) {
                        viewModel.importBackup(uri) { count ->
                            importResultCount = count
                        }
                    }
                }) { Text("Import") }
            },
            dismissButton = {
                TextButton(onClick = {
                    showImportConfirmDialog = false
                    pendingImportUri = null
                }) { Text("Cancel") }
            },
            title = { Text("Import backup?") },
            text = { Text("Entries for dates that already exist will be overwritten with the imported version. This can't be undone.") }
        )
    }

    importResultCount?.let { count ->
        AlertDialog(
            onDismissRequest = { importResultCount = null },
            confirmButton = {
                TextButton(onClick = { importResultCount = null }) { Text("OK") }
            },
            title = { Text("Import complete") },
            text = { Text("Imported $count entr${if (count == 1) "y" else "ies"}.") }
        )
    }

    editingColorIndex?.let { index ->
        AlertDialog(
            onDismissRequest = { editingColorIndex = null },
            confirmButton = {
                TextButton(onClick = {
                    val cleaned = hexInput.removePrefix("#").trim()
                    val parsed = cleaned.toLongOrNull(16)
                    if (parsed != null && cleaned.length == 6) {
                        val colorLong = 0xFF000000L or parsed
                        MoodAppearancePrefs.setCustomColor(context, index, colorLong)
                        customColors = MoodAppearancePrefs.getCustomColors(context)
                        if (paletteName == "Custom") MoodVisualsState.colors.value = customColors
                    }
                    editingColorIndex = null
                }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { editingColorIndex = null }) { Text("Cancel") }
            },
            title = { Text("Mood ${index + 1} color") },
            text = {
                OutlinedTextField(
                    value = hexInput,
                    onValueChange = { hexInput = it },
                    label = { Text("Hex (e.g. FF7043)") },
                    singleLine = true
                )
            }
        )
    }
}

@Composable
private fun CategoryLabel(text: String, topPadding: Dp = 0.dp) {
    Text(
        text.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(top = topPadding, bottom = 4.dp, start = 4.dp)
    )
}

@Composable
private fun SettingsCard(content: @Composable ColumnScope.() -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            content = content
        )
    }
}

@Composable
private fun ThemeSwatch(theme: AppTheme, selected: Boolean, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(previewColorFor(theme), CircleShape)
                .border(
                    width = if (selected) 3.dp else 0.dp,
                    color = MaterialTheme.colorScheme.onSurface,
                    shape = CircleShape
                )
        )
        Text(
            theme.displayName,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}