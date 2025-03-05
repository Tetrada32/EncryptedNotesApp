package com.gahov.encrypted_notes.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gahov.encrypted_notes.domain.entities.Note
import com.gahov.encrypted_notes.feature.NotesViewModel

/**
 * Displays a list of notes in a LazyColumn.
 *
 * This composable observes the notes list from the provided ViewModel and displays each note
 * using the [NoteItemUi] composable. It also provides click callbacks for editing and deleting notes.
 *
 * @param viewModel The ViewModel containing the list of notes.
 * @param onNoteClicked Callback invoked when a note is clicked (e.g., for editing).
 * @param onNoteLongClicked Callback invoked when a note is long-clicked (e.g., for deletion).
 */
@Composable
fun NotesListUi(
    viewModel: NotesViewModel?,
    onNoteClicked: (Note) -> Unit,
    onNoteLongClicked: (Note) -> Unit,
) {
    /**
     * Observe the notes list from the ViewModel, using an empty list as the initial value.
     * If viewModel is null, use a remembered empty list.
     */
    val notesList by viewModel?.notesListAsStateFlow?.collectAsState(
        initial = emptyList()
    ) ?: remember {
        mutableStateOf(listOf())
    }

    LazyColumn(modifier = Modifier.padding(top = 8.dp)) {
        items(notesList) { note ->
            NoteItemUi(note = note, onNoteClicked, onNoteLongClicked)
        }
    }
}