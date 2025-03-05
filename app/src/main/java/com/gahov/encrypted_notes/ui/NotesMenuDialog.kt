package com.gahov.encrypted_notes.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gahov.encrypted_notes.ui.command.ActionCommand

/**
 * Displays a dialog that allows the user to choose between exporting or importing notes.
 *
 * When an option is selected, the [onCommandSelected] callback is invoked with the command:
 * either "export" or "import". The dialog can be dismissed without taking action.
 *
 * @param onDismiss Callback invoked when the dialog is dismissed.
 * @param onCommandSelected Callback invoked with the selected command.
 */
@Composable
fun NotesMenuDialog(
    onDismiss: () -> Unit,
    onCommandSelected: (command: ActionCommand) -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Select Action") },
        text = {
            Column {
                Text(
                    text = "Export Notes",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onCommandSelected(ActionCommand.Export) }
                        .padding(16.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Import Notes",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onCommandSelected(ActionCommand.Import) }
                        .padding(16.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancel")
            }
        },
        // No confirm button needed since selection is done via clickable text.
        confirmButton = {}
    )
}