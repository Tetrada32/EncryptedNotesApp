package com.gahov.encrypted_notes.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gahov.encrypted_notes.domain.entities.Note

/**
 * Displays a note item as a clickable card.
 *
 * This composable shows the note message and its creation timestamp (if available) within a card.
 * The card supports both short click (for editing) and long click (for deletion) actions.
 *
 * @param note The note to be displayed.
 * @param onNoteClicked Callback triggered when the note is clicked (e.g., to edit the note).
 * @param onNoteLongClicked Callback triggered when the note is long-clicked (e.g., to prompt deletion).
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoteItemUi(
    note: Note,
    onNoteClicked: (Note) -> Unit,
    onNoteLongClicked: (Note) -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            /**
             * combinedClickable handles both click and long-click events.
             */
            .combinedClickable(
                onClick = { onNoteClicked(note) },
                onLongClick = { onNoteLongClicked(note) },
            )
        ,
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            /**
             * Display the note message or an empty string if null.
             */
            Text(
                text = note.message ?: "",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            /**
             * If the creation timestamp is available, display it.
             */
            note.createdAt?.let {
                Text(
                    text = "Created at: $it",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    fontSize = 12.sp
                )
            }
        }
    }
}