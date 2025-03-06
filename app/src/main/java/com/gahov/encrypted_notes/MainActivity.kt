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
import androidx.core.content.FileProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.gahov.encrypted_notes.arch.callback.ExportImportCallback
import com.gahov.encrypted_notes.ui.NotesScreenUi
import com.gahov.encrypted_notes.ui.theme.MyApplicationTheme
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
     * This method obtains a content URI for the provided file using a FileProvider,
     * then creates and launches an intent chooser to share the file.
     *
     * Make sure you have configured the FileProvider in your AndroidManifest.xml
     * and added a file_paths.xml resource with proper paths.
     *
     * @param exportFile The JSON file to be shared.
     */
    private fun exportNotesList(exportFile: File) {
        val fileUri = FileProvider.getUriForFile(
            this,
            "${applicationContext.packageName}.fileprovider",
            exportFile
        )

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/json"
            putExtra(Intent.EXTRA_STREAM, fileUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        val chooserIntent = Intent.createChooser(shareIntent, "Share notes via")
        startActivity(chooserIntent)
    }

    /**
     * Launches file picker to select a JSON or TXT file for import.
     *
     * This method creates and launches an intent to pick a file from the device storage.
     * It expects a file with MIME types "application/json" or "text/plain". Once a file is selected,
     * the ActivityResultLauncher obtains persistable read permission, converts the file URI to a File,
     * and then passes it to the JSON converter for parsing.
     */
    private fun importNotesList() {
        importFileLauncher.launch(arrayOf("application/json", "text/plain"))
    }

    /**
     * ActivityResultLauncher to handle file selection result.
     */
    private val importFileLauncher =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
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