package com.gahov.encrypted_notes.data.storage

import androidx.room.Database
import androidx.room.RoomDatabase
import com.gahov.encrypted_notes.data.storage.AppDatabase.Companion.DB_VERSION
import com.gahov.encrypted_notes.data.storage.entities.NoteDTO
import com.gahov.encrypted_notes.data.storage.storage.NotesDao

/**
 * An abstract Room database class representing the application's database.
 *
 * @see Database
 */
@Database(
    entities = [NoteDTO::class],
    version = DB_VERSION
)

abstract class AppDatabase : RoomDatabase() {

    /**
     * Provides access to the NotesDao for database operations.
     *
     * @return An instance of the NotesDao interface.
     */
    abstract fun notesDao(): NotesDao


    companion object {

        /**
         * The version of the database.
         * Should be increased every time the developer changes DTO or DB config.
         */
        const val DB_VERSION = 1

        /**
         * The name of the database file.
         */
        var DB_NAME = "encrypted_notes_app.db"
    }
}