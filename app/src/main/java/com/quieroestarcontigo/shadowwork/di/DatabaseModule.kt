package com.quieroestarcontigo.shadowwork.di

import android.content.Context
import androidx.room.Room
import com.quieroestarcontigo.shadowwork.data.local.AppDatabase
import com.quieroestarcontigo.shadowwork.data.local.dao.DreamDao
import com.quieroestarcontigo.shadowwork.data.local.SessionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "app_db")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideSessionDao(db: AppDatabase): SessionDao = db.sessionDao()

    @Provides
    fun provideDreamDao(db: AppDatabase): DreamDao = db.dreamDao()
}
