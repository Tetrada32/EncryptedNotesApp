package com.gahov.encrypted_notes.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gahov.encrypted_notes.domain.entities.Note
import com.gahov.encrypted_notes.utils.formatDate
import com.gahov.encrypted_notes.utils.yearFromNow

/**
 * Displays a note item as a card with a pin checkbox, a deletion date indicator, and a three-dot menu
 * to open a date picker dialog for setting the deletion date.
 *
 * Tapping the card triggers [onNoteClicked], a long press triggers [onNoteLongClicked].
 * Toggling the checkbox calls [onPinChanged]. If a deletion date is set, it is displayed at the bottom in red.
 * Tapping the three-dot menu opens a Date Picker Dialog; upon selecting a date, [onDeleteDateChanged] is called.
 *
 * @param note The note to be displayed.
 * @param onNoteClicked Callback invoked when the note is clicked.
 * @param onNoteLongClicked Callback invoked when the note is long-clicked.
 * @param onPinChanged Callback invoked with the new pin state when the checkbox is toggled.
 * @param onDeleteDateChanged Callback invoked with the new deletion timestamp when a date is selected.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoteItemUi(
    note: Note,
    onNoteClicked: (Note) -> Unit,
    onNoteLongClicked: (Note) -> Unit,
    onPinChanged: ((Boolean) -> Unit)? = null,
    onDeleteDateChanged: ((Long) -> Unit)? = null,
) {

    // Local state for the pinned checkbox.
    var isPinned by remember { mutableStateOf(note.isPinned) }

    // Local state to control the visibility of the date picker dialog.
    var showDatePickerDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .combinedClickable(
                onClick = { onNoteClicked(note) },
                onLongClick = { onNoteLongClicked(note) },
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Row for the note content and the actions (checkbox and three-dot menu).
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = note.message ?: "",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    note.createdAt?.let {
                        Text(
                            text = "Created at: ${it.formatDate()}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            fontSize = 12.sp
                        )
                    }
                }
                // Row for the pin checkbox and three-dot menu.
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    Checkbox(
                        checked = isPinned,
                        onCheckedChange = { newValue ->
                            isPinned = newValue
                            onPinChanged?.invoke(newValue)
                        },
                        colors = CheckboxDefaults.colors(
                            checkedColor = MaterialTheme.colorScheme.primary,
                            uncheckedColor = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    IconButton(
                        onClick = { showDatePickerDialog = true }
                    ) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.MoreVert,
                            contentDescription = "Set deletion date",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            /**
             * If the note has a deletion date set, display it at the bottom in red.
             */
            var deletedAtAsString by remember { mutableStateOf("") }

            LaunchedEffect(note.deletedAt) {
                if (note.deletedAt == null) deletedAtAsString = ""
                note.deletedAt?.let { deletedAt ->
                    deletedAtAsString = if (deletedAt < yearFromNow()) {
                        "Will be deleted at: ${deletedAt.formatDate()}"
                    } else {
                        ""
                    }
                }
            }

            if (deletedAtAsString.isNotBlank()) {
                Text(
                    text = deletedAtAsString,
                    style = MaterialTheme.typography.bodySmall.merge(
                        TextStyle(color = MaterialTheme.colorScheme.error)
                    ),
                )
            }
        }
    }

    if (showDatePickerDialog) {
        TimePickerDialog(
            onTimeSelected = { selectedTime ->
                println("selected time : $selectedTime")
                onDeleteDateChanged?.invoke(selectedTime)
                showDatePickerDialog = false
            },
            onDismiss = { showDatePickerDialog = false }
        )
    }
}