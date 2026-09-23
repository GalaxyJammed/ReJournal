package com.example.rejournal.data

data class WhatsNewEntry(
    val version: String,
    val date: String,
    val highlights: List<String>
)

object WhatsNewEntries {
    val all = listOf(
        WhatsNewEntry(
            version = "3.2.0",
            date = "23/9/2026",
            highlights = listOf(
                "Changed fade in/fade out animations for sliding animations",
                "Added a micro-wins area when pressing the '+' that logs small wins throughout your day and in the end of the month shows you all of them to help users be motivated",

            )
        )
    )
}