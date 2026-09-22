package com.example.rejournal.data

data class WhatsNewEntry(
    val version: String,
    val date: String,
    val highlights: List<String>
)

object WhatsNewEntries {
    val all = listOf(
        WhatsNewEntry(
            version = "3.1.0",
            date = "22/9/2026",
            highlights = listOf(
                "Minor GUI changes",
                "Added 2 more personality tests",
                "Added more goals",
                "Added more achievements and expanded on some current ones",
                "Added a filter option to the achievements screen",
                "Added some screen animations to make the app feel more lively"

            )
        )
    )
}