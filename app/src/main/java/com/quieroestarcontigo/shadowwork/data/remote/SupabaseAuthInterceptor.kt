package com.quieroestarcontigo.shadowwork.data.remote

import android.content.Context
import android.util.Log
import com.quieroestarcontigo.shadowwork.BuildConfig
import com.quieroestarcontigo.shadowwork.util.SecurePrefs
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class SupabaseAuthInterceptor @Inject constructor(
    @ApplicationContext private val context: Context
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val (accessToken, _) = SecurePrefs.getTokens(context)
        Log.d("AuthInterceptor", "🪪 Using token: $accessToken")

        return chain.proceed(
            chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $accessToken")
                .addHeader("apikey", BuildConfig.SUPABASE_API_KEY)
                .build()
        )
    }
}
