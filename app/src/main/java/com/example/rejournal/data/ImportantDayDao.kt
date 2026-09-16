package com.example.rejournal.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface ImportantDayDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(day: ImportantDay)

    @Delete
    suspend fun delete(day: ImportantDay)

    @Query("SELECT * FROM important_days ORDER BY date ASC")
    fun getAll(): Flow<List<ImportantDay>>

    @Query("SELECT * FROM important_days ORDER BY date ASC")
    suspend fun getAllOnce(): List<ImportantDay>

    @Query("SELECT * FROM important_days WHERE date = :date LIMIT 1")
    suspend fun getForDate(date: LocalDate): ImportantDay?
}