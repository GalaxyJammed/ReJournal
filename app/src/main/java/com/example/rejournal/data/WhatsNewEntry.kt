package com.example.rejournal.data

data class WhatsNewEntry(
    val version: String,
    val date: String,
    val highlights: List<String>
)

object WhatsNewEntries {
    val all = listOf(
        WhatsNewEntry(
            version = "3.3.0",
            date = "24/9/2026",
            highlights = listOf(
                "Made the Emotional Mixtape screen prettier",
                "Added More goals and Achievements tied to Micro-Wins and Emotional Mixtapes",
                "Reworked the Goals screen a tiny bit to make the cards look nicer"

            )
        )
    )
}