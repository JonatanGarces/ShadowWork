package com.quieroestarcontigo.shadowwork.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "audio_records")
data class AudioRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val filePath: String,
    val transcription: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val synced: Boolean = false // useful for knowing if it's synced to Supabase
)


data class AudioInsertRequest(
    val file_bytes: String,
    val transcription: String,
    val timestamp: Long = System.currentTimeMillis()
)
