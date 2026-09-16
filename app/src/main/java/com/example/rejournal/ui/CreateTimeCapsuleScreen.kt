package com.example.rejournal.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import com.example.rejournal.data.CapsuleType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTimeCapsuleScreen(
    viewModel: MoodViewModel,
    onDone: () -> Unit,
    onBack: () -> Unit
) {
    var type by remember { mutableStateOf(CapsuleType.MOOD) }
    var selectedMood by remember { mutableStateOf<Int?>(null) }
    var daysFromNowText by remember { mutableStateOf("7") }
    var message by remember { mutableStateOf("") }

    val canSave = message.isNotBlank() &&
            (type == CapsuleType.MOOD && selectedMood != null ||
                    type == CapsuleType.TIME && daysFromNowText.toIntOrNull()?.let { it > 0 } == true)

    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("New Time Capsule") }, navigationIcon = { BackButton(onBack) }) }
    ) { padding: PaddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                CapsuleType.entries.forEachIndexed { index, option ->
                    SegmentedButton(
                        shape = SegmentedButtonDefaults.itemShape(index = index, count = CapsuleType.entries.size),
                        selected = type == option,
                        onClick = { type = option }
                    ) {
                        Text(if (option == CapsuleType.MOOD) "Mood" else "Time")
                    }
                }
            }

            if (type == CapsuleType.MOOD) {
                Text("Deliver the next time you log this mood:", style = MaterialTheme.typography.titleMedium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    (1..5).forEach { moodValue ->
                        FilterChip(
                            selected = selectedMood == moodValue,
                            onClick = { selectedMood = moodValue },
                            label = { MoodGlyph(moodValue) }
                        )
                    }
                }
            } else {
                Text("Deliver in how many days?", style = MaterialTheme.typography.titleMedium)
                OutlinedTextField(
                    value = daysFromNowText,
                    onValueChange = { if (it.all(Char::isDigit)) daysFromNowText = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Text("Message to your future self", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = message,
                onValueChange = { message = it },
                placeholder = { Text("Write something you'll want to read later...") },
                modifier = Modifier.fillMaxWidth().height(140.dp)
            )

            Button(
                onClick = {
                    if (type == CapsuleType.MOOD) {
                        viewModel.createMoodCapsule(selectedMood!!, message.trim())
                    } else {
                        viewModel.createTimeCapsule(daysFromNowText.toInt(), message.trim())
                    }
                    onDone()
                },
                enabled = canSave,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Time Capsule")
            }
        }
    }
}