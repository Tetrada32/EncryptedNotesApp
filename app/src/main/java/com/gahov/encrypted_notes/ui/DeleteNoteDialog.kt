package com.gahov.encrypted_notes.ui

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.DialogProperties
import com.gahov.encrypted_notes.domain.entities.Note

/**
 * Displays a confirmation dialog for deleting a note.
 *
 * This composable shows an AlertDialog prompting the user to confirm the deletion of the given note.
 * If the user confirms, the [onConfirmDelete] callback is invoked with the note to be deleted.
 * If the user cancels, the [onDismiss] callback is triggered.
 *
 * @param note The note that is intended to be deleted.
 * @param onDismiss Callback invoked when the dialog is dismissed without deleting the note.
 * @param onConfirmDelete Callback invoked when the user confirms the deletion of the note.
 */
@Composable
fun DeleteNoteDialog(
    note: Note,
    onDismiss: () -> Unit,
    onConfirmDelete: (Note) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Delete Note") },
        text = { Text(text = "Are you sure you want to delete this note?") },
        confirmButton = {
            TextButton(onClick = { onConfirmDelete(note) }) {
                Text(text = "Delete")
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