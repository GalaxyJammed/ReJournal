package com.example.rejournal.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.rejournal.ui.components.ButterflyCardWrapper
import com.example.rejournal.ui.components.IconPill
import com.example.rejournal.ui.components.PastelIcon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    onWhatsNewClick: () -> Unit,
    onFaqClick: () -> Unit,
    onNotificationTroubleshootClick: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = { CenterAlignedTopAppBar(title = { Text("About") }, navigationIcon = { BackButton(onBack) }) }
    ) { padding: PaddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Column {
                CategoryLabelPublic("Help")
                AboutCard {
                    AboutRow(Icons.Filled.Info, "What's New", onWhatsNewClick)
                    HorizontalDivider()
                    AboutRow(Icons.Filled.QuestionAnswer, "FAQ", onFaqClick)
                }
            }

            Column {
                CategoryLabelPublic("Troubleshooting")
                AboutCard {
                    AboutRow(Icons.Filled.NotificationsActive, "Notifications not working?", onNotificationTroubleshootClick)
                }
            }
        }
    }
}

@Composable
private fun CategoryLabelPublic(text: String) {
    Text(
        text.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(bottom = 4.dp, start = 4.dp)
    )
}

@Composable
private fun AboutCard(content: @Composable ColumnScope.() -> Unit) {
    ButterflyCardWrapper(seed = "AboutCardMain", modifier = Modifier.fillMaxWidth()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = softCardShape,
            border = softCardBorder()
        ) {
            Column { content() }
        }
    }
}

@Composable
private fun AboutRow(icon: ImageVector, label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconPill(icon = icon)
            Text(label, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(start = 16.dp))
        }
        PastelIcon(Icons.Filled.ChevronRight, contentDescription = null)
    }
}