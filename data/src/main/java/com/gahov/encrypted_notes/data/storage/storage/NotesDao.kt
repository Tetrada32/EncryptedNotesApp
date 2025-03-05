package com.gahov.encrypted_notes.data.storage.storage

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.gahov.encrypted_notes.data.storage.entities.NoteDTO
import kotlinx.coroutines.flow.Flow

@Dao
interface NotesDao {

    @Upsert
    fun insertItems(items: List<NoteDTO>)

    @Query("SELECT * FROM notes WHERE deletedAt > :currentDate")
    fun fetchAll(currentDate: Long): Flow<List<NoteDTO>>

    @Query("DELETE FROM notes WHERE uid = :id")
    fun deleteItemById(id: Long)

    @Query("UPDATE notes SET content = :message, isPinned = :isPinned WHERE uid = :id")
    fun updateNote(id: Long, message: String, isPinned: Boolean)
}