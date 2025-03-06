package com.gahov.encrypted_notes.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gahov.encrypted_notes.arch.command.ActionCommand

/**
 * Displays a dialog that allows the user to choose an action: Export, Import or toggle Dark Mode.
 *
 * In addition to the clickable options for Export and Import, this dialog displays a dark mode switch.
 * When the switch is toggled, the [onDarkModeToggled] callback is invoked with the new state.
 *
 * @param onDismiss Callback invoked when the dialog is dismissed.
 * @param darkTheme Current dark mode state; true if dark mode is enabled.
 * @param onDarkModeToggled Callback invoked when the dark mode switch is toggled.
 * @param onCommandSelected Callback invoked with the selected export/import command.
 */
@Composable
fun NotesMenuDialog(
    onDismiss: () -> Unit,
    darkTheme: Boolean,
    onDarkModeToggled: (Boolean) -> Unit,
    onCommandSelected: (command: ActionCommand) -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Select Action") },
        text = {
            Column {
                // Option for exporting notes.
                Text(
                    text = "Export Notes",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onCommandSelected(ActionCommand.Export) },
                    style = MaterialTheme.typography.bodyLarge
                )
                // Option for importing notes.
                Text(
                    text = "Import Notes",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onCommandSelected(ActionCommand.Import) }
                        .padding(top = 12.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
                // Dark Mode toggle row.
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    Text(
                        text = "Dark Mode",
                        fontSize = 16.sp,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Switch(
                        checked = darkTheme,
                        onCheckedChange = { onDarkModeToggled(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.surface,
                            uncheckedThumbColor = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancel")
            }
        },
        // No confirm button needed since selection is done via clickable options.
        confirmButton = {}
    )
}