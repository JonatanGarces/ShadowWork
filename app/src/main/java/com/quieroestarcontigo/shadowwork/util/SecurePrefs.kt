package com.quieroestarcontigo.shadowwork.util

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

object SecurePrefs {

    private const val FILE_NAME = "secure_prefs"

    fun get(context: Context): SharedPreferences {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        return EncryptedSharedPreferences.create(
            context,
            FILE_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun saveTokens(context: Context, accessToken: String, refreshToken: String) {
        get(context).edit()
            .putString("access_token", accessToken)
            .putString("refresh_token", refreshToken)
            .apply()
    }

    fun getTokens(context: Context): Pair<String?, String?> {
        val prefs = get(context)
        return prefs.getString("access_token", null) to prefs.getString("refresh_token", null)
    }

    fun clear(context: Context) {
        get(context).edit().clear().apply()
    }
}
