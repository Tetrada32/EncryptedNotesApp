package com.gahov.encrypted_notes.arch.di.module

import android.app.Application
import com.gahov.encrypted_notes.data.files.JsonFileConverter
import com.gahov.encrypted_notes.data.mapper.NotesLocalMapper
import com.gahov.encrypted_notes.data.security.manager.CryptoManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class MapperModule {

    @Provides
    @Singleton
    internal fun provideNotesLocalMapper(
        cryptoManager: CryptoManager
    ): NotesLocalMapper = NotesLocalMapper(cryptoManager)

    @Provides
    @Singleton
    internal fun provideJsonConverter(
        application: Application
    ): JsonFileConverter = JsonFileConverter(application)
}