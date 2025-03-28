package com.quieroestarcontigo.shadowwork.data.remote

import com.quieroestarcontigo.shadowwork.data.model.AuthRequest
import com.quieroestarcontigo.shadowwork.data.model.AuthResponse
import com.quieroestarcontigo.shadowwork.data.model.RefreshRequest
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface SupabaseAuthApi {

    @Headers("Content-Type: application/json")
    @POST("auth/v1/token?grant_type=password")
    suspend fun login(@Body request: AuthRequest): AuthResponse

    @Headers("Content-Type: application/json")
    @POST("auth/v1/signup")
    suspend fun signup(@Body request: AuthRequest): AuthResponse

    @POST("auth/v1/token?grant_type=refresh_token")
    suspend fun refreshToken(@Body request: RefreshRequest): AuthResponse
}
