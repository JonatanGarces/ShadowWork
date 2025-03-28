package com.quieroestarcontigo.shadowwork.di

import com.quieroestarcontigo.shadowwork.data.repo.DreamRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface DreamRepositoryEntryPoint {
    fun dreamRepository(): DreamRepository
}
