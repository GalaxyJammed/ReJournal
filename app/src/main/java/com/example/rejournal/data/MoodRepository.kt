package com.example.rejournal.data

import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class MoodRepository(
    private val dao: MoodDao,
    private val importantDayDao: ImportantDayDao
) {
    val allEntries: Flow<List<MoodEntry>> = dao.getAllEntries()
    val allImportantDays: Flow<List<ImportantDay>> = importantDayDao.getAll()

    suspend fun saveEntry(entry: MoodEntry) = dao.insert(entry)
    suspend fun deleteEntry(entry: MoodEntry) = dao.delete(entry)
    suspend fun getEntryForDate(date: LocalDate): MoodEntry? = dao.getEntryForDate(date)

    suspend fun removeTagFromAllEntries(tag: String) {
        val allEntries = dao.getAllEntriesOnce()
        allEntries.filter { tag in it.activities }.forEach { entry ->
            dao.insert(entry.copy(activities = entry.activities - tag))
        }
    }

    suspend fun saveImportantDay(day: ImportantDay) = importantDayDao.insert(day)
    suspend fun deleteImportantDay(day: ImportantDay) = importantDayDao.delete(day)
    suspend fun getImportantDayForDate(date: LocalDate): ImportantDay? = importantDayDao.getForDate(date)
}