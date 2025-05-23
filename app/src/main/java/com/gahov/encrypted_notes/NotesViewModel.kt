package com.gahov.encrypted_notes

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gahov.encrypted_notes.arch.callback.ExportImportCallback
import com.gahov.encrypted_notes.arch.command.ActionCommand
import com.gahov.encrypted_notes.domain.common.Either
import com.gahov.encrypted_notes.domain.common.getOrNull
import com.gahov.encrypted_notes.domain.entities.Failure
import com.gahov.encrypted_notes.domain.entities.Note
import com.gahov.encrypted_notes.domain.repository.NotesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock.System.now
import java.io.File
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(
    private val notesRepository: NotesRepository
) : ViewModel() {

    var exportImportCallback: ExportImportCallback? = null

    private var fullNotesList = mutableListOf<Note>()

    /**
     *  Private mutable state holding the list of notes.
     */
    private val _notesListAsStateFlow = MutableStateFlow(listOf<Note>())

    /**
     *  Publicly exposed immutable state for observers.
     */
    val notesListAsStateFlow = _notesListAsStateFlow.asStateFlow()


    /**
     * Called when the ViewModel starts. Initializes the observation of database notes and,
     * for testing purposes, populates the state with test data.
     */
    fun onStart() { fetchContent() }

    private fun fetchContent() {
        viewModelScope.launch {
            notesRepository.fetchAllNotes().collect {
                when (it) {
                    is Either.Right -> onSuccess(it.success)
                    is Either.Left -> onError(it.failure)
                }
            }
        }
    }

    private fun onSuccess(result: List<Note>) {
        // Update the full list from the repository.
        fullNotesList = result.toMutableList()
        // Sort the notes: pinned ones first, then by createdAt descending.
        val sortedNotes = sortNotes(fullNotesList)
        _notesListAsStateFlow.value = sortedNotes
        // Plan updates for deletion of notes
        scheduleNextDeletionCheck(sortedNotes)
    }

    private fun onError(f: Failure) {
        Log.e("FetchFailure", "Fetch content failure: $f")
    }

    /**
     * Adds a new note.
     *
     * @param note The note to be added.
     */
    fun addNote(note: Note) {
        viewModelScope.launch { notesRepository.addNote(note) }
    }

    /**
     * Updates an existing note.
     *
     * @param note The note with updated data.
     */
    fun updateNote(note: Note) {
        viewModelScope.launch {
            note.id?.let {
                notesRepository.updateNote(
                    it,
                    note.message.toString(),
                    note.isPinned,
                    note.deletedAt ?: Long.MAX_VALUE
                )
            }
        }
    }

    /**
     * Deletes an existing note.
     *
     * Removes the note from the list by its ID.
     *
     * @param note The note to be deleted.
     */
    fun deleteNote(note: Note) {
        viewModelScope.launch { note.id?.let { notesRepository.deleteNote(it) } }
    }

    /**
     * Filters the notes based on the provided search input.
     *
     * This function filters notes by their [Note.message] field. When [inputData] is empty,
     * the full list of notes is restored. Otherwise, it filters the list to only include notes
     * whose message contains the [inputData] string (case-insensitive).
     *
     * IMPORTANT: This function assumes that [fullNotesList] holds the complete list of notes.
     * Make sure to update [fullNotesList] appropriately when adding, updating, or deleting notes.
     *
     * @param inputData The search query string used to filter notes.
     */
    fun onSearchInputChanged(inputData: String) {
        if (inputData.isBlank()) {
            // When the search input is empty, show the full sorted list.
            _notesListAsStateFlow.value = sortNotes(fullNotesList)
        } else {
            // Filter the full list based on the search input (ignoring case) and then sort.
            val filteredNotes = fullNotesList.filter { note ->
                note.message?.contains(inputData, ignoreCase = true) == true
            }
            _notesListAsStateFlow.value = sortNotes(filteredNotes)
        }
    }

    /**
     * Processes the given action command to either export or import notes.
     *
     * When the command is [ActionCommand.Export], it calls the [ExportImportCallback.exportNotes]
     * method with the full list of notes. When the command is [ActionCommand.Import], it calls the
     * [ExportImportCallback.importNotes] method.
     *
     * @param command The action command to be processed.
     */
    fun onActionCommand(command: ActionCommand) {
        when (command) {
            ActionCommand.Export -> viewModelScope.launch { prepareFile()?.let {
                exportImportCallback?.exportNotes(it) } }
            ActionCommand.Import -> exportImportCallback?.importNotes()
        }
    }

    private suspend fun prepareFile(): File? {
        return withContext(Dispatchers.IO) {
           notesRepository.prepareToExportNotes().getOrNull()
        }
    }

    fun importNotes(notes: File) {
        viewModelScope.launch { notesRepository.importNotes(notes) }
    }

    /**
     * Sorts the given list of notes so that pinned notes appear at the top, and within each group, notes are sorted
     * by their creation time (newest first).
     *
     * @param notes The list of notes to be sorted.
     * @return A sorted list of notes.
     */
    private fun sortNotes(notes: List<Note>): List<Note> {
        return notes.sortedWith(
            compareByDescending<Note> { it.isPinned }
                .thenBy { it.createdAt ?: 0L }
        )
    }

    private fun scheduleNextDeletionCheck(notes: List<Note>) {
        val currentTime = now().toEpochMilliseconds()
        val futureDeletionTimes = notes.mapNotNull { it.deletedAt }.filter { it > currentTime }
        val nextDeletionTime = futureDeletionTimes.minOrNull()
        if (nextDeletionTime != null) {
            val delayMillis = nextDeletionTime - currentTime
            viewModelScope.launch {
                delay(delayMillis)
                fetchContent()
            }
        }
    }
}