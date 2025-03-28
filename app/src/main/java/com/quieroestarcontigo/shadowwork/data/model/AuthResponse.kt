package com.quieroestarcontigo.shadowwork.data.model

data class AuthResponse(
    val access_token: String?,
    val token_type: String?,
    val expires_in: Int?,
    val expires_at: Long?,
    val refresh_token: String?,
    val user: SupabaseUser?
)

fun AuthResponse.toUserSession(): UserSession? {
    if (access_token.isNullOrBlank() || refresh_token.isNullOrBlank() || user == null) return null

    return UserSession(
        accessToken = access_token,
        refreshToken = refresh_token,
        tokenType = token_type ?: "bearer",
        expiresIn = expires_in ?: 3600,
        expiresAt = expires_at ?: System.currentTimeMillis() / 1000L,

        userId = user.id,
        email = user.email,
        emailConfirmedAt = user.email_confirmed_at,
        lastSignInAt = user.last_sign_in_at,
        isAnonymous = user.is_anonymous
    )
}

fun AuthResponse.updateSession(old: UserSession): UserSession? {
    if (access_token.isNullOrBlank()) return null // solo obligatorio el access_token

    return old.copy(
        accessToken = access_token,
        refreshToken = refresh_token ?: old.refreshToken,
        tokenType = token_type ?: old.tokenType,
        expiresIn = expires_in ?: old.expiresIn,
        expiresAt = expires_at ?: old.expiresAt
    )
}