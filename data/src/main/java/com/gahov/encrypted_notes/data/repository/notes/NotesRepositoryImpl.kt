package com.gahov.encrypted_notes.data.repository.notes

import com.gahov.encrypted_notes.data.mapper.NotesLocalMapper
import com.gahov.encrypted_notes.data.source.notes.NotesLocalSource
import com.gahov.encrypted_notes.domain.common.Either
import com.gahov.encrypted_notes.domain.entities.Failure
import com.gahov.encrypted_notes.domain.entities.NoteEntity
import com.gahov.encrypted_notes.domain.repository.NotesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class NotesRepositoryImpl(
    private val localSource: NotesLocalSource,
    private val localMapper: NotesLocalMapper
) : NotesRepository {

    override suspend fun fetchAllNotes(): Flow<Either<Failure, List<NoteEntity>>> {
        return withContext(Dispatchers.IO) {
            localSource.fetchNotes().map { result ->
                when (result) {
                    is Either.Left -> result
                    is Either.Right -> Either.Right(localMapper.toDomain(result.success))
                }
            }
        }
    }

    override suspend fun fetchPinnedNotes(): Flow<Either<Failure, List<NoteEntity>>> {
        return withContext(Dispatchers.IO) {
            localSource.fetchPinnedNotes().map { result ->
                when (result) {
                    is Either.Left -> result
                    is Either.Right -> Either.Right(localMapper.toDomain(result.success))
                }
            }
        }
    }

    override suspend fun deleteAllNotes(): Either<Failure, Unit> {
        return withContext(Dispatchers.IO) { localSource.deleteAllNotes() }
    }

    override suspend fun addNote(note: NoteEntity): Either<Failure, Unit> {
        return withContext(Dispatchers.IO) {
            localSource.addNotes(listOf(localMapper.toDatabase(note)))
        }
    }

    override suspend fun switchPinStatus(noteId: Long, isPinned: Boolean): Either<Failure, Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteNote(noteId: Long): Either<Failure, Unit> {
        return withContext(Dispatchers.IO) { localSource.deleteById(noteId) }
    }
}