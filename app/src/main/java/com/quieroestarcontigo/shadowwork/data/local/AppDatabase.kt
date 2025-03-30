package com.quieroestarcontigo.shadowwork.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.quieroestarcontigo.shadowwork.data.local.dao.AudioRecordDao
import com.quieroestarcontigo.shadowwork.data.local.dao.DreamDao
import com.quieroestarcontigo.shadowwork.data.local.dao.SessionDao
import com.quieroestarcontigo.shadowwork.data.model.AudioRecord
import com.quieroestarcontigo.shadowwork.data.model.UserSession
import com.quieroestarcontigo.shadowwork.data.model.Dream

@Database(entities = [UserSession::class, Dream::class, AudioRecord::class], version = 5)//for development,exportSchema = false

abstract class AppDatabase : RoomDatabase() {
    abstract fun sessionDao(): SessionDao
    abstract fun dreamDao(): DreamDao
    abstract fun audioDao(): AudioRecordDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(context, AppDatabase::class.java, "app_db")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
