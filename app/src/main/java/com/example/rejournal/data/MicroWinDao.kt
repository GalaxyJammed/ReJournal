package com.example.rejournal.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface MicroWinDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(microWin: MicroWin)

    @Delete
    suspend fun delete(microWin: MicroWin)

    @Query("SELECT * FROM micro_wins ORDER BY date DESC, id DESC")
    fun getAll(): Flow<List<MicroWin>>

    @Query("SELECT COUNT(*) FROM micro_wins WHERE date = :date")
    suspend fun getCountForDate(date: LocalDate): Int
}
