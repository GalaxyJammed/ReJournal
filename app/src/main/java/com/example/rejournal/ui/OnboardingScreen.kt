package com.example.rejournal.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rejournal.data.ProfilePrefs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(onFinish: () -> Unit) {
    val context = LocalContext.current
    var step by remember { mutableStateOf(1) }
    
    var nickname by remember { mutableStateOf("") }
    var ageText by remember { mutableStateOf("") }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (step > 1) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Center)
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.MenuBook,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                            modifier = Modifier.size(96.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Your Mindful Sanctuary",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                            .align(Alignment.BottomCenter),
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
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Filled.CalendarViewMonth, contentDescription = null, modifier = Modifier.size(24.dp))
                                    Text("Entries", style = MaterialTheme.typography.labelSmall)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Filled.BarChart, contentDescription = null, modifier = Modifier.size(24.dp))
                                    Text("Stats", style = MaterialTheme.typography.labelSmall)
                                }
                            }
                            Spacer(modifier = Modifier.width(64.dp))
                            Row(
                                modifier = Modifier.weight(1f),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.AutoMirrored.Filled.ShowChart, contentDescription = null, modifier = Modifier.size(24.dp))
                                    Text("Trend", style = MaterialTheme.typography.labelSmall)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Filled.MoreHoriz, contentDescription = null, modifier = Modifier.size(24.dp))
                                    Text("More", style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .offset(y = (-16).dp)
                            .size(56.dp)
                            .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                }
            }

            if (step == 1) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.AutoAwesome,
                        contentDescription = "Welcome",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(80.dp)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Welcome to ReJournal",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Your personal emotional journal and analytics companion. Let's get to know you first!",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(32.dp))

                    OutlinedTextField(
                        value = nickname,
                        onValueChange = { if (it.length <= ProfilePrefs.MAX_NICKNAME_LENGTH) nickname = it },
                        label = { Text("Nickname / Name") },
                        supportingText = { Text("${nickname.length} / ${ProfilePrefs.MAX_NICKNAME_LENGTH}") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = ageText,
                        onValueChange = { if (it.length <= 3 && it.all(Char::isDigit)) ageText = it },
                        label = { Text("Age (optional)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = {
                            if (nickname.isBlank()) {
                                nickname = "Journaler"
                            }
                            ProfilePrefs.save(context, nickname, ageText.toIntOrNull())
                            step = 2
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text("Get Started", fontSize = 16.sp)
                    }
                }
            }

            if (step > 1) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer { alpha = 0.99f }
                ) {
                    drawRect(color = Color.Black.copy(alpha = 0.75f))

                    val bottomBarY = size.height - 32.dp.toPx()
                    val totalWidth = size.width
                    val spacerWidthPx = 64.dp.toPx()
                    val weightsWidthPx = totalWidth - spacerWidthPx
                    val halfWeightWidthPx = weightsWidthPx / 2f

                    val targetOffset = when (step) {
                        2 -> {
                            Offset(halfWeightWidthPx * 0.25f, bottomBarY)
                        }
                        3 -> {
                            Offset(halfWeightWidthPx * 0.75f, bottomBarY)
                        }
                        4 -> Offset(totalWidth / 2f, size.height - 44.dp.toPx())
                        5 -> {
                            Offset(halfWeightWidthPx + spacerWidthPx + halfWeightWidthPx * 0.25f, bottomBarY)
                        }
                        6 -> {
                            Offset(halfWeightWidthPx + spacerWidthPx + halfWeightWidthPx * 0.75f, bottomBarY)
                        }
                        else -> Offset.Zero
                    }

                    if (targetOffset != Offset.Zero) {
                        drawCircle(
                            color = Color.Transparent,
                            radius = if (step == 4) 42.dp.toPx() else 36.dp.toPx(),
                            center = targetOffset,
                            blendMode = BlendMode.Clear
                        )
                    }
                }

                Card(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(24.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val title = when (step) {
                            2 -> "📅 Daily Entries"
                            3 -> "📊 Comprehensive Stats"
                            4 -> "✨ Log Your Day"
                            5 -> "📈 Mind Trends"
                            6 -> "🚀 Explore More Features"
                            else -> ""
                        }

                        val description = when (step) {
                            2 -> "View all your past mood logs, diary thoughts, positive memories, and entry calendars in one place."
                            3 -> "Discover detailed analytics, mood distributions, correlation metrics, and psychological calculators."
                            4 -> "Tap the central plus button any time to complete a quick questionnaire and log your current state."
                            5 -> "Analyze long-term curves, moving averages, and advanced emotional trends to visualize your growth."
                            6 -> "Access goals tracking, personality tests (MBTI, Big Five), photo/voice albums, and advanced app synchronization."
                            else -> ""
                        }

                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = description,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(24.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Step ${step - 1} of 5",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                            Button(
                                onClick = {
                                    if (step < 6) {
                                        step++
                                    } else {
                                        ProfilePrefs.setOnboarded(context, true)
                                        onFinish()
                                    }
                                }
                            ) {
                                Text(if (step == 6) "Finish" else "Next")
                            }
                        }
                    }
                }
            }
        }
    }
}
