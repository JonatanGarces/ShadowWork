package com.quieroestarcontigo.shadowwork.data.model

data class SupabaseUser(
    val id: String,
    val aud: String?,
    val role: String?,
    val email: String,
    val email_confirmed_at: String?,
    val phone: String?,
    val confirmation_sent_at: String?,
    val confirmed_at: String?,
    val recovery_sent_at: String?,
    val last_sign_in_at: String?,
    val app_metadata: AppMetadata?,
    val user_metadata: UserMetadata?,
    val identities: List<Identity>?,
    val created_at: String?,
    val updated_at: String?,
    val is_anonymous: Boolean
)

data class AppMetadata(
    val provider: String?,
    val providers: List<String>?
)

data class UserMetadata(
    val email: String?,
    val email_verified: Boolean,
    val phone_verified: Boolean,
    val sub: String?
)

data class Identity(
    val identity_id: String,
    val id: String,
    val user_id: String,
    val identity_data: IdentityData,
    val provider: String?,
    val last_sign_in_at: String?,
    val created_at: String?,
    val updated_at: String?,
    val email: String?
)

data class IdentityData(
    val email: String?,
    val email_verified: Boolean,
    val phone_verified: Boolean,
    val sub: String?
)
