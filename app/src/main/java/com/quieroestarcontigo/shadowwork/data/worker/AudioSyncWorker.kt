package com.quieroestarcontigo.shadowwork.data.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.quieroestarcontigo.shadowwork.data.repo.AudioRepository
import com.quieroestarcontigo.shadowwork.data.repo.DreamRepository
import com.quieroestarcontigo.shadowwork.di.AudioRepositoryEntryPoint
import com.quieroestarcontigo.shadowwork.di.DreamRepositoryEntryPoint
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.EntryPointAccessors

@HiltWorker
class AudioSyncWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        Log.d("AudioSyncWorker", "✅ Worker running via EntryPoint")

        return try {
            val entryPoint = EntryPointAccessors.fromApplication(
                context,
                AudioRepositoryEntryPoint::class.java
            )
            val repository: AudioRepository = entryPoint.audioRepository()

            Log.d("DreamSyncWorker", "🔄 Syncing dreams...")
            repository.syncWithSupabase()

            Log.d("DreamSyncWorker", "✅ Sync complete")
            Result.success()
        } catch (e: Exception) {
            Log.e("DreamSyncWorker", "❌ Sync failed: ${e.message}", e)
            Result.failure()
        }
    }
}
