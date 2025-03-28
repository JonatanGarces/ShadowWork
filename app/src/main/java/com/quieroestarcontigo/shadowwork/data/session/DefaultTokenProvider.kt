package com.quieroestarcontigo.shadowwork.data.session

import android.content.Context
import android.util.Log
import com.quieroestarcontigo.shadowwork.util.SecurePrefs
import com.quieroestarcontigo.shadowwork.data.local.SessionDao
import com.quieroestarcontigo.shadowwork.data.model.RefreshRequest
import com.quieroestarcontigo.shadowwork.data.model.updateSession
import com.quieroestarcontigo.shadowwork.data.remote.SupabaseRefreshApi
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class DefaultTokenProvider @Inject constructor(
    private val api: SupabaseRefreshApi,
    private val sessionDao: SessionDao,
    @ApplicationContext private val context: Context
) : TokenProvider {

    override suspend fun refreshToken(): String? {
        val (_, refreshToken) = SecurePrefs.getTokens(context)
        Log.d("TokenProvider", "🔄 Intentando refrescar token: $refreshToken")

        if (refreshToken == null) {
            Log.e("TokenProvider", "🚫 No refresh token encontrado")
            return null
        }

        return try {
            val current = sessionDao.getSessionNow() ?: return null

            val response = api.refreshToken(RefreshRequest(refreshToken))
            Log.d("TokenProvider", "📦 Respuesta del servidor: $response")

            val updated = response.updateSession(current)
            if (updated == null) {
                Log.e("TokenProvider", "🚫 updateSession devolvió null")
                return null
            }

            sessionDao.insertSession(updated)
            SecurePrefs.saveTokens(context, updated.accessToken, updated.refreshToken)

            Log.d("TokenProvider", "✅ Token refrescado: ${updated.accessToken}")
            updated.accessToken
        } catch (e: Exception) {
            Log.e("TokenProvider", "❌ Error al refrescar token", e)
            null
        }
    }

}
