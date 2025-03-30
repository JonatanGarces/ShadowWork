package com.quieroestarcontigo.shadowwork.data.repo

import android.content.Context
import android.media.MediaRecorder
import android.util.Log
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.quieroestarcontigo.shadowwork.data.remote.SupabaseDatabaseApi
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import java.io.File
import java.util.*
import javax.inject.Inject
import com.quieroestarcontigo.shadowwork.data.local.dao.AudioRecordDao
import com.quieroestarcontigo.shadowwork.data.model.AudioRecord
import com.quieroestarcontigo.shadowwork.data.model.DreamInsertRequest
import com.quieroestarcontigo.shadowwork.data.worker.AudioSyncWorker
import kotlinx.coroutines.flow.first
import javax.inject.Singleton
import android.util.Base64
import com.quieroestarcontigo.shadowwork.data.model.AudioInsertRequest

@Singleton
class AudioRepository @Inject constructor(
    private val dao: AudioRecordDao,
    private val api: SupabaseDatabaseApi, // tu servicio de Retrofit
    @ApplicationContext private val context: Context,
) {

    private var recorder: MediaRecorder? = null
    private var currentFilePath: String? = null

    fun getAll(): Flow<List<AudioRecord>> = dao.getAll()

    fun getUnsyncedCount(): Flow<Int> = dao.getUnsyncedCount()

    suspend fun insert(audioRecord: AudioRecord) {
        dao.insert(audioRecord)
    }


    suspend fun syncWithSupabase() {
        val items = dao.getUnsynced()


        try {

            items.forEach { item ->
                val file = File(item.filePath)
                if (!file.exists()) {
                    Log.e("AudioRepository", "⚠️ File not found: ${item.filePath}")
                    return
                }

                val bytes = file.readBytes()
                val base64Audio = Base64.encodeToString(bytes, Base64.NO_WRAP)
                val request = AudioInsertRequest(
                    transcription = item.transcription,
                    timestamp = item.timestamp,
                    file_bytes = base64Audio
                )

                api.addAudio(request)
                dao.markAsSynced(item.id)
            }


        } catch (e: Exception) {
            Log.e("AudioRepository", "❌ Error uploading bytea", e)
        }
    }


    fun startRecording(): String {
        val fileName = "audio_${UUID.randomUUID()}.m4a"
        val file = File(context.filesDir, fileName)
        currentFilePath = file.absolutePath

        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setOutputFile(currentFilePath)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            prepare()
            start()
        }

        return currentFilePath!!
    }

    fun stopRecording(): AudioRecord? {
        recorder?.apply {
            stop()
            release()
        }
        recorder = null

        val filePath = currentFilePath ?: return null
        val record = AudioRecord(filePath = filePath)
        currentFilePath = null
        return record
    }

    suspend fun updateTranscription(id: Int, transcription: String) {
        val record = dao.getAll()
            .first() // 🔥 Esto te da la lista actual emitida
            .find { it.id == id } // 🔍 Buscar por id

        record?.let {
            dao.update(it.copy(transcription = transcription))
        }
    }

    suspend fun speechToText(record: AudioRecord) {
        try {
            val audioFile = File(record.filePath)
            if (!audioFile.exists()) {
                Log.e("AudioRepository", "⚠️ Archivo no encontrado: ${record.filePath}")
                return
            }

            // Simulación: aquí usarías un servicio real de STT (Speech-to-Text)
            val fakeTranscription =
                "Esto es una transcripción de prueba del archivo ${audioFile.name}"

            dao.update(record.copy(transcription = fakeTranscription))
            Log.d("AudioRepository", "✅ Transcripción agregada: $fakeTranscription")

        } catch (e: Exception) {
            Log.e("AudioRepository", "❌ Error en transcripción", e)
        }
    }

    fun enqueueSyncWork() {
        val request =
            OneTimeWorkRequestBuilder<AudioSyncWorker>().addTag("audio_sync") // Helps identify this worker
                .build()
        WorkManager.getInstance(context).enqueue(request)
        // 👇 Check the current status of the worker
        WorkManager.getInstance(context).getWorkInfosByTag("audio_sync")
            .get() // This runs synchronously — okay for debug
            .forEach {
                Log.d("DreamRepository", "🔍 Work state: ${it.state}")
            }
    }
}

