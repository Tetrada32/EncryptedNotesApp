package com.gahov.encrypted_notes.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gahov.encrypted_notes.domain.entities.Note

/**
 * Displays a note item as a clickable card with a pin checkbox.
 *
 * This composable shows the note's message and creation time, along with a checkbox indicating
 * whether the note is pinned. Tapping the card triggers the [onNoteClicked] callback,
 * a long press triggers [onNoteLongClicked], and toggling the checkbox triggers [onPinChanged].
 *
 * @param note The note to be displayed.
 * @param onNoteClicked Callback invoked when the note is clicked.
 * @param onNoteLongClicked Callback invoked when the note is long-clicked.
 * @param onPinChanged Callback invoked with the new pinned state when the checkbox is toggled.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoteItemUi(
    note: Note,
    onNoteClicked: (Note) -> Unit,
    onNoteLongClicked: (Note) -> Unit,
    onPinChanged: ((Boolean) -> Unit)? = null,
) {
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
        // Row to arrange the note content and the checkbox.
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Column for the note text content.
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = note.message ?: "",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                note.createdAt?.let {
                    Text(
                        text = "Created at: $it",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        fontSize = 12.sp
                    )
                }
            }
            // Checkbox for indicating whether the note is pinned.
            var isPinned by remember { mutableStateOf(note.isPinned) }
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
        }
    }
}
