package com.quieroestarcontigo.shadowwork.data.remote

import com.quieroestarcontigo.shadowwork.data.model.AuthResponse
import com.quieroestarcontigo.shadowwork.data.model.RefreshRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface SupabaseRefreshApi {
    @POST("auth/v1/token?grant_type=refresh_token")
    suspend fun refreshToken(@Body request: RefreshRequest): AuthResponse
}
