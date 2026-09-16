package com.example.rejournal.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(
    tableName = "mood_entries",
    indices = [Index(value = ["date"], unique = true)]
)
data class MoodEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: LocalDate,
    val mood: Int,
    val activities: List<String>,
    val note: String,
    val energy: Int = 3,
    val productivity: Int = 3,
    val stress: Int = 3,
    val sleep: Int = 3,
    val photoPaths: List<String> = emptyList(),
    val audioPaths: List<String> = emptyList(),
    val isFavorite: Boolean = false
)