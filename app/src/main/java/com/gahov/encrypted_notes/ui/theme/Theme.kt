package com.gahov.encrypted_notes.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFD0BCFF),
    secondary = Color(0xFFCCC2DC),
    tertiary = Color(0xFFEFB8C8),
    surface = Color(0xFF625b71),
    background = Color(red = 28, green = 27, blue = 31)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF6650a4),
    secondary = Color(0xFF625b71),
    tertiary = Color(0xFF7D5260),
)

/**
 * MyApplicationTheme sets up the Material theme for the application.
 *
 * This composable applies a light or dark color scheme based on the system's current theme.
 *
 * @param darkTheme A Boolean flag to determine whether the dark theme should be used.
 *                  Defaults to the system's current dark theme setting.
 * @param content The UI content that will be styled using the applied theme.
 */
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    /**
     * Choose the color scheme based on whether darkTheme is true or false.
     */
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    /**
     *  Apply the selected color scheme to MaterialTheme.
     */
    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}