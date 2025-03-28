package com.quieroestarcontigo.shadowwork.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dreams")
data class Dream(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val content: String,
    val tags: String,
    val created_at: String? = null,
    val timestamp: Long = System.currentTimeMillis(), // ⏰
    val synced: Boolean = false // useful for knowing if it's synced to Supabase

)


data class DreamInsertRequest(
    val content: String,
    val title: String,
    val description: String,
    val tags: String,
    //val timestamp: Long
)
