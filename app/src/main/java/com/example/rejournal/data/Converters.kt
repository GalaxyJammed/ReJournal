package com.example.rejournal.data

import androidx.room.TypeConverter
import java.time.LocalDate

class Converters {
    @TypeConverter
    fun fromLocalDate(date: LocalDate): String = date.toString()

    @TypeConverter
    fun toLocalDate(value: String): LocalDate = LocalDate.parse(value)

    @TypeConverter
    fun fromActivities(activities: List<String>): String = activities.joinToString(",")

    @TypeConverter
    fun toActivities(value: String): List<String> =
        if (value.isBlank()) emptyList() else value.split(",")

    @TypeConverter
    fun fromNullableLocalDate(date: LocalDate?): String? = date?.toString()

    @TypeConverter
    fun toNullableLocalDate(value: String?): LocalDate? = value?.let { LocalDate.parse(it) }

    @TypeConverter
    fun fromCapsuleType(type: CapsuleType): String = type.name

    @TypeConverter
    fun toCapsuleType(value: String): CapsuleType = CapsuleType.valueOf(value)
}