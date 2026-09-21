package com.example.rejournal.data

data class WhatsNewEntry(
    val version: String,
    val date: String,
    val highlights: List<String>
)

object WhatsNewEntries {
    val all = listOf(
        WhatsNewEntry(
            version = "2.9.0",
            date = "21/9/2026",
            highlights = listOf(
                "App now guarantees at least one butterfly spawns on each screen, added butterflies to small UI elements",
                "Fixed splash screen appearing on rotation",
                "Added 4 personality tests in 'Tests' that are purely for insight and not diagnostic, meant to mimic the real tests as close as possible",
                "Added a small Onboarding screen for new users to help them learn about the app more"


            )
        )
    )
}