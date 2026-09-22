package com.example.rejournal.data

data class WhatsNewEntry(
    val version: String,
    val date: String,
    val highlights: List<String>
)

object WhatsNewEntries {
    val all = listOf(
        WhatsNewEntry(
            version = "3.1.1",
            date = "23/9/2026",
            highlights = listOf(
                "Made some animations feel even smoother (There is an issue that can't be solved as this is a Github release app and not a playstore release app. Once its fully published on the playstore input lag will naturally disappear)"
            )
        )
    )
}