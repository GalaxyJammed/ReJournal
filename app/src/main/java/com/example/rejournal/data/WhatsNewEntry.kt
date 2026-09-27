package com.example.rejournal.data

data class WhatsNewEntry(
    val version: String,
    val date: String,
    val highlights: List<String>
)

object WhatsNewEntries {
    val all = listOf(
        WhatsNewEntry(
            version = "3.7.1",
            date = "27/9/2026",
            highlights = listOf(
                "Fixed Goals not working properly with Health Connect sync"


            )
        )
    )
}