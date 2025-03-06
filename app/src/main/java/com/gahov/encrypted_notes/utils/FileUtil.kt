package com.gahov.encrypted_notes.utils

import android.content.ContentResolver
import android.net.Uri
import java.io.File
import java.io.IOException


/**
 * Converts Uri to File by copying content to a temporary file.
 */
fun uriToFile(cacheDir: File, uri: Uri, contentResolver: ContentResolver): File? {
    return try {
        val tempFile = File(cacheDir, "imported_notes.json")
        contentResolver.openInputStream(uri)?.use { inputStream ->
            tempFile.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        tempFile
    } catch (e: IOException) {
        null
    }
}