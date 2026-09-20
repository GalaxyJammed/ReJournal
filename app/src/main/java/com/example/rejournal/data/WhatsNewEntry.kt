package com.example.rejournal.data

data class WhatsNewEntry(
    val version: String,
    val date: String,
    val highlights: List<String>
)

object WhatsNewEntries {
    val all = listOf(
        WhatsNewEntry(
            version = "2.8.0",
            date = "19/9/2026",
            highlights = listOf(
                "Added a custom section in the Emojis Appearance area to add your own custom emojis for moods",
                "A few cards in 'More' now show a number besides them of how much of {x} you have in that card",
                "Each card has a 35% to have a variation of a miniature butterfly appear in it to give the app more soul",
                "Entering the app now adds a splash screen of the logo instead of a white screen to help the app load".
                "Fixed bug where users could spam the 7 day goal if they had at least 7 days logged",
                "Added back button on search menu"


            )
        )
    )
}