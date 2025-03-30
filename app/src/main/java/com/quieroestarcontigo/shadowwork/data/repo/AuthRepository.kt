package com.quieroestarcontigo.shadowwork.data.repo


import android.content.Context
import androidx.lifecycle.LiveData
import com.quieroestarcontigo.shadowwork.util.SecurePrefs
import com.quieroestarcontigo.shadowwork.data.local.dao.SessionDao
import com.quieroestarcontigo.shadowwork.data.model.AuthRequest
import com.quieroestarcontigo.shadowwork.data.model.RefreshRequest
import com.quieroestarcontigo.shadowwork.data.remote.SupabaseAuthApi
import com.quieroestarcontigo.shadowwork.data.model.UserSession
import com.quieroestarcontigo.shadowwork.data.model.toUserSession
import com.quieroestarcontigo.shadowwork.data.model.updateSession
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val api: SupabaseAuthApi,
    private val sessionDao: SessionDao,
    @ApplicationContext private val context: Context
) {

    val session: LiveData<UserSession?> = sessionDao.getSession()

    suspend fun login(email: String, password: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val response = api.login(AuthRequest(email, password))
            val session = response.toUserSession() ?: return@withContext false

            sessionDao.insertSession(session)
            SecurePrefs.saveTokens(context, session.accessToken, session.refreshToken)
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun refresh(): Boolean = withContext(Dispatchers.IO) {
        try {
            val (_, refreshToken) = SecurePrefs.getTokens(context)
            if (refreshToken == null) return@withContext false

            val response = api.refreshToken(RefreshRequest(refreshToken))
            val current = sessionDao.getSession().value ?: return@withContext false
            val updated = response.updateSession(current) ?: return@withContext false

            sessionDao.insertSession(updated)
            SecurePrefs.saveTokens(context, updated.accessToken, updated.refreshToken)
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun logout() {
        sessionDao.clearSession()
        SecurePrefs.clear(context)
    }

    suspend fun signup(email: String, password: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val response = api.signup(AuthRequest(email, password))
            val session = response.toUserSession() ?: return@withContext false

            sessionDao.insertSession(session)
            SecurePrefs.saveTokens(context, session.accessToken, session.refreshToken)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

}


