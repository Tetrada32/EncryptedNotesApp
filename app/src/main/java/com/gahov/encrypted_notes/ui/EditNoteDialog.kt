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
 * Displays a dialog for editing an existing note.
 *
 * This composable shows an AlertDialog with a TextField pre-filled with the note's current message.
 * When the user confirms, it creates an updated copy of the note with the new message and an updated timestamp,
 * and then triggers the [onNoteUpdated] callback.
 *
 * @param note The note to be edited.
 * @param onDismiss Callback invoked when the dialog is dismissed without saving changes.
 * @param onNoteUpdated Callback invoked with the updated note when the user confirms the edit.
 */
@Composable
fun EditNoteDialog(
    note: Note,
    onDismiss: () -> Unit,
    onNoteUpdated: (Note) -> Unit
) {
    /**
     * Local state for the text input, initialized with the current note message.
     */
    var noteText by remember { mutableStateOf(note.message ?: "") }

    /**
     * Local state for the text input, initialized with the current note message.
     */
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Edit Note") },
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
                    /**
                     * Only update if the input text is not blank.
                     */
                    if (noteText.isNotBlank()) {
                        val updatedNote = note.copy(
                            message = noteText,
                            updatedAt = now().toEpochMilliseconds()
                        )
                        onNoteUpdated(updatedNote)
                    }
                }
            ) {
                Text(text = "Update")
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