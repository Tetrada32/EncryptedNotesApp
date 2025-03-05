package com.gahov.encrypted_notes.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.gahov.encrypted_notes.domain.entities.Note
import com.gahov.encrypted_notes.feature.NotesViewModel


/**
 * Displays the main UI screen for notes.
 *
 * This composable sets up the overall layout including the top bar, FAB, and notes list.
 * It handles the state for showing dialogs for adding, editing, and deleting notes.
 *
 * @param viewModel The ViewModel that holds the list of notes and performs note operations.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreenUi(
    viewModel: NotesViewModel? = null,
    isDarkThemeEnabled: Boolean = false,
    themeChangedCallback: ((isDarkThemeEnabledNow: Boolean) -> Unit)? = null,
) {
    // State to control the visibility of the "Add Note" dialog.
    var showDialog by remember { mutableStateOf(false) }
    // State to hold the note being edited (if any), to display the "Edit Note" dialog.
    var editingNote by remember { mutableStateOf<Note?>(null) }
    // State to hold the note selected for deletion (if any), to display the "Delete Note" dialog.
    var noteToDelete by remember { mutableStateOf<Note?>(null) }

    // Update status bar icons (for better contrast or style).
    UpdateStatusBarIcons()

    // Scaffold provides the basic structure of the UI (top bar, FAB, content).
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TopBarWithSearchButton(
                titleText = "My Notes",
                isBackButtonEnabled = false,
                isSearchButtonEnabled = true,
                isMenuEnabled = true,
                onSearchInputUpdate = { inputData -> viewModel?.onSearchInputChanged(inputData) },
                onMenuCommand = { command ->
                    viewModel?.onActionCommand(command)
                },
                isDarkThemeEnabled = isDarkThemeEnabled,
                isDarkThemeChangedCallback = themeChangedCallback ?: { /* */ }
            )
        },
        floatingActionButton = {
            // Custom FAB with click callback to show the "Add Note" dialog.
            CustomFab {
                showDialog = true
            }
        }
    ) { paddingValues ->
        // Box that applies the inner padding from the Scaffold to avoid system UI overlap.
        Box(
            modifier = Modifier
                .padding(paddingValues)
        ) {
            // Display the list of notes with click and long-click callbacks.
            NotesListUi(
                viewModel = viewModel,
                onNoteClicked = { note -> editingNote = note },
                onNoteLongClicked = { note -> noteToDelete = note },
            )
        }
    }

    // Show the "Add Note" dialog when showDialog is true.
    if (showDialog) {
        AddNoteDialog(
            onDismiss = { showDialog = false },
            onNoteAdded = { newNote ->
                viewModel?.addNote(newNote)
                showDialog = false
            }
        )
    }

    // Show the "Edit Note" dialog if there is a note being edited.
    editingNote?.let { note ->
        EditNoteDialog(
            note = note,
            onDismiss = { editingNote = null },
            onNoteUpdated = { updatedNote ->
                viewModel?.updateNote(updatedNote)
                editingNote = null
            }
        )
    }

    // Show the "Delete Note" dialog if a note is selected for deletion.
    noteToDelete?.let { note ->
        DeleteNoteDialog(
            note = note,
            onDismiss = { noteToDelete = null },
            onConfirmDelete = { selectedNote ->
                viewModel?.deleteNote(selectedNote)
                noteToDelete = null
            }
        )
    }
}