package com.example.rejournal.ui

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.example.rejournal.data.LockPrefs

private const val PIN_LENGTH = 4

private val warmGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFFFFF3E0), Color(0xFFFFE0B2))
)

@Composable
fun LockScreen(activity: FragmentActivity, onUnlocked: () -> Unit) {
    val context = LocalContext.current

    val fingerprintEnabled = remember { LockPrefs.isFingerprintEnabled(context) }
    val pinEnabled = remember { LockPrefs.isPinEnabled(context) }

    val biometricManager = remember { BiometricManager.from(context) }
    val biometricAvailable = remember {
        biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK) ==
                BiometricManager.BIOMETRIC_SUCCESS
    }
    val canUseFingerprint = fingerprintEnabled && biometricAvailable

    var enteredPin by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val biometricPrompt = remember {
        BiometricPrompt(
            activity,
            ContextCompat.getMainExecutor(context),
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    onUnlocked()
                }
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    if (!pinEnabled) errorMessage = "Fingerprint failed. Try again."
                }
            }
        )
    }

    val promptInfo = remember {
        BiometricPrompt.PromptInfo.Builder()
            .setTitle("Unlock ReJournal")
            .setSubtitle(if (pinEnabled) "Use your fingerprint, or enter your PIN below" else "Use your fingerprint")
            .apply {
                if (pinEnabled) setNegativeButtonText("Use PIN") else setNegativeButtonText("Cancel")
            }
            .build()
    }

    LaunchedEffect(Unit) {
        if (canUseFingerprint) {
            biometricPrompt.authenticate(promptInfo)
        }
    }

    fun onDigitEntered(digit: String) {
        if (enteredPin.length >= PIN_LENGTH) return
        errorMessage = null
        enteredPin += digit
        if (enteredPin.length == PIN_LENGTH) {
            if (LockPrefs.verifyPin(context, enteredPin)) {
                onUnlocked()
            } else {
                errorMessage = "Incorrect PIN"
                enteredPin = ""
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(warmGradient)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (!pinEnabled) {
            Text("Unlock ReJournal", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(24.dp))
            Icon(
                Icons.Filled.Fingerprint,
                contentDescription = "Use fingerprint",
                modifier = Modifier
                    .size(72.dp)
                    .clickable { biometricPrompt.authenticate(promptInfo) }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                errorMessage ?: "Tap to unlock with fingerprint",
                style = MaterialTheme.typography.bodyMedium
            )
            return@Column
        }

        Text("Enter PIN", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(24.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            repeat(PIN_LENGTH) { index ->
                val filled = index < enteredPin.length
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(
                            if (filled) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.5f),
                            CircleShape
                        )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            errorMessage ?: " ",
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(24.dp))

        val rows = listOf(
            listOf("1", "2", "3"),
            listOf("4", "5", "6"),
            listOf("7", "8", "9"),
            listOf(if (canUseFingerprint) "fingerprint" else "", "0", "backspace")
        )

        rows.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                row.forEach { key ->
                    KeypadButton(key = key, onDigitEntered = ::onDigitEntered) {
                        when (key) {
                            "backspace" -> {
                                errorMessage = null
                                enteredPin = enteredPin.dropLast(1)
                            }
                            "fingerprint" -> biometricPrompt.authenticate(promptInfo)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun KeypadButton(
    key: String,
    onDigitEntered: (String) -> Unit,
    onSpecialKey: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(64.dp)
            .clickable(enabled = key.isNotEmpty()) {
                if (key.toIntOrNull() != null) onDigitEntered(key) else onSpecialKey()
            },
        contentAlignment = Alignment.Center
    ) {
        when {
            key == "backspace" -> Icon(Icons.Filled.Backspace, contentDescription = "Delete")
            key == "fingerprint" -> Icon(Icons.Filled.Fingerprint, contentDescription = "Use fingerprint")
            key.isNotEmpty() -> Text(key, style = MaterialTheme.typography.headlineMedium)
        }
    }
}