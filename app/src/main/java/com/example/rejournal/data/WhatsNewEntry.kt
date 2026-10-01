package com.example.rejournal.data

data class WhatsNewEntry(
    val version: String,
    val date: String,
    val highlights: List<String>
)

object WhatsNewEntries {
    val all = listOf(
        WhatsNewEntry(
            version = "3.7.3",
            date = "27/9/2026",
            highlights = listOf(
                "Fixed Micro-Win screen getting cluttered if there were 2+ months logged with Micro-Wins",
                "Fixed Health Connect sync sometimes giving duplicated Micro-Wins"


            )
        )
    )
}