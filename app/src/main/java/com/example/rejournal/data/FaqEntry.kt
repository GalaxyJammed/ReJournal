package com.example.rejournal.data

data class FaqEntry(val question: String, val answer: String)

object FaqEntries {
    val all = listOf(
        FaqEntry(
            "How do I log a day?",
            "Tap the + button on the bottom bar, or tap any day on the calendar. Pick a mood, adjust the sliders, add activities, photos, voice memos, or notes, then tap Save."
        ),
        FaqEntry(
            "Can I edit or delete a day I already logged?",
            "Yes, tap that day again to reopen it. You can change anything and tap Save, or tap Delete to remove the entry entirely."
        ),
        FaqEntry(
            "How do I mark a day as Important?",
            "Tap on a future date on the calendar. Since future days can't be logged yet, you'll be offered the option to mark it Important with an optional custom message, you'll get a notification when that day arrives."
        ),
        FaqEntry(
            "How do I mark a day as a Favorite?",
            "Open any day you can log (today or a past day) and tap the heart icon in the top-right corner, next to the date."
        ),
        FaqEntry(
            "What's the difference between Important Days and Favorite Days?",
            "Important Days are for future dates you want a reminder about, with a custom message. Favorite Days are for past or present days you've already logged and want to look back on."
        ),
        FaqEntry(
            "How do streaks work?",
            "Your streak counts consecutive days logged. If you logged yesterday but haven't logged today yet, your streak still shows as active, it only resets once a full day is skipped."
        ),
        FaqEntry(
            "How do Goals work?",
            "Go to More > Goals and tap \"Find a goal that suits you\" to pick one from a category. You can have up to 3 active goals at once — complete or cancel one to make room for another."
        ),
        FaqEntry(
            "What are Time Capsules?",
            "Messages you write to your future self. A Mood Capsule delivers the next time you log a specific mood; a Time Capsule delivers on a specific future date you choose."
        ),
        FaqEntry(
            "How do Achievements work?",
            "Achievements unlock automatically as you use the app — logging entries, building streaks, completing goals, adding photos or memos, and more. Check More > Achievements to see your progress."
        ),
        FaqEntry(
            "Can I change how mood is displayed?",
            "Yes, in Settings > Mood Appearance, switch between emoji and colored circles, and pick from several color palettes or set your own custom colors."
        ),
        FaqEntry(
            "How do I change the app's theme?",
            "Settings > App Appearance lets you pick a color theme and toggle Dark Mode."
        ),
        FaqEntry(
            "Is my data private?",
            "Yes, everything is stored locally on your device. Nothing is sent anywhere unless you choose to export or back it up yourself."
        ),
        FaqEntry(
            "How do I back up my data or move it to a new phone?",
            "Settings > Backup > \"Export Full Backup (.zip)\" saves everything, including photos and voice memos. On your new device, use \"Import Backup\" and select that file."
        ),
        FaqEntry(
            "Can I lock the app with a PIN or fingerprint?",
            "Yes, Settings > App Lock lets you enable either or both. If both are on, fingerprint is tried first with PIN as a backup."
        ),
        FaqEntry(
            "How does calendar sync work?",
            "More > Sync lets you connect your device calendar. It checks for events for the rest of the current month and adds them as Important Days automatically, without overwriting any you've already set manually."
        ),
        FaqEntry(
            "What is the Constellation view?",
            "In Trend, tap \"Create Constellation\" for an artistic star-map version of your mood trend, where star size reflects how much you logged that day. You can save it as an image."
        )
    )
}