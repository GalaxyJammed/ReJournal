package com.example.rejournal.data

data class WhatsNewEntry(
    val version: String,
    val date: String,
    val highlights: List<String>
)

object WhatsNewEntries {
    val all = listOf(
        WhatsNewEntry(
            version = "2.7.0",
            date = "19/9/2026",
            highlights = listOf(
                "Added an outline to cards to make them look nicer",
                "Changed the way Constellation works for a cleaner and nicer look",
                "Finally added version checking. If you have an outdated version it will let you know in the main menu.",
                "Trend screen now only shows 7 latest days, clicking on the trend screen shows the entire month",
                "Instead of a 1,5 number trend screen now copies your personal style of mood icons (emojis/circles)",
                "Deleting a log now adds a popup asking for confirmation before actually deleting"


            )
        )
    )
}