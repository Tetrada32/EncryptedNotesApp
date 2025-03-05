package com.gahov.encrypted_notes.feature

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gahov.encrypted_notes.domain.common.Either
import com.gahov.encrypted_notes.domain.entities.Failure
import com.gahov.encrypted_notes.domain.entities.Note
import com.gahov.encrypted_notes.domain.repository.NotesRepository
import com.gahov.encrypted_notes.ui.command.ActionCommand
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(
    private val notesRepository: NotesRepository
) : ViewModel() {

    var exportImportCallback: ExportImportCallback? = null

    /**
     * Callback interface for handling export and import actions on notes.
     */
    interface ExportImportCallback {
        /**
         * Called when notes should be exported.
         *
         * @param notesList The list of notes to be exported.
         */
        fun exportNotes(notesFile: File)

        /**
         * Called when notes should be imported.
         */
        fun importNotes()
    }


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
    fun onStart() {
        fetchContent()
    }

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
        fullNotesList = result.toMutableList()
        _notesListAsStateFlow.value = result.toList()
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
        //TODO change "isPinned" status
        viewModelScope.launch {
            note.id?.let {
                notesRepository.updateNote(it, note.message.toString(), !note.isPinned)
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
            /**
             * If the search input is empty, display the full list of notes.
             */
            _notesListAsStateFlow.value = fullNotesList.toList()
        } else {
            /**
             * Filter the full notes list based on the search input (ignoring case).
             */
            val filteredNotes = fullNotesList.filter { note ->
                note.message?.contains(inputData, ignoreCase = true) == true
            }.toMutableList()
            _notesListAsStateFlow.value = filteredNotes.toList()
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
            ActionCommand.Export -> prepareExportNotes()
            ActionCommand.Import -> exportImportCallback?.importNotes()
        }
    }

    private fun prepareExportNotes() {
        viewModelScope.launch {
            when (val result = notesRepository.prepareToExportNotes()) {
                is Either.Left -> onError(result.failure)
                is Either.Right -> exportImportCallback?.exportNotes(result.success)
            }
        }
    }
}