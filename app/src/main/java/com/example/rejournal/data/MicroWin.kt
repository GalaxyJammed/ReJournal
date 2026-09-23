package com.example.rejournal.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "micro_wins")
data class MicroWin(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val date: LocalDate = LocalDate.now()
)
