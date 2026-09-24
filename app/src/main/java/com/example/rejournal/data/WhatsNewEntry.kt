package com.example.rejournal.data

data class WhatsNewEntry(
    val version: String,
    val date: String,
    val highlights: List<String>
)

object WhatsNewEntries {
    val all = listOf(
        WhatsNewEntry(
            version = "3.4.0",
            date = "25/9/2026",
            highlights = listOf(
                "Added a notification for Micro-Wins for 3-9PM (can be toggled in Settings)",
                "Added 9 multi-color themes instead of just having mono-color themes",
                "Changed the UI of the settings to look nicer",
                "Small GUI changes on the Stats screen to look nicer",
                "Fixed Search screen crashing when tapping on it"

            )
        )
    )
}