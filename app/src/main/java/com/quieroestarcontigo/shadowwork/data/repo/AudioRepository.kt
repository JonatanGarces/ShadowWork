package com.quieroestarcontigo.shadowwork.data.repo

import android.content.Context
import android.media.MediaRecorder
import android.net.Uri
import android.util.Log
import com.quieroestarcontigo.shadowwork.data.local.dao.AudioRecordDao
import com.quieroestarcontigo.shadowwork.data.model.AudioRecord
import com.quieroestarcontigo.shadowwork.data.remote.SupabaseDatabaseApi
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import java.io.File
import java.util.*
import javax.inject.Inject
import android.util.Base64
import com.quieroestarcontigo.shadowwork.data.model.AudioInsertRequest

class AudioRepository @Inject constructor(
    private val audioDao: AudioRecordDao,
    private val api: SupabaseDatabaseApi, // tu servicio de Retrofit
    @ApplicationContext private val context: Context
) {

    private var recorder: MediaRecorder? = null
    private var currentFilePath: String? = null

    fun getAllRecords(): Flow<List<AudioRecord>> = audioDao.getAll()

    suspend fun insertAudio(record: AudioRecord) {
        audioDao.insert(record)
    }

    suspend fun syncWithSupabase(record: AudioRecord) {
        try {
            val file = File(record.filePath)
            if (!file.exists()) {
                Log.e("AudioRepository", "⚠️ File not found: ${record.filePath}")
                return
            }

            val bytes = file.readBytes()
            val base64Audio = Base64.encodeToString(bytes, Base64.NO_WRAP)

            val request = AudioInsertRequest(
                transcription = record.transcription,
                timestamp = record.timestamp,
                file_bytes = base64Audio
            )

            val response = api.uploadAudio(request)
            if (response.isSuccessful) {
                audioDao.update(record.copy(isSynced = true))
                Log.d("AudioRepository", "✅ Uploaded audio as bytea")
            } else {
                Log.e("AudioRepository", "❌ Upload failed: ${response.code()}")
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


    suspend fun updateTranscription(id: String, transcription: String) {
        val records = audioDao.getAll()
        val record = records.find { it.id == id } ?: return
        audioDao.update(record.copy(transcription = transcription))
    }

    suspend fun speechToText(record: AudioRecord) {
        try {
            val audioFile = File(record.filePath)
            if (!audioFile.exists()) {
                Log.e("AudioRepository", "⚠️ Archivo no encontrado: ${record.filePath}")
                return
            }

            // Simulación: aquí usarías un servicio real de STT (Speech-to-Text)
            val fakeTranscription = "Esto es una transcripción de prueba del archivo ${audioFile.name}"

            audioDao.update(record.copy(transcription = fakeTranscription))
            Log.d("AudioRepository", "✅ Transcripción agregada: $fakeTranscription")

        } catch (e: Exception) {
            Log.e("AudioRepository", "❌ Error en transcripción", e)
        }
    }

}
