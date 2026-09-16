package com.example.rejournal.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TimeCapsuleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(capsule: TimeCapsule)

    @Delete
    suspend fun delete(capsule: TimeCapsule)

    @Query("SELECT * FROM time_capsules ORDER BY createdDate DESC")
    fun getAll(): Flow<List<TimeCapsule>>

    @Query("SELECT * FROM time_capsules WHERE delivered = 0")
    suspend fun getUndeliveredOnce(): List<TimeCapsule>
}