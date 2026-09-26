package com.example.rejournal.data

data class WhatsNewEntry(
    val version: String,
    val date: String,
    val highlights: List<String>
)

object WhatsNewEntries {
    val all = listOf(
        WhatsNewEntry(
            version = "3.6.0",
            date = "27/9/2026",
            highlights = listOf(
                "Added a sync with Health Connect that allows users to set goals and log them as Micro-Wins automatically",
                "Changed the notification icon to also include the face",
                "Added stickers when expanding the Note in the day logger to let you add more customization in your notes (including customs from your gallery)"


            )
        )
    )
}