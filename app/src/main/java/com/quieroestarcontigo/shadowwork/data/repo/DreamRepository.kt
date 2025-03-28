package com.quieroestarcontigo.shadowwork.data.repo

import android.content.Context
import android.util.Log
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.quieroestarcontigo.shadowwork.data.local.dao.DreamDao
import com.quieroestarcontigo.shadowwork.data.model.Dream
import com.quieroestarcontigo.shadowwork.data.model.DreamInsertRequest
import com.quieroestarcontigo.shadowwork.data.remote.SupabaseDatabaseApi
import com.quieroestarcontigo.shadowwork.data.worker.DreamSyncWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DreamRepository @Inject constructor(
    private val dreamDao: DreamDao,
    private val api: SupabaseDatabaseApi,
    @ApplicationContext private val context: Context, // ✅ Add this
) {

    fun getAllDreams(): Flow<List<Dream>> = dreamDao.getAllDreams()

    suspend fun insertDream(dream: Dream) {
        dreamDao.insert(dream)
    }

    suspend fun syncWithSupabase() {

        val unsyncedDreams = dreamDao.getUnsyncedDreams()

        try {
            unsyncedDreams.forEach { dream ->

                val request = DreamInsertRequest(
                    content = dream.content,
                    title = dream.title,
                    description = dream.description,
                    tags = dream.tags
                    // timestamp = dream.timestamp
                )

                api.addDream(
                    request
                )
                dreamDao.markAsSynced(dream.id)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getUnsyncedCount(): Flow<Int> = dreamDao.getUnsyncedCount()

    fun enqueueSyncWork() {
        val request =
            OneTimeWorkRequestBuilder<DreamSyncWorker>().addTag("dream_sync") // Helps identify this worker
                .build()
        WorkManager.getInstance(context).enqueue(request)
        // 👇 Check the current status of the worker
        WorkManager.getInstance(context).getWorkInfosByTag("dream_sync")
            .get() // This runs synchronously — okay for debug
            .forEach {
                Log.d("DreamRepository", "🔍 Work state: ${it.state}")
            }
    }

}
