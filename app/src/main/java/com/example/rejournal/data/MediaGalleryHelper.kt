package com.example.rejournal.data

import java.time.LocalDate

data class MediaItem(val date: LocalDate, val path: String)

object MediaGalleryHelper {
    fun allPhotos(entries: List<MoodEntry>): List<MediaItem> =
        entries.flatMap { entry -> entry.photoPaths.map { path -> MediaItem(entry.date, path) } }
            .sortedByDescending { it.date }

    fun allAudioMemos(entries: List<MoodEntry>): List<MediaItem> =
        entries.flatMap { entry -> entry.audioPaths.map { path -> MediaItem(entry.date, path) } }
            .sortedByDescending { it.date }
}