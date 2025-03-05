package com.gahov.encrypted_notes.arch.di.module

import android.content.Context
import androidx.room.Room
import com.gahov.encrypted_notes.data.storage.AppDatabase
import com.gahov.encrypted_notes.data.storage.AppDatabase.Companion.DB_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, DB_NAME)
            .build()
    }

    @Provides
    @Singleton
    fun provideNotesDao(db: AppDatabase) = db.notesDao()
}