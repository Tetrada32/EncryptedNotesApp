package com.gahov.encrypted_notes.ui

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.DialogProperties
import kotlinx.datetime.Clock.System.now

/**
 * Displays a custom Time Picker Dialog built using AlertDialog and Material3's TimePicker.
 *
 * This dialog allows the user to select a time (hour and minute) using the TimePicker composable.
 * When the user confirms the selection, the [onTimeSelected] callback is invoked with a timestamp
 * (milliseconds) corresponding to the selected time on the current day.
 *
 * @param onTimeSelected Callback invoked with the selected time as a timestamp (milliseconds).
 * @param onDismiss Callback invoked when the dialog is dismissed.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    onTimeSelected: (Long) -> Unit,
    onDismiss: () -> Unit,
) {
    // Create and remember the TimePicker state.
    val timePickerState = rememberTimePickerState(is24Hour = true)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Select Time After Which This Note Will Be Deleted") },
        text = {
            // Use the TimePicker composable as the dialog content.
            TimePicker(state = timePickerState)
        },
        confirmButton = {
            TextButton(
                onClick = {
                    // Use kotlinx.datetime to build a timestamp for today with the selected time.
                    val currentTimeMillis = now().toEpochMilliseconds()
                    // Round down to start minute.
                    val roundedMillis = currentTimeMillis - (currentTimeMillis % 60000)
                    // Get a choose
                    val selectedMinuteAsMillis = timePickerState.minute * 60 * 1000
                    val selectedHourAsMillis = timePickerState.hour * 60 * 60 * 1000
                    onTimeSelected(roundedMillis + selectedMinuteAsMillis + selectedHourAsMillis)
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        properties = DialogProperties()
    )
}