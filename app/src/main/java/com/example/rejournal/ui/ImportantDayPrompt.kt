package com.example.rejournal.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.unit.dp
import com.example.rejournal.data.ImportantDay
import java.time.LocalDate

@Composable
fun ImportantDayPrompt(
    date: LocalDate,
    existing: ImportantDay?,
    onMark: (String) -> Unit,
    onRemove: () -> Unit,
    onCancel: () -> Unit
) {
    var message by remember { mutableStateOf(existing?.message ?: "") }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .blur(18.dp)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(5) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                    )
                }
            }
            repeat(4) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .size(24.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background.copy(alpha = 0.4f)),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("You can't log this day yet", style = MaterialTheme.typography.titleLarge)
                    Text(
                        if (existing != null) "This day is marked important. Edit its message or remove it below."
                        else "Would you like to mark it as an important day? You'll get a notification when it arrives.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    OutlinedTextField(
                        value = message,
                        onValueChange = { message = it },
                        label = { Text(if (existing?.message?.isNotBlank() == true) "Edit message" else "Custom message (optional)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Button(
                        onClick = { onMark(message) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (existing != null) "Update" else "Mark Important")
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(onClick = onCancel, modifier = Modifier.weight(1f)) {
                            Text("Close")
                        }
                        if (existing != null) {
                            OutlinedButton(onClick = onRemove, modifier = Modifier.weight(1f)) {
                                Text("Remove")
                            }
                        }
                    }
                }
            }
        }
    }
}