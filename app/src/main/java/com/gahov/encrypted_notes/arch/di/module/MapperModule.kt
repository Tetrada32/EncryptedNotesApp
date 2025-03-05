package com.gahov.encrypted_notes.arch.di.module

import com.gahov.encrypted_notes.data.mapper.NotesLocalMapper
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
    internal fun provideNotesLocalMapper(): NotesLocalMapper = NotesLocalMapper()
}