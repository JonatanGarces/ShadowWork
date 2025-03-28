package com.quieroestarcontigo.shadowwork.data.remote.auth


import android.util.Log
import com.quieroestarcontigo.shadowwork.data.session.TokenProvider
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

class SupabaseTokenAuthenticator @Inject constructor(
    private val tokenProvider: TokenProvider
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        val newToken = runBlocking {
            tokenProvider.refreshToken()
        }
        Log.d("Authenticator", "🔄 New token: $newToken")

        return newToken?.let {
            response.request.newBuilder()
                .header("Authorization", "Bearer $it")
                .build()
        }
    }
}
