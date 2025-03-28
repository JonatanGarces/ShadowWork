package com.quieroestarcontigo.shadowwork.di

import android.content.Context
import com.quieroestarcontigo.shadowwork.BuildConfig
import com.quieroestarcontigo.shadowwork.data.remote.SupabaseAuthApi
import com.quieroestarcontigo.shadowwork.data.remote.SupabaseAuthInterceptor
import com.quieroestarcontigo.shadowwork.data.remote.SupabaseDatabaseApi
import com.quieroestarcontigo.shadowwork.data.remote.SupabaseRefreshApi
import com.quieroestarcontigo.shadowwork.data.remote.auth.SupabaseTokenAuthenticator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(
        supabaseAuthInterceptor: SupabaseAuthInterceptor,
        supabaseTokenAuthenticator: SupabaseTokenAuthenticator
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(supabaseAuthInterceptor)
            .authenticator(supabaseTokenAuthenticator)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.SUPABASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    fun provideAuthApi(retrofit: Retrofit): SupabaseAuthApi =
        retrofit.create(SupabaseAuthApi::class.java)

    @Provides
    fun provideDbApi(retrofit: Retrofit): SupabaseDatabaseApi =
        retrofit.create(SupabaseDatabaseApi::class.java)

    @Provides
    @Singleton
    fun provideSupabaseAuthInterceptor(
        @ApplicationContext context: Context
    ): SupabaseAuthInterceptor {
        return SupabaseAuthInterceptor(context)
    }

    @Provides
    @Singleton
    fun provideRefreshApi(): SupabaseRefreshApi {
        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("apikey", BuildConfig.SUPABASE_API_KEY)
                    .addHeader("Content-Type", "application/json")
                    .build()
                chain.proceed(request)
            }
            .build()

        return Retrofit.Builder()
            .baseUrl(BuildConfig.SUPABASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SupabaseRefreshApi::class.java)
    }

}
