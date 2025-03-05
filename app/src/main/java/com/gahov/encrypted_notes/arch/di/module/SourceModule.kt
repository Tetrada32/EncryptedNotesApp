package com.gahov.encrypted_notes.arch.di.module

import com.gahov.encrypted_notes.data.source.notes.NotesLocalSource
import com.gahov.encrypted_notes.data.source.notes.NotesLocalSourceImpl
import com.gahov.encrypted_notes.data.storage.storage.NotesDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class SourceModule {

    @Provides
    @Singleton
    internal fun provideNotesLocalSource(
        notesDao: NotesDao
    ): NotesLocalSource {
        return NotesLocalSourceImpl(
            notesDao = notesDao
        )
    }
}