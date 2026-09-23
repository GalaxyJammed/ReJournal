package com.example.rejournal.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.rejournal.data.GoalCategory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalCategoryScreen(
    onCategoryClick: (GoalCategory) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("Find a Goal") }, navigationIcon = { BackButton(onBack) }) }
    ) { padding: PaddingValues ->
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScrollbar(scrollState)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "What are you planning to make better about yourself?",
                style = MaterialTheme.typography.titleLarge
            )
            GoalCategory.entries.forEach { category ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = softCardShape,
                    border = softCardBorder(),
                    onClick = { onCategoryClick(category) }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(category.displayName, style = MaterialTheme.typography.titleMedium)
                        Text(category.description, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}
