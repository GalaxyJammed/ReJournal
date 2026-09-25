package com.example.rejournal.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.example.rejournal.ui.components.PastelIcon
import com.example.rejournal.ui.components.bouncyClick
import com.example.rejournal.ui.components.pressScale

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
    var popupVisible by remember { mutableStateOf(false) }
    var pendingAction by remember { mutableStateOf<(() -> Unit)?>(null) }

    LaunchedEffect(isExpanded) {
        if (isExpanded) {
            popupVisible = true
        }
    }

    LaunchedEffect(currentRoute) {
        popupVisible = false
        isExpanded = false
    }

    val dismissPopup = remember {
        { action: (() -> Unit)? ->
            pendingAction = action
            popupVisible = false
        }
    }

    val animProgress by animateFloatAsState(
        targetValue = if (popupVisible) 1f else 0f,
        animationSpec = spring(
            dampingRatio = 0.68f,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "fabExpandProgress",
        finishedListener = { finalVal ->
            if (finalVal == 0f && !popupVisible) {
                isExpanded = false
                pendingAction?.invoke()
                pendingAction = null
            }
        }
    )

    val fabRotation = 135f * animProgress

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
            dismissPopup(null)
        }

        Popup(
            alignment = Alignment.BottomCenter,
            onDismissRequest = { dismissPopup(null) },
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
                    .background(Color.Black.copy(alpha = 0.65f * animProgress))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        dismissPopup(null)
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

                    val density = LocalDensity.current
                    val logTodayDx = remember(density) { with(density) { (-78).dp.toPx() } }
                    val logTodayDy = remember(density) { with(density) { (-120).dp.toPx() } }
                    val microWinDx = remember(density) { with(density) { 78.dp.toPx() } }
                    val microWinDy = remember(density) { with(density) { (-120).dp.toPx() } }

                    OptionButton(
                        icon = Icons.AutoMirrored.Filled.MenuBook,
                        label = "Log Today",
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        onClick = {
                            dismissPopup(onLogTodayClick)
                        },
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .offset(y = (-16).dp)
                            .graphicsLayer {
                                val progress = animProgress.coerceIn(0f, 1f)
                                translationX = logTodayDx * progress
                                translationY = logTodayDy * progress
                                scaleX = 0.2f + 0.8f * progress
                                scaleY = 0.2f + 0.8f * progress
                                alpha = progress
                                rotationZ = -15f * (1f - progress)
                                transformOrigin = TransformOrigin(0.5f, 0.5f)
                            }
                    )

                    OptionButton(
                        icon = Icons.Filled.EmojiEvents,
                        label = "Micro-Win",
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        onClick = {
                            dismissPopup(onMicroWinClick)
                        },
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .offset(y = (-16).dp)
                            .graphicsLayer {
                                val progress = animProgress.coerceIn(0f, 1f)
                                translationX = microWinDx * progress
                                translationY = microWinDy * progress
                                scaleX = 0.2f + 0.8f * progress
                                scaleY = 0.2f + 0.8f * progress
                                alpha = progress
                                rotationZ = 15f * (1f - progress)
                                transformOrigin = TransformOrigin(0.5f, 0.5f)
                            }
                    )

                    val cancelFabInteraction = remember { MutableInteractionSource() }
                    FloatingActionButton(
                        onClick = { dismissPopup(null) },
                        interactionSource = cancelFabInteraction,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .offset(y = (-16).dp)
                            .size(56.dp)
                            .pressScale(cancelFabInteraction, pressedScale = 0.88f),
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

        val mainFabInteraction = remember { MutableInteractionSource() }
        FloatingActionButton(
            onClick = { isExpanded = true },
            interactionSource = mainFabInteraction,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-16).dp)
                .size(56.dp)
                .pressScale(mainFabInteraction, pressedScale = 0.88f),
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
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = modifier.bouncyClick(pressedScale = 0.90f, onClick = onClick)
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
    val scale by animateFloatAsState(
        targetValue = if (selected) 1.15f else 1.0f,
        animationSpec = spring(dampingRatio = 0.55f, stiffness = 300f),
        label = "bottomIconScale"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .bouncyClick(pressedScale = 0.85f, onClick = onClick)
            .padding(4.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
    ) {
        if (selected) {
            PastelIcon(icon, contentDescription = label)
        } else {
            Icon(icon, contentDescription = label, tint = tint)
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            color = tint
        )
    }
}
