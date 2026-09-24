package com.example.rejournal.data

data class WhatsNewEntry(
    val version: String,
    val date: String,
    val highlights: List<String>
)

object WhatsNewEntries {
    val all = listOf(
        WhatsNewEntry(
            version = "3.3.1",
            date = "24/9/2026",
            highlights = listOf(
                "Fixed butterflies not appearing randomly on screen entry"

            )
        )
    )
}