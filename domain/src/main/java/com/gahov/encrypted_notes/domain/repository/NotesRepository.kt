package com.gahov.encrypted_notes.domain.repository

import com.gahov.encrypted_notes.domain.common.Either
import com.gahov.encrypted_notes.domain.entities.Failure
import com.gahov.encrypted_notes.domain.entities.Note
import kotlinx.coroutines.flow.Flow
import java.io.File

interface NotesRepository {

    suspend fun fetchAllNotes(): Flow<Either<Failure, List<Note>>>

    suspend fun addNote(note: Note): Either<Failure, Unit>

    suspend fun updateNote(noteId: Long, message: String, isPinned: Boolean): Either<Failure, Unit>

    suspend fun deleteNote(noteId: Long): Either<Failure, Unit>

    suspend fun prepareToExportNotes(): Either<Failure, File>

}