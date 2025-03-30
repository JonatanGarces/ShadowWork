package com.quieroestarcontigo.shadowwork.data.remote
import com.quieroestarcontigo.shadowwork.data.model.AudioInsertRequest
import com.quieroestarcontigo.shadowwork.data.model.Dream
import com.quieroestarcontigo.shadowwork.data.model.DreamInsertRequest
import retrofit2.http.*


interface SupabaseDatabaseApi {

    @GET("rest/v1/dreams")
    suspend fun getAllDreams(): List<Dream>

    @POST("rest/v1/dreams")
    suspend fun addDream(@Body dream: DreamInsertRequest)

    @POST("rest/v1/audios")
    suspend fun addAudio(@Body audio: AudioInsertRequest)
}