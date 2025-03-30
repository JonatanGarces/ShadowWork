package com.quieroestarcontigo.shadowwork.di

import android.content.Context
import com.quieroestarcontigo.shadowwork.data.local.dao.DreamDao
import com.quieroestarcontigo.shadowwork.data.local.dao.SessionDao
import com.quieroestarcontigo.shadowwork.data.local.dao.AudioRecordDao
import com.quieroestarcontigo.shadowwork.data.remote.SupabaseAuthApi
import com.quieroestarcontigo.shadowwork.data.remote.SupabaseDatabaseApi
import com.quieroestarcontigo.shadowwork.data.remote.SupabaseRefreshApi
import com.quieroestarcontigo.shadowwork.data.remote.auth.SupabaseTokenAuthenticator
import com.quieroestarcontigo.shadowwork.data.repo.AudioRepository
import com.quieroestarcontigo.shadowwork.data.repo.AuthRepository
import com.quieroestarcontigo.shadowwork.data.repo.DreamRepository
import com.quieroestarcontigo.shadowwork.data.session.DefaultTokenProvider
import com.quieroestarcontigo.shadowwork.data.session.TokenProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideAuthRepository(
        api: SupabaseAuthApi,
        sessionDao: SessionDao,
        @ApplicationContext context: Context
    ): AuthRepository {
        return AuthRepository(api, sessionDao, context)
    }

    @Provides
    @Singleton // ✅ ADD THIS
    fun provideDreamRepository(
        dreamDao: DreamDao,
        dbApi: SupabaseDatabaseApi,
        @ApplicationContext context: Context
    ): DreamRepository {
        return DreamRepository(dreamDao, dbApi, context)
    }

    @Provides
    @Singleton // ✅ ADD THIS
    fun provideAudioRepository(
        audio: AudioRecordDao,
        dbApi: SupabaseDatabaseApi,
        @ApplicationContext context: Context
    ): AudioRepository {
        return AudioRepository(audio, dbApi, context)
    }

    @Provides
    @Singleton
    fun provideSupabaseTokenAuthenticator(tokenProvider: TokenProvider): SupabaseTokenAuthenticator {
        return SupabaseTokenAuthenticator(tokenProvider)
    }

    @Provides
    @Singleton
    fun provideTokenProvider(
        api: SupabaseRefreshApi,
        sessionDao: SessionDao,
        @ApplicationContext context: Context
    ): TokenProvider = DefaultTokenProvider(api, sessionDao, context)
}
