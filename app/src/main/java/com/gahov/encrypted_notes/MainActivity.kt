package com.gahov.encrypted_notes

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.gahov.encrypted_notes.arch.callback.ExportImportCallback
import com.gahov.encrypted_notes.ui.NotesScreenUi
import com.gahov.encrypted_notes.ui.theme.MyApplicationTheme
import com.gahov.encrypted_notes.utils.MIME_JSON
import com.gahov.encrypted_notes.utils.MIME_TEXT
import com.gahov.encrypted_notes.utils.createSendIntent
import com.gahov.encrypted_notes.utils.getUriForFile
import com.gahov.encrypted_notes.utils.uriToFile
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File

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
            val currentThemeValue = isSystemInDarkTheme()
            var darkThemeState by remember { mutableStateOf(currentThemeValue) }

            MyApplicationTheme(darkTheme = darkThemeState) {
                NotesScreenUi(notesViewModel, darkThemeState) { isDarkThemeEnabledNow ->
                    darkThemeState = isDarkThemeEnabledNow
                }
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
                notesViewModel.exportImportCallback = object : ExportImportCallback {
                    override fun exportNotes(file: File) {
                        exportNotesList(file)
                    }

                    override fun importNotes() {
                        importNotesList()
                    }
                }
                notesViewModel.onStart()
            }
        }
    }

    /**
     * Shares the provided JSON file via an intent.
     *
     */
    private fun exportNotesList(exportFile: File) {
        startActivity(createSendIntent(getUriForFile(exportFile)))
    }

    /**
     * Launches file picker to select a JSON or TXT file for import.
     *
     */
    private fun importNotesList() {
        importFileLauncher.launch(arrayOf(MIME_JSON, MIME_TEXT))
    }

    /**
     * ActivityResultLauncher to handle file selection result.
     */
    private val importFileLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
            uri?.let { selectedUri ->
                contentResolver.takePersistableUriPermission(
                    selectedUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                val tempFile = uriToFile(cacheDir, selectedUri, contentResolver) ?: return@let
                notesViewModel.importNotes(tempFile)
            }
        }
}