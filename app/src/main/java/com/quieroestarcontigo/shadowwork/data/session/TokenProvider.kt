package com.quieroestarcontigo.shadowwork.data.session

interface TokenProvider {
    suspend fun refreshToken(): String?
}