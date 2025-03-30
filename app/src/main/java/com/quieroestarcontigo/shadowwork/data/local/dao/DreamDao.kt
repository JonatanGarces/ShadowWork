package com.quieroestarcontigo.shadowwork.data.local.dao

import androidx.room.*
import com.quieroestarcontigo.shadowwork.data.model.Dream
import kotlinx.coroutines.flow.Flow

@Dao
interface DreamDao {
    @Query("SELECT * FROM dreams ORDER BY timestamp DESC")
    fun getAllDreams(): Flow<List<Dream>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(dreams: List<Dream>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(dream: Dream)

    @Query("DELETE FROM dreams")
    suspend fun clearAll()

    @Query("SELECT * FROM dreams WHERE synced = 0")
    suspend fun getUnsyncedDreams(): List<Dream>

    @Query("UPDATE dreams SET synced = 1 WHERE id = :id")
    suspend fun markAsSynced(id: Int)

    @Query("SELECT COUNT(*) FROM dreams WHERE synced = 0")
    fun getUnsyncedCount(): Flow<Int>
}
