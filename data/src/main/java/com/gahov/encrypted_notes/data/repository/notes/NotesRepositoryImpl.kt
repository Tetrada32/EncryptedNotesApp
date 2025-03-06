package com.gahov.encrypted_notes.data.repository.notes

import com.gahov.encrypted_notes.data.files.JsonFileConverter
import com.gahov.encrypted_notes.data.mapper.NotesLocalMapper
import com.gahov.encrypted_notes.data.security.manager.CryptoManager
import com.gahov.encrypted_notes.data.source.notes.NotesLocalSource
import com.gahov.encrypted_notes.domain.common.Either
import com.gahov.encrypted_notes.domain.common.getOrElse
import com.gahov.encrypted_notes.domain.entities.Failure
import com.gahov.encrypted_notes.domain.entities.Note
import com.gahov.encrypted_notes.domain.repository.NotesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.File

class NotesRepositoryImpl(
    private val localSource: NotesLocalSource,
    private val localMapper: NotesLocalMapper,
    private val cryptoManager: CryptoManager,
    private val jsonFileConverter: JsonFileConverter
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

    override suspend fun prepareToExportNotes(): Either<Failure, File> {
        val result = fetchAllNotes().first()
        return withContext(Dispatchers.IO) {
            when (result) {
                is Either.Left -> result
                is Either.Right -> {
                    try {
                        val encryptedData = localMapper.toDatabase(result.success)
                        Either.Right(jsonFileConverter.toJson(encryptedData))
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Either.Left(Failure.DataSourceException(e))
                    }
                }
            }
        }
    }

    override suspend fun importNotes(notesFile: File): Either<Failure, Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val notes = jsonFileConverter.fromJson(notesFile)
                localSource.addNotes(notes)
            } catch (e: Exception) {
                Either.Left(Failure.DataSourceException(e))
            }
        }
    }
}