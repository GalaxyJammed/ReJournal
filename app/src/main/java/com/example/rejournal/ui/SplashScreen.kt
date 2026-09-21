package com.example.rejournal.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.rejournal.R
import com.example.rejournal.RejournalApplication
import kotlinx.coroutines.delay

private const val MIN_SPLASH_DURATION_MS = 1000L

@Composable
fun SplashGate(content: @Composable () -> Unit) {
    val context = LocalContext.current
    val app = context.applicationContext as RejournalApplication
    var showSplash by remember { mutableStateOf(!app.hasShownSplashThisSession) }

    LaunchedEffect(Unit) {
        if (showSplash) {
            delay(MIN_SPLASH_DURATION_MS)
            app.hasShownSplashThisSession = true
            showSplash = false
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        content()

        AnimatedVisibility(
            visible = showSplash,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.app_logo),
                    contentDescription = "ReJournal",
                    modifier = Modifier
                        .padding(48.dp)
                )
            }
        }
    }
}