package com.gahov.encrypted_notes.data.source.notes

import com.gahov.encrypted_notes.data.storage.entities.NoteDTO
import com.gahov.encrypted_notes.data.storage.storage.NotesDao
import com.gahov.encrypted_notes.domain.common.Either
import com.gahov.encrypted_notes.domain.entities.Failure
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.datetime.Clock.System.now

class NotesLocalSourceImpl(private val notesDao: NotesDao) : NotesLocalSource {

    override suspend fun fetchNotes(): Flow<Either<Failure, List<NoteDTO>>> {
        return flow {
            notesDao.fetchAll(now().toEpochMilliseconds()).collect {
                emit(Either.Right(success = it))
            }
        }.flowOn(Dispatchers.IO).catch { Either.Left(Failure.DataSourceException(it)) }
    }

    override suspend fun addNotes(notes: List<NoteDTO>): Either<Failure, Unit> {
        return try {
            Either.Right(notesDao.insertItems(notes))
        } catch (e: Exception) {
            Either.Left(Failure.DataSourceException(e))
        }
    }

    override suspend fun updateNote(
        id: Long,
        message: String,
        isPinned: Boolean
    ): Either<Failure, Unit> {
        return try {
            Either.Right(notesDao.updateNote(id, message, isPinned))
        } catch (e: Exception) {
            Either.Left(Failure.DataSourceException(e))
        }
    }

    override suspend fun deleteById(id: Long): Either<Failure, Unit> {
        return try {
            Either.Right(notesDao.deleteItemById(id))
        } catch (e: Exception) {
            Either.Left(Failure.DataSourceException(e))
        }
    }
}