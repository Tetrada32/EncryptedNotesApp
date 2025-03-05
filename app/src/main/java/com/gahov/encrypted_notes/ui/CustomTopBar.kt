package com.gahov.encrypted_notes.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * CustomTopBar displays the top app bar for the application.
 *
 * This composable uses Material3's TopAppBar with custom colors.
 * The title text is set to "Encrypted Notes App" with white color,
 * and the background (container) color is set to the primary color of the current theme.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopBar() {
    TopAppBar(
        title = { Text(text = "Encrypted Notes App") },
        colors = TopAppBarDefaults.topAppBarColors(
            titleContentColor = Color.White,
            containerColor = MaterialTheme.colorScheme.primary,
        )
    )
}