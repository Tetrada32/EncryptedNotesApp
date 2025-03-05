package com.gahov.encrypted_notes.data.source.notes

import com.gahov.encrypted_notes.data.storage.entities.NoteDTO
import com.gahov.encrypted_notes.domain.common.Either
import com.gahov.encrypted_notes.domain.entities.Failure
import kotlinx.coroutines.flow.Flow

interface NotesLocalSource {

    suspend fun fetchNotes(): Flow<Either<Failure, List<NoteDTO>>>

    suspend fun addNotes(notes: List<NoteDTO>): Either<Failure, Unit>

    suspend fun updateNote(id: Long, message: String, isPinned: Boolean): Either<Failure, Unit>

    suspend fun deleteById(id: Long): Either<Failure, Unit>

}