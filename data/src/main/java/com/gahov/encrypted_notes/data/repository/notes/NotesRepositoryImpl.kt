package com.gahov.encrypted_notes.data.repository.notes

import com.gahov.encrypted_notes.data.mapper.NotesLocalMapper
import com.gahov.encrypted_notes.data.security.CryptoManager
import com.gahov.encrypted_notes.data.source.notes.NotesLocalSource
import com.gahov.encrypted_notes.domain.common.Either
import com.gahov.encrypted_notes.domain.common.getOrElse
import com.gahov.encrypted_notes.domain.entities.Failure
import com.gahov.encrypted_notes.domain.entities.Note
import com.gahov.encrypted_notes.domain.repository.NotesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class NotesRepositoryImpl(
    private val localSource: NotesLocalSource,
    private val localMapper: NotesLocalMapper,
    private val cryptoManager: CryptoManager
) : NotesRepository {

    override suspend fun fetchAllNotes(): Flow<Either<Failure, List<Note>>> {
        return withContext(Dispatchers.IO) {
            localSource.fetchNotes().map { result ->
                when (result) {
                    is Either.Left -> result
                    is Either.Right -> Either.Right(localMapper.toDomain(result.success))
                }
            }
        }
    }

    override suspend fun addNote(note: Note): Either<Failure, Unit> {
        return withContext(Dispatchers.IO) {
            localSource.addNotes(listOf(localMapper.toDatabase(note)))
        }
    }

    override suspend fun updateNote(
        noteId: Long,
        message: String,
        isPinned: Boolean
    ): Either<Failure, Unit> {
        return withContext(Dispatchers.IO) {
            val encryptedMessage = cryptoManager.encryptToString(message)
            localSource.updateNote(noteId, encryptedMessage.getOrElse(""), isPinned)
        }
    }

    override suspend fun deleteNote(noteId: Long): Either<Failure, Unit> {
        return withContext(Dispatchers.IO) { localSource.deleteById(noteId) }
    }
}