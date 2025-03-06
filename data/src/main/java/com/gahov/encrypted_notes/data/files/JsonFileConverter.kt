package com.gahov.encrypted_notes.data.files

import android.content.Context
import com.gahov.encrypted_notes.data.common.FileConverter
import com.gahov.encrypted_notes.data.storage.entities.NoteDTO
import kotlinx.serialization.json.Json
import java.io.File

class JsonFileConverter(private val context: Context) : FileConverter<NoteDTO> {

    companion object {
        private const val FILE_NAME = "notes.json"
    }

    private val json = Json {
        prettyPrint = true
        encodeDefaults = true
    }

    /**
     * Converts a list of Note objects to a JSON file.
     * The JSON representation is saved to a file named "notes.json" in the cache directory.
     *
     * @param modelList the list of Note objects to be converted.
     * @return the File containing the JSON representation.
     */
    override fun toJson(modelList: List<NoteDTO>?): File {
        val list = modelList ?: emptyList()
        val exportJson = json.encodeToString(list)
        val exportFile = File(context.cacheDir, FILE_NAME)
        exportFile.writeText(exportJson)
        return exportFile
    }

    /**
     * Reads a JSON file and converts its content into a list of Note objects.
     *
     * @param file the File containing the JSON representation.
     * @return the list of Note objects parsed from the JSON content.
     */
    override fun fromJson(file: File): List<NoteDTO> {
        val jsonString = file.readText()
        return json.decodeFromString<List<NoteDTO>>(jsonString)
    }
}