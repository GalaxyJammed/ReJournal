package com.example.rejournal.ui

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedbackScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var feedbackType by remember { mutableStateOf("Bug Report") }
    var message by remember { mutableStateOf("") }

    val categories = listOf("Bug Report", "Feature Request", "General")

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Send Feedback") },
                navigationIcon = { BackButton(onBack) }
            )
        }
    ) { padding: PaddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = "We'd love to hear from you! Report a bug or suggest a feature below. This will open your email app to send feedback directly to rejournalfeedback@gmail.com.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Category",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { category ->
                        FilterChip(
                            selected = feedbackType == category,
                            onClick = { feedbackType = category },
                            label = { Text(category) }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = message,
                onValueChange = { message = it },
                label = { Text("Describe your feedback or bug...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                maxLines = 10
            )

            Button(
                onClick = {
                    if (message.isBlank()) {
                        Toast.makeText(context, "Please enter some feedback first.", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val packageName = context.packageName
                    val packageInfo = try {
                        context.packageManager.getPackageInfo(packageName, 0)
                    } catch (e: Exception) {
                        null
                    }
                    val versionName = packageInfo?.versionName ?: "Unknown"
                    val deviceModel = Build.MODEL
                    val androidVersion = Build.VERSION.RELEASE

                    val emailBody = """
                        |Feedback Type: $feedbackType
                        |
                        |Message:
                        |$message
                        |
                        |---
                        |App Version: $versionName
                        |Device: $deviceModel (Android $androidVersion)
                    """.trimMargin()

                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "message/rfc822"
                        putExtra(Intent.EXTRA_EMAIL, arrayOf("rejournalfeedback@gmail.com"))
                        putExtra(Intent.EXTRA_SUBJECT, "[ReJournal Feedback - $feedbackType]")
                        putExtra(Intent.EXTRA_TEXT, emailBody)
                    }

                    try {
                        context.startActivity(Intent.createChooser(intent, "Send Feedback"))
                    } catch (e: Exception) {
                        Toast.makeText(context, "No email app found on device.", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Send Feedback via Email")
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
