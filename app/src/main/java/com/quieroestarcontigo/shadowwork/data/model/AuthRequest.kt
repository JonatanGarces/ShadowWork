package com.quieroestarcontigo.shadowwork.data.model

data class AuthRequest(val email: String, val password: String)
data class RefreshRequest(val refresh_token: String)