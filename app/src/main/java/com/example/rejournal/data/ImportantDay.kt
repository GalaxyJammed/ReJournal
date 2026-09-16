package com.example.rejournal.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(
    tableName = "important_days",
    indices = [Index(value = ["date"], unique = true)]
)
data class ImportantDay(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: LocalDate,
    val message: String
)