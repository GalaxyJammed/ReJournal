package com.example.rejournal.data

data class WhatsNewEntry(
    val version: String,
    val date: String,
    val highlights: List<String>
)

object WhatsNewEntries {
    val all = listOf(
        WhatsNewEntry(
            version = "2.6.0",
            date = "19/9/2026",
            highlights = listOf(
                "Sleepy Pink is now a lot more pink",
                "Slow Burgundy has more hints of red",
                "Theme Palette now shows lighter to darker colors",
                "Added a 'What's New' and 'FAQ' screen",
                "Photo Album/Voice Memos now let you download them to your device",
                "Added a notification troubleshooting area incase notifications don't come through",
                "UI changes to make the app look better"

            )
        )
    )
}