package com.example.rejournal.ui

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.example.rejournal.data.BigFiveTest
import com.example.rejournal.data.BigFiveTrait
import com.example.rejournal.data.ProfilePrefs
import com.example.rejournal.ui.components.ButterflyCardWrapper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BigFiveTestScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val age = remember { ProfilePrefs.getAge(context) }
    var currentIndex by remember { mutableStateOf(0) }
    val answers = remember { mutableStateOf(mutableMapOf<Int, Int>()) }
    var results by remember { mutableStateOf<Map<BigFiveTrait, Double>?>(null) }

    val total = BigFiveTest.questions.size
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("Big Five Personality") }, navigationIcon = { BackButton(onBack) }) }
    ) { padding: PaddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            results?.let { scores ->
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Your Traits Breakdown", style = MaterialTheme.typography.titleMedium)
                    
                    BigFiveTrait.entries.forEachIndexed { index, trait ->
                        val avg = scores[trait] ?: 3.0
                        ButterflyCardWrapper(seed = "BigFive_${trait.name}", indexOffset = index, modifier = Modifier.fillMaxWidth()) {
                            Card(modifier = Modifier.fillMaxWidth(), shape = softCardShape, border = softCardBorder()) {
                                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(trait.displayName, style = MaterialTheme.typography.titleMedium)
                                        Text(BigFiveTest.bandFor(avg), style = MaterialTheme.typography.titleMedium)
                                    }
                                    Text(
                                        trait.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        "Score: ${String.format("%.1f", avg)} / 5.0",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }

                    age?.let {
                        Text(
                            "Age reflection: Longitudinal studies show that Conscientiousness and Agreeableness typically increase as people age, while Neuroticism tends to decrease.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    Text(
                        "The Big Five (OCEAN) framework is widely used in psychological research to understand human personality variance across five stable dimensions.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                val summary = scores.entries.joinToString(", ") { "${it.key.displayName}: ${BigFiveTest.bandFor(it.value)}" }
                                val shareText = "Look at the test result I got from ReJournal in the Big Five Personality test! My traits breakdown: $summary. Discover yours with ReJournal!"
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
                        
                        OutlinedButton(
                            onClick = onBack,
                            modifier = Modifier.weight(1f)
                        ) {
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

                val question = BigFiveTest.questions[currentIndex]
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
                                        results = BigFiveTest.score(answers.value)
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
                                        results = BigFiveTest.score(answers.value)
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
