package com.example.rejournal.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface MoodDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: MoodEntry)

    @Delete
    suspend fun delete(entry: MoodEntry)

    @Query("SELECT * FROM mood_entries ORDER BY date DESC")
    fun getAllEntries(): Flow<List<MoodEntry>>

    // One-shot (non-Flow) fetch, used only for maintenance tasks like purging
    // a deleted tag from every entry that used it.
    @Query("SELECT * FROM mood_entries")
    suspend fun getAllEntriesOnce(): List<MoodEntry>

    @Query("SELECT * FROM mood_entries WHERE date = :date LIMIT 1")
    suspend fun getEntryForDate(date: LocalDate): MoodEntry?
}