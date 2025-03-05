package com.gahov.encrypted_notes.domain.repository

import com.gahov.encrypted_notes.domain.common.Either
import com.gahov.encrypted_notes.domain.entities.Failure
import com.gahov.encrypted_notes.domain.entities.NoteEntity
import kotlinx.coroutines.flow.Flow

interface NotesRepository {

    suspend fun fetchAllNotes(): Flow<Either<Failure, List<NoteEntity>>>

    suspend fun fetchPinnedNotes(): Flow<Either<Failure, List<NoteEntity>>>

    suspend fun deleteAllNotes(): Either<Failure, Unit>


    suspend fun addNote(note: NoteEntity): Either<Failure, Unit>

    suspend fun switchPinStatus(noteId: Long, isPinned: Boolean): Either<Failure, Unit>

    suspend fun deleteNote(noteId: Long): Either<Failure, Unit>

}