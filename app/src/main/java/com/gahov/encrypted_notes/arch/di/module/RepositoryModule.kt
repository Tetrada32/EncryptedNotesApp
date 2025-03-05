package com.gahov.encrypted_notes.arch.di.module

import com.gahov.encrypted_notes.data.mapper.NotesLocalMapper
import com.gahov.encrypted_notes.data.repository.notes.NotesRepositoryImpl
import com.gahov.encrypted_notes.data.security.CryptoManager
import com.gahov.encrypted_notes.data.source.notes.NotesLocalSource
import com.gahov.encrypted_notes.domain.repository.NotesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Provides
    @Singleton
    internal fun provideNotesRepository(
        localSource: NotesLocalSource,
        localMapper: NotesLocalMapper,
        cryptoManager: CryptoManager
    ): NotesRepository {
        return NotesRepositoryImpl(
            localSource = localSource,
            localMapper = localMapper,
            cryptoManager = cryptoManager
        )
    }
}