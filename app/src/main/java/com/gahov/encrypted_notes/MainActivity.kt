package com.gahov.encrypted_notes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.gahov.encrypted_notes.feature.NotesViewModel
import com.gahov.encrypted_notes.ui.NotesScreenUi
import com.gahov.encrypted_notes.ui.theme.MyApplicationTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * MainActivity serves as the entry point of the application.
 * It sets up the custom theme, displays the UI that observes the ViewModel,
 * and initializes the ViewModel on startup.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val notesViewModel: NotesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /**
         *  Enables edge-to-edge mode so that the content occupies the entire screen.
         */
        enableEdgeToEdge()
        /**
         * Sets the Compose UI content using a custom theme.
         */
        setContent {
            MyApplicationTheme {
                NotesScreenUi(notesViewModel)
            }
        }

        initViewModel()
    }

    /**
     * Initializes the ViewModel by calling its onStart() method within a coroutine.
     * The coroutine runs in the lifecycleScope and repeats while the Activity's lifecycle
     * is at least in the STARTED state.
     */
    private fun initViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                notesViewModel.onStart()
            }
        }
    }
}