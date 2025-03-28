package com.quieroestarcontigo.shadowwork.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_session")
data class UserSession(
    @PrimaryKey val id: Int = 1,
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String,
    val expiresIn: Int,
    val expiresAt: Long,
    val userId: String,
    val email: String,
    val emailConfirmedAt: String?,
    val lastSignInAt: String?,
    val isAnonymous: Boolean
)
