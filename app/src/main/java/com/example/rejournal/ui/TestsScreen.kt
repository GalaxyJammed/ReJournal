package com.example.rejournal.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.filled.CameraFront
import androidx.compose.material.icons.filled.Water
import com.example.rejournal.ui.components.ButterflyCardWrapper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestsScreen(
    onMbtiClick: () -> Unit,
    onNpiClick: () -> Unit,
    onDarkTriadClick: () -> Unit,
    onBigFiveClick: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = { CenterAlignedTopAppBar(title = { Text("Tests") }, navigationIcon = { BackButton(onBack) }) }
    ) { padding: PaddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Light self-reflection quizzes inspired by well-known psychology frameworks. These are for fun and insight only - not clinical or diagnostic tools.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            ButterflyCardWrapper(seed = "TestsMain", indexOffset = 0, modifier = Modifier.fillMaxWidth()) {
                Card(modifier = Modifier.fillMaxWidth(), shape = softCardShape, border = softCardBorder()) {
                    Column {
                        TestRow(Icons.Filled.Groups, "Personality Type (16 Types)", "40 questions", onMbtiClick)
                        HorizontalDivider()
                        TestRow(Icons.Filled.Water, "Big Five Personality Traits", "15 questions", onBigFiveClick)
                        HorizontalDivider()
                        TestRow(Icons.Filled.CameraFront, "Narcissistic Traits", "16 questions", onNpiClick)
                        HorizontalDivider()
                        TestRow(Icons.Filled.WarningAmber, "Dark Traits Reflection", "27 questions", onDarkTriadClick)
                    }
                }
            }
        }
    }
}

@Composable
private fun TestRow(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null)
            Column(modifier = Modifier.padding(start = 16.dp)) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}