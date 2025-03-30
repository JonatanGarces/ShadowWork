package com.quieroestarcontigo.shadowwork.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.quieroestarcontigo.shadowwork.data.model.AudioRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface AudioRecordDao {

    @Query("SELECT * FROM audio_records ORDER BY timestamp DESC")
    fun getAll(): Flow<List<AudioRecord>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(records: List<AudioRecord>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: AudioRecord)

    @Query("DELETE FROM audio_records")
    suspend fun clearAll()

    @Query("SELECT * FROM audio_records WHERE synced = 0")
    suspend fun getUnsynced(): List<AudioRecord>

    @Query("UPDATE audio_records SET synced = 1 WHERE id = :id")
    suspend fun markAsSynced(id: Int)

    @Query("SELECT COUNT(*) FROM audio_records WHERE synced = 0")
    fun getUnsyncedCount(): Flow<Int>

    @Update
    suspend fun update(record: AudioRecord)

}
