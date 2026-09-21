package com.example.rejournal.ui

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.example.rejournal.ui.components.IconPill
import com.example.rejournal.ui.components.PastelIcon
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.TravelExplore
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.rejournal.data.ToolkitPrefs
import kotlinx.coroutines.delay

private enum class ToolkitScreen { MENU, BREATHING, GROUNDING, MEDIA, JOURNAL }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolkitDialog(onDismiss: () -> Unit) {
    var screen by remember { mutableStateOf(ToolkitScreen.MENU) }

    if (screen != ToolkitScreen.MENU) {
        BackHandler {
            screen = ToolkitScreen.MENU
        }
    }

    Dialog(
        onDismissRequest = {
            if (screen != ToolkitScreen.MENU) {
                screen = ToolkitScreen.MENU
            } else {
                onDismiss()
            }
        },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("A Moment for You") },
                    navigationIcon = {
                        if (screen != ToolkitScreen.MENU) {
                            BackButton(onBack = { screen = ToolkitScreen.MENU })
                        }
                    },
                    actions = {
                        TextButton(onClick = onDismiss) { Text("Close") }
                    }
                )
            }
        ) { padding: PaddingValues ->
            Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                when (screen) {
                    ToolkitScreen.MENU -> ToolkitMenu(onNavigate = { screen = it })
                    ToolkitScreen.BREATHING -> BreathingExercise()
                    ToolkitScreen.GROUNDING -> GroundingExercise()
                    ToolkitScreen.MEDIA -> ComfortMediaScreen()
                    ToolkitScreen.JOURNAL -> ShreddingJournal()
                }
            }
        }
    }
}

@Composable
private fun ToolkitMenu(onNavigate: (ToolkitScreen) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "It looks like today was rough. Here are a few things that might help, if you'd like.",
            style = MaterialTheme.typography.bodyMedium
        )

        ToolkitMenuItem(Icons.Filled.Air, "Breathing Exercise", "A slow, guided breathing pattern") { onNavigate(ToolkitScreen.BREATHING) }
        ToolkitMenuItem(Icons.Filled.TravelExplore, "5-4-3-2-1 Grounding", "Reconnect with your senses, one step at a time") { onNavigate(ToolkitScreen.GROUNDING) }
        ToolkitMenuItem(Icons.Filled.Link, "Comfort Media", "Save a photo, video, or playlist to revisit") { onNavigate(ToolkitScreen.MEDIA) }
        ToolkitMenuItem(Icons.Filled.Delete, "Shredding Journal", "Vent freely - nothing here is ever saved") { onNavigate(ToolkitScreen.JOURNAL) }

        Text(
            "If things feel like more than a rough day, it always helps to reach out to someone you trust or a professional.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
private fun ToolkitMenuItem(icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = softCardShape,
        border = softCardBorder(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                IconPill(icon = icon)
                Column(modifier = Modifier.padding(start = 16.dp)) {
                    Text(title, style = MaterialTheme.typography.titleMedium)
                    Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            PastelIcon(Icons.Filled.ChevronRight, contentDescription = null)
        }
    }
}

@Composable
private fun BreathingExercise() {
    var inhaling by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(4000)
            inhaling = !inhaling
        }
    }

    val scale by animateFloatAsState(
        targetValue = if (inhaling) 1f else 0.55f,
        animationSpec = tween(durationMillis = 4000, easing = LinearEasing),
        label = "breathingScale"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier.size(220.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(220.dp * scale)
                    .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
            )
            Text(
                if (inhaling) "Breathe In" else "Breathe Out",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        Text(
            "Follow the circle. In for 4 seconds, out for 4 seconds.",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 32.dp)
        )
    }
}

private data class GroundingStep(val count: Int, val sense: String, val instruction: String)

private val groundingSteps = listOf(
    GroundingStep(5, "see", "Name 5 things you can see around you right now."),
    GroundingStep(4, "touch", "Name 4 things you can physically touch or feel."),
    GroundingStep(3, "hear", "Name 3 things you can hear right now."),
    GroundingStep(2, "smell", "Name 2 things you can smell."),
    GroundingStep(1, "taste", "Name 1 thing you can taste.")
)

@Composable
private fun GroundingExercise() {
    var stepIndex by remember { mutableStateOf(0) }
    val step = groundingSteps[stepIndex]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text("Step ${stepIndex + 1} of ${groundingSteps.size}", style = MaterialTheme.typography.labelMedium)
        Text("${step.count}", style = MaterialTheme.typography.displayLarge, color = MaterialTheme.colorScheme.primary)
        Text(step.instruction, style = MaterialTheme.typography.titleLarge)

        Button(
            onClick = { if (stepIndex < groundingSteps.size - 1) stepIndex++ },
            enabled = stepIndex < groundingSteps.size - 1,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (stepIndex < groundingSteps.size - 1) "Next" else "Finished")
        }
    }
}

@Composable
private fun ComfortMediaScreen() {
    val context = LocalContext.current
    var link by remember { mutableStateOf(ToolkitPrefs.getMediaLink(context) ?: "") }
    var label by remember { mutableStateOf(ToolkitPrefs.getMediaLabel(context) ?: "") }
    val savedLink = ToolkitPrefs.getMediaLink(context)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "Link a photo, video, playlist, or anything else you can return to on a hard day.",
            style = MaterialTheme.typography.bodyMedium
        )

        if (!savedLink.isNullOrBlank()) {
            OutlinedButton(
                onClick = {
                    try {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(savedLink)))
                    } catch (e: Exception) {
                        Toast.makeText(context, "Couldn't open this link", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(ToolkitPrefs.getMediaLabel(context)?.takeIf { it.isNotBlank() } ?: "Open Saved Media")
            }
        }

        OutlinedTextField(
            value = label,
            onValueChange = { label = it },
            label = { Text("Label (optional)") },
            placeholder = { Text("e.g. Our trip to the beach") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = link,
            onValueChange = { link = it },
            label = { Text("Link (URL)") },
            placeholder = { Text("Paste a link to a photo, video, or playlist") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                if (link.isNotBlank()) {
                    ToolkitPrefs.saveMedia(context, link, label)
                    Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show()
                }
            },
            enabled = link.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Link")
        }

        if (!savedLink.isNullOrBlank()) {
            TextButton(
                onClick = {
                    ToolkitPrefs.clearMedia(context)
                    link = ""
                    label = ""
                }
            ) {
                Text("Remove saved link")
            }
        }
    }
}

@Composable
private fun ShreddingJournal() {
    var text by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            "Write whatever you need to. This is never saved anywhere - it disappears the moment you leave this screen.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            placeholder = { Text("Let it out...") },
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        )
        Button(
            onClick = { text = "" },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Shred It")
        }
    }
}