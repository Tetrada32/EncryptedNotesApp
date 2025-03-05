package com.gahov.encrypted_notes.ui

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.DialogProperties
import com.gahov.encrypted_notes.domain.entities.Note
import kotlinx.datetime.Clock.System.now

/**
 * Displays a dialog for adding a new note.
 *
 * This composable shows an AlertDialog with a TextField for entering the note's text.
 * When confirmed, it creates a new Note with the current timestamp for both creation and update times,
 * and triggers the [onNoteAdded] callback.
 *
 * @param onDismiss Callback invoked when the dialog is dismissed without adding a note.
 * @param onNoteAdded Callback invoked with the new Note when the user confirms the addition.
 */
@Composable
fun AddNoteDialog(
    onDismiss: () -> Unit,
    onNoteAdded: (Note) -> Unit
) {
    // Local state for the text input of the note.
    var noteText by remember { mutableStateOf("") }

    // Display the AlertDialog for adding a new note.
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Add New Note") },
        text = {
            TextField(
                value = noteText,
                onValueChange = { noteText = it },
                placeholder = { Text(text = "Enter note text") }
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (noteText.isNotBlank()) {
                        // Get the current time in milliseconds.
                        val currentTimeAsLong = now().toEpochMilliseconds()
                        // Create a new note with the entered text and current timestamp.
                        val newNote = Note(
                            message = noteText,
                            createdAt = currentTimeAsLong
                        )
                        onNoteAdded(newNote)
                    }
                }
            ) {
                Text(text = "Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancel")
            }
        },
        properties = DialogProperties()
    )
}