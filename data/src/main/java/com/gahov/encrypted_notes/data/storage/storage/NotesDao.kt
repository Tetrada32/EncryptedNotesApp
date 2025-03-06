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

    @Query("SELECT * FROM notes")
    fun fetchAll(): Flow<List<NoteDTO>>

    @Query("UPDATE notes SET deletedAt = :deletedAt WHERE uid = :id")
    fun deleteItem(id: Long, deletedAt: Long)

    @Query("UPDATE notes SET content = :message, isPinned = :isPinned, deletedAt = :deletedAt WHERE uid = :id")
    fun updateNote(id: Long, message: String, isPinned: Boolean, deletedAt: Long)
}