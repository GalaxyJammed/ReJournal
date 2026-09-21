package com.example.rejournal.ui

import android.content.Intent
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.rejournal.data.NpiTest
import com.example.rejournal.ui.components.ButterflyCardWrapper
import androidx.compose.ui.platform.LocalContext
import com.example.rejournal.data.ProfilePrefs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NpiTestScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val age = remember { ProfilePrefs.getAge(context) }
    var currentIndex by remember { mutableStateOf(0) }
    val answers = remember { mutableStateOf(mutableMapOf<Int, Boolean>()) }
    var finalScore by remember { mutableStateOf<Int?>(null) }

    val total = NpiTest.questions.size

    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("Narcissistic Traits") }, navigationIcon = { BackButton(onBack) }) }
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
                    ButterflyCardWrapper(seed = "NpiResult_$score", indexOffset = 0, modifier = Modifier.fillMaxWidth()) {
                        Card(modifier = Modifier.fillMaxWidth(), shape = softCardShape, border = softCardBorder()) {
                            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("$score / ${total}", style = MaterialTheme.typography.headlineLarge)
                                Text(NpiTest.interpretation(score), style = MaterialTheme.typography.bodyMedium)

                                age?.let { a ->
                                    val comment = when {
                                        a < 25 -> "Research indicates that narcissistic scores often peak in late teens and early twenties, typically declining as people gain more life perspective in their 30s."
                                        else -> "Studies suggest that narcissistic traits generally decrease with age, reaching lower stable levels as individuals move further into adulthood."
                                    }
                                    Text(
                                        "Age reflection: $comment",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(top = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                    Text(
                        "This is a simplified, non-clinical self-reflection quiz inspired by narcissism research concepts - not a validated psychological assessment or diagnosis.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                val shareText = "Look at the test result I got from ReJournal in the Narcissistic Traits test: $score/$total. Reflect on your traits with ReJournal!"
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

                val question = NpiTest.questions[currentIndex]
                Text("Which statement describes you better?", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 8.dp))

                fun advance(choseB: Boolean) {
                    answers.value[question.id] = choseB
                    if (currentIndex < total - 1) {
                        currentIndex++
                    } else {
                        finalScore = NpiTest.score(answers.value)
                    }
                }

                val currentAnswer = answers.value[question.id]

                if (currentAnswer == false) {
                    Button(onClick = { advance(false) }, modifier = Modifier.fillMaxWidth()) {
                        Text(question.optionA)
                    }
                } else {
                    OutlinedButton(onClick = { advance(false) }, modifier = Modifier.fillMaxWidth()) {
                        Text(question.optionA)
                    }
                }

                if (currentAnswer == true) {
                    Button(onClick = { advance(true) }, modifier = Modifier.fillMaxWidth()) {
                        Text(question.optionB)
                    }
                } else {
                    OutlinedButton(onClick = { advance(true) }, modifier = Modifier.fillMaxWidth()) {
                        Text(question.optionB)
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