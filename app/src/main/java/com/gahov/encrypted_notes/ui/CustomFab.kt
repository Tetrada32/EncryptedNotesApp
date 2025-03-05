package com.gahov.encrypted_notes.ui

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


/**
 * CustomFab displays a custom Floating Action Button.
 *
 * This composable creates a FAB with a circular shape and a specified size.
 * When clicked, it triggers the provided [onClick] callback.
 *
 * @param onClick Callback function to be invoked when the FAB is clicked.
 */
@Composable
fun CustomFab(
    onClick: () -> Unit
) {
    FloatingActionButton(
        modifier = Modifier.size(100.dp),
        onClick = onClick,
        shape = CircleShape
    ) {
        /**
         * Displays an "Add" icon within the FAB.
         */
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add Note"
        )
    }
}