package com.example.rejournal.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarViewMonth
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun MainBottomBar(
    currentRoute: String?,
    onEntriesClick: () -> Unit,
    onStatsClick: () -> Unit,
    onAddClick: () -> Unit,
    onTrendClick: () -> Unit,
    onExtrasClick: () -> Unit
) {
    val extrasSelected = currentRoute == Screen.Extras.route ||
            currentRoute == Screen.Goals.route ||
            currentRoute?.startsWith("goalDetail") == true ||
            currentRoute == Screen.PhotoAlbum.route ||
            currentRoute == Screen.VoiceMemoAlbum.route ||
            currentRoute == Screen.Settings.route
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .height(64.dp)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize(),
            tonalElevation = 3.dp
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    BottomBarIcon(
                        icon = Icons.Filled.CalendarViewMonth,
                        label = "Entries",
                        selected = currentRoute == Screen.Log.route,
                        onClick = onEntriesClick
                    )
                    BottomBarIcon(
                        icon = Icons.Filled.BarChart,
                        label = "Stats",
                        selected = currentRoute == Screen.Stats.route,
                        onClick = onStatsClick
                    )
                }
                Spacer(modifier = Modifier.width(64.dp))
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    BottomBarIcon(
                        icon = Icons.Filled.ShowChart,
                        label = "Trend",
                        selected = currentRoute == Screen.Trend.route,
                        onClick = onTrendClick
                    )
                    BottomBarIcon(
                        icon = Icons.Filled.MoreHoriz,
                        label = "More",
                        selected = extrasSelected,
                        onClick = onExtrasClick
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = onAddClick,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-16).dp)
                .size(56.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Add entry")
        }
    }
}

@Composable
private fun BottomBarIcon(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(4.dp)
    ) {
        Icon(icon, contentDescription = label, tint = tint)
        Text(label, style = MaterialTheme.typography.labelSmall, color = tint)
    }
}