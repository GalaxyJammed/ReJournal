package com.example.rejournal.data

data class WhatsNewEntry(
    val version: String,
    val date: String,
    val highlights: List<String>
)

object WhatsNewEntries {
    val all = listOf(
        WhatsNewEntry(
            version = "3.7.0",
            date = "27/9/2026",
            highlights = listOf(
                "Fixed calorie tracker only tracking active calories (causing it to track calories burned in sleep and making a spike)",
                "Reworked the Sync screen to make the goals use dropdown menus instead to clutter the screen less",
                "Placed 'Time Capsules' below 'Emotional Mixtapes' instead of 'Goals' to match the theme of the cards",
                "Added notification whenever the app gets a new update (triggers once every 24 hours to check)",
                "Added 'Contact Us' section in 'About'"


            )
        )
    )
}