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

    @Query("SELECT * FROM notes WHERE uid = :id")
    fun getItemById(id: Long): Flow<NoteDTO>

    @Query("UPDATE notes SET isPinned = :isPinned WHERE uid = :id")
    fun updatePinnedStatus(id: Long, isPinned: Boolean)

    @Query("SELECT * FROM notes WHERE isPinned = 1")
    fun fetchPinned(): Flow<List<NoteDTO>>

    @Query("DELETE FROM notes WHERE uid = :id")
    fun deleteItemById(id: Long)

    @Query("DELETE FROM notes")
    fun deleteAll()
}