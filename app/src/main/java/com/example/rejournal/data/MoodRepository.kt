package com.example.rejournal.data

import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class MoodRepository(private val dao: MoodDao) {
    val allEntries: Flow<List<MoodEntry>> = dao.getAllEntries()

    suspend fun saveEntry(entry: MoodEntry) = dao.insert(entry)

    suspend fun deleteEntry(entry: MoodEntry) = dao.delete(entry)

    suspend fun getEntryForDate(date: LocalDate): MoodEntry? = dao.getEntryForDate(date)

    suspend fun removeTagFromAllEntries(tag: String) {
        val allEntries = dao.getAllEntriesOnce()
        allEntries.filter { tag in it.activities }.forEach { entry ->
            dao.insert(entry.copy(activities = entry.activities - tag))
        }
    }
}