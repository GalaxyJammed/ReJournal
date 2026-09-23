package com.example.rejournal.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarViewMonth
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.example.rejournal.ui.components.PastelIcon

@Composable
fun MainBottomBar(
    currentRoute: String?,
    onEntriesClick: () -> Unit,
    onStatsClick: () -> Unit,
    onLogTodayClick: () -> Unit,
    onMicroWinClick: () -> Unit,
    onTrendClick: () -> Unit,
    onExtrasClick: () -> Unit
) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }

    val fabRotation by animateFloatAsState(
        targetValue = if (isExpanded) 135f else 0f,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
        label = "fabRotation"
    )

    LaunchedEffect(currentRoute) {
        isExpanded = false
    }

    val extrasSelected = currentRoute == Screen.Extras.route ||
            currentRoute == Screen.Goals.route ||
            currentRoute?.startsWith("goalDetail") == true ||
            currentRoute == Screen.PhotoAlbum.route ||
            currentRoute == Screen.VoiceMemoAlbum.route ||
            currentRoute == Screen.Settings.route

    val bottomBarRoutes = remember {
        setOf(
            Screen.Log.route, Screen.Stats.route, Screen.Trend.route,
            Screen.Extras.route,
        )
    }
    val isVisible = currentRoute in bottomBarRoutes

    if (!isVisible) return

    if (isExpanded) {
        BackHandler(enabled = true) {
            isExpanded = false
        }

        Popup(
            alignment = Alignment.BottomCenter,
            onDismissRequest = { isExpanded = false },
            properties = PopupProperties(
                focusable = true,
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
                clippingEnabled = false,
                usePlatformDefaultWidth = false
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.65f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        isExpanded = false
                    }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .height(64.dp)
                    )

                    Row(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .offset(y = (-136).dp),
                        horizontalArrangement = Arrangement.spacedBy(36.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OptionButton(
                            icon = Icons.AutoMirrored.Filled.MenuBook,
                            label = "Log Today",
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            onClick = {
                                isExpanded = false
                                onLogTodayClick()
                            }
                        )

                        OptionButton(
                            icon = Icons.Filled.EmojiEvents,
                            label = "Micro-Win",
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            onClick = {
                                isExpanded = false
                                onMicroWinClick()
                            }
                        )
                    }

                    FloatingActionButton(
                        onClick = { isExpanded = false },
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .offset(y = (-16).dp)
                            .size(56.dp),
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Cancel action",
                            modifier = Modifier
                                .size(28.dp)
                                .graphicsLayer { rotationZ = fabRotation }
                        )
                    }
                }
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            tonalElevation = 3.dp
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .height(64.dp)
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
                            icon = Icons.AutoMirrored.Filled.ShowChart,
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
        }

        FloatingActionButton(
            onClick = { isExpanded = true },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-16).dp)
                .size(56.dp),
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Add options",
                modifier = Modifier
                    .size(28.dp)
                    .graphicsLayer { rotationZ = fabRotation }
            )
        }
    }
}

@Composable
private fun OptionButton(
    icon: ImageVector,
    label: String,
    containerColor: Color,
    contentColor: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Surface(
            shape = CircleShape,
            color = containerColor,
            tonalElevation = 6.dp,
            shadowElevation = 8.dp,
            modifier = Modifier.size(64.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = contentColor,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 4.dp,
            shadowElevation = 4.dp
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            )
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
        if (selected) {
            PastelIcon(icon, contentDescription = label)
        } else {
            Icon(icon, contentDescription = label, tint = tint)
        }
        Text(label, style = MaterialTheme.typography.labelSmall, color = tint)
    }
}
