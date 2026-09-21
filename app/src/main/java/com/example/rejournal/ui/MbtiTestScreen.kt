package com.example.rejournal.ui

import android.content.Intent

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.rejournal.data.MbtiResult
import com.example.rejournal.data.MbtiTest
import com.example.rejournal.ui.components.ButterflyCardWrapper
import androidx.compose.ui.platform.LocalContext
import com.example.rejournal.data.ProfilePrefs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MbtiTestScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val age = remember { ProfilePrefs.getAge(context) }
    var currentIndex by remember { mutableStateOf(0) }
    val answers = remember { mutableStateOf(mutableMapOf<Int, Int>()) }
    var result by remember { mutableStateOf<MbtiResult?>(null) }

    val total = MbtiTest.questions.size

    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("Personality Type") }, navigationIcon = { BackButton(onBack) }) }
    ) { padding: PaddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            result?.let { r ->
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Your Type", style = MaterialTheme.typography.titleMedium)
                    ButterflyCardWrapper(seed = "MbtiResult_${r.type}", indexOffset = 0, modifier = Modifier.fillMaxWidth()) {
                        Card(modifier = Modifier.fillMaxWidth(), shape = softCardShape, border = softCardBorder()) {
                            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(r.type, style = MaterialTheme.typography.headlineLarge)
                                Text(r.nickname, style = MaterialTheme.typography.titleMedium)
                                Text(r.description, style = MaterialTheme.typography.bodyMedium)
                                
                                age?.let {
                                    Text(
                                        "Age reflection: Research on personality development suggests that while core types like $r.type are stable, people often become more emotionally balanced as they progress through adulthood.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(top = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                    Text(
                        "This is a simplified, non-clinical self-reflection quiz inspired by common personality typology ideas - not a validated psychological assessment.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                val shareText = "Look at the test result I got from ReJournal in the Personality Type test: I am an ${r.type} (${r.nickname})! Check out your personality with ReJournal."
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, shareText)
                                    type = "text/plain"
                                }
                                val shareIntent = Intent.createChooser(sendIntent, null)
                                context.startActivity(shareIntent)
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Share Results")
                        }
                        OutlinedButton(onClick = onBack, modifier = Modifier.weight(1f)) {
                            Text("Done")
                        }
                    }
                }
            } ?: run {
                Text("$currentIndex / $total questions", style = MaterialTheme.typography.labelMedium)
                LinearProgressIndicator(
                    progress = { currentIndex.toFloat() / total },
                    modifier = Modifier.fillMaxWidth(),
                    gapSize = 0.dp,
                    drawStopIndicator = {}
                )

                val question = MbtiTest.questions[currentIndex]
                Text(question.text, style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(top = 8.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    val labels = listOf("Strongly Disagree", "Disagree", "Neutral", "Agree", "Strongly Agree")
                    val currentAnswer = answers.value[question.id]
                    
                    labels.forEachIndexed { i, label ->
                        val value = i + 1
                        val isSelected = currentAnswer == value
                        if (isSelected) {
                            Button(
                                onClick = {
                                    answers.value[question.id] = value
                                    if (currentIndex < total - 1) {
                                        currentIndex++
                                    } else {
                                        result = MbtiTest.score(answers.value)
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(label)
                            }
                        } else {
                            OutlinedButton(
                                onClick = {
                                    answers.value[question.id] = value
                                    if (currentIndex < total - 1) {
                                        currentIndex++
                                    } else {
                                        result = MbtiTest.score(answers.value)
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(label)
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedButton(
                        onClick = { if (currentIndex > 0) currentIndex-- },
                        enabled = currentIndex > 0
                    ) {
                        Text("Previous")
                    }
                    OutlinedButton(
                        onClick = { if (currentIndex < total - 1) currentIndex++ },
                        enabled = answers.value.containsKey(question.id) && currentIndex < total - 1
                    ) {
                        Text("Next")
                    }
                }
            }
        }
    }
}