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

class NotesLocalSourceImpl(private val notesDao: NotesDao) : NotesLocalSource {

    override suspend fun fetchNotes(): Flow<Either<Failure, List<NoteDTO>>> {
        return flow {
            notesDao.fetchAll().collect {
                emit(Either.Right(success = it))
            }
        }.flowOn(Dispatchers.IO).catch { Either.Left(Failure.DataSourceException(it)) }
    }

    override suspend fun fetchPinnedNotes(): Flow<Either<Failure, List<NoteDTO>>> {
        return flow {
            notesDao.fetchPinned().collect {
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

    override suspend fun switchPinStatus(id: Long, isPinned: Boolean): Either<Failure, Unit> {
        return try {
            Either.Right(notesDao.updatePinnedStatus(id = id, isPinned = isPinned))
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

    override suspend fun deleteAllNotes(): Either<Failure, Unit> {
        return try {
            Either.Right(notesDao.deleteAll())
        } catch (e: Exception) {
            Either.Left(Failure.DataSourceException(e))
        }
    }
}