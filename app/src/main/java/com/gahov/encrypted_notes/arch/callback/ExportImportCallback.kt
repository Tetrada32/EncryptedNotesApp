package com.gahov.encrypted_notes.arch.callback

import java.io.File

/**
 * Callback interface for handling export and import actions on notes.
 */
interface ExportImportCallback {

    /**
     * Called when notes should be exported.
     *
     * @param file The list of notes to be exported in file
     */
    fun exportNotes(file: File)

    /**
     * Called when notes should be imported.
     */
    fun importNotes()
}