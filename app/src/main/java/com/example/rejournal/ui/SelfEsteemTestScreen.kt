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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.rejournal.data.SelfEsteemTest
import com.example.rejournal.data.TestResultPrefs
import com.example.rejournal.ui.components.ButterflyCardWrapper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelfEsteemTestScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var currentIndex by remember { mutableStateOf(0) }
    val answers = remember { mutableStateOf(mutableMapOf<Int, Int>()) }
    var finalScore by remember { mutableStateOf<Int?>(null) }

    val total = SelfEsteemTest.questions.size

    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("Self-Esteem Quiz") }, navigationIcon = { BackButton(onBack) }) }
    ) { padding: PaddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            finalScore?.let { score ->
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Your Result", style = MaterialTheme.typography.titleMedium)
                    ButterflyCardWrapper(seed = "SelfEsteemResult", indexOffset = 0, modifier = Modifier.fillMaxWidth()) {
                        Card(modifier = Modifier.fillMaxWidth(), shape = softCardShape, border = softCardBorder()) {
                            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("Score: $score / 50", style = MaterialTheme.typography.headlineMedium)
                                Text(SelfEsteemTest.interpretation(score), style = MaterialTheme.typography.bodyLarge)
                            }
                        }
                    }
                    Text(
                        "This quiz is based on the Rosenberg Self-Esteem Scale, a widely used tool for assessing self-worth, but this version is for self-reflection only.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                val shareText = "I just took the Self-Esteem Quiz on ReJournal and got a score of $score! It's a great tool for self-reflection."
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
                            Text("Share")
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

                val question = SelfEsteemTest.questions[currentIndex]
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
                                        finalScore = SelfEsteemTest.score(answers.value)
                                        TestResultPrefs.incrementTestsCompletedCount(context)
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
                                        finalScore = SelfEsteemTest.score(answers.value)
                                        TestResultPrefs.incrementTestsCompletedCount(context)
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
