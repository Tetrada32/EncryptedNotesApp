package com.gahov.encrypted_notes.ui

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * Updates the status bar icons appearance.
 *
 * This composable configures the system status bar icons based on the design of the toolbar.
 * In this case, it sets the icons to a light appearance because the toolbar has a dark design.
 * The operation is performed as a side effect to ensure it executes after composition.
 */
@Composable
fun UpdateStatusBarIcons() {
    val view = LocalView.current
    if (!view.isInEditMode) {
        // Perform the update as a side effect so that it runs after composition.
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }
}