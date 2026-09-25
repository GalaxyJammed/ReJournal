package com.example.rejournal.data

data class WhatsNewEntry(
    val version: String,
    val date: String,
    val highlights: List<String>
)

object WhatsNewEntries {
    val all = listOf(
        WhatsNewEntry(
            version = "3.5.0",
            date = "26/9/2026",
            highlights = listOf(
                "Made the Stats screen prettier",
                "Added a 'Journaling Highlights' in 'Additional Stats' shows statistics about the notes written",
                "Changed App Logo to a more friendly one",
                "Added some more quality of life animations to make the app more lively",
                "Fixed Achievements and Goals not tracking properly"

            )
        )
    )
}