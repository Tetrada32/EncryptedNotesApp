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

/**
 * Implementation of [NotesRepository] which handles note operations such as fetching, adding,
 * updating, deleting, exporting, and importing notes. This implementation interacts with a local
 * data source, performs necessary data mapping, handles encryption/decryption via [CryptoManager],
 * and manages JSON file conversion through [JsonFileConverter].
 *
 * @property localSource the local data source for note persistence.
 * @property localMapper the mapper to convert between local (database) and domain models.
 * @property cryptoManager the manager used for encrypting note content.
 * @property jsonFileConverter the converter used for converting note data to/from JSON files.
 */
class NotesRepositoryImpl(
    private val localSource: NotesLocalSource,
    private val localMapper: NotesLocalMapper,
    private val cryptoManager: CryptoManager,
    private val jsonFileConverter: JsonFileConverter
) : NotesRepository {

    /**
     * Fetches all notes from the local source.
     *
     * This method retrieves a flow of notes from the local database, mapping the results
     * from local models to domain models using [localMapper]. It uses the IO dispatcher
     * to perform asynchronous I/O operations.
     *
     * @return a [Flow] emitting an [Either] instance containing a [Failure] in case of an error or
     * a list of [Note] on success.
     */
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

    /**
     * Adds a new note to the local database.
     *
     * The provided note is converted from the domain model to the local (database) model using
     * [localMapper] before being stored.
     *
     * @param note the [Note] to be added.
     * @return an [Either] instance contains a [Failure] if the operation fails or [Unit] if successful.
     */
    override suspend fun addNote(note: Note): Either<Failure, Unit> {
        return withContext(Dispatchers.IO) {
            localSource.addNotes(listOf(localMapper.toDatabase(note)))
        }
    }

    /**
     * Updates an existing note in the local database.
     *
     * This method encrypts the new note message using [cryptoManager] before updating the note.
     *
     * @param noteId the unique identifier of the note to update.
     * @param message the new message content for the note.
     * @param isPinned a flag indicating whether the note should be pinned.
     * @param deletedAt a timestamp representing when the note was deleted (or 0 if not deleted).
     * @return an [Either] instance containing a [Failure] if the operation fails or [Unit] if successful.
     */
    override suspend fun updateNote(
        noteId: Long,
        message: String,
        isPinned: Boolean,
        deletedAt: Long,
    ): Either<Failure, Unit> {
        return withContext(Dispatchers.IO) {
            val encryptedMessage = cryptoManager.encryptToString(message)
            localSource.updateNote(noteId, encryptedMessage.getOrElse(""), isPinned, deletedAt)
        }
    }

    /**
     * Deletes a note from the local data source by its unique identifier.
     *
     * @param noteId the unique identifier of the note to delete.
     * @return an [Either] instance containing a [Failure] if the operation fails or [Unit] if successful.
     */
    override suspend fun deleteNote(noteId: Long): Either<Failure, Unit> {
        return withContext(Dispatchers.IO) { localSource.deleteById(noteId) }
    }

    /**
     * Prepares all notes for export by converting them into a JSON file.
     *
     * This method first fetches all notes, converts the data using [localMapper],
     * and then converts it into a JSON file using [jsonFileConverter]. If an error occurs
     * during conversion, it returns a [Failure.DataSourceException].
     *
     * @return an [Either] instance contains a [Failure] if the operation fails or
     * a [File] representing the exported notes if successful.
     */
    override suspend fun prepareToExportNotes(): Either<Failure, File> {
        val result = localSource.fetchNotes().first()
        return withContext(Dispatchers.IO) {
            when (result) {
                is Either.Left -> result
                is Either.Right -> {
                    try {
                        Either.Right(jsonFileConverter.toJson(result.success))
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Either.Left(Failure.DataSourceException(e))
                    }
                }
            }
        }
    }

    /**
     * Imports notes from a provided JSON file.
     *
     * This method reads note data from the given file using [jsonFileConverter] and adds them to
     * the local data source. In case of an error during the import, it returns a [Failure.DataSourceException].
     *
     * @param notesFile the [File] containing the notes in JSON format.
     * @return an [Either] instance contains a [Failure] if the operation fails or [Unit] if successful.
     */
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