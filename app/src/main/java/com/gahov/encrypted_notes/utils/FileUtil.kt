package com.gahov.encrypted_notes.utils

import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.gahov.encrypted_notes.MainActivity
import java.io.File
import java.io.IOException


const val MIME_JSON = "application/json"
const val MIME_TEXT = "text/plain"

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

/**
 * Creates an Intent for sharing the file.
 */
fun createSendIntent(fileUri: Uri, mimeType: String = MIME_JSON): Intent {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = mimeType
        putExtra(Intent.EXTRA_STREAM, fileUri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    val chooserIntent = Intent.createChooser(intent, "Share notes via")
    return chooserIntent
}


fun MainActivity.getUriForFile(file: File): Uri {
    return FileProvider.getUriForFile(
        this,
        "${applicationContext.packageName}.fileprovider",
        file
    )
}