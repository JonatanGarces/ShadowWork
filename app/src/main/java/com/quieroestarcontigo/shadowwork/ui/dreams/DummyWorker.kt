package com.quieroestarcontigo.shadowwork.ui.dreams

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class DummyWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        Log.d("DummyWorker", "✅ Dummy worker is running!")
        return Result.success()
    }
}