package com.example.rejournal.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

enum class CapsuleType { MOOD, TIME }

@Entity(tableName = "time_capsules")
data class TimeCapsule(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: CapsuleType,
    val targetMood: Int? = null,
    val targetDate: LocalDate? = null,
    val message: String,
    val createdDate: LocalDate,
    val delivered: Boolean = false,
    val deliveredDate: LocalDate? = null,
    val opened: Boolean = false
)