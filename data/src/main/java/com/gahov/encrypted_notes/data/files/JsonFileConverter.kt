package com.gahov.encrypted_notes.data.files

import android.content.Context
import com.gahov.encrypted_notes.data.common.FileConverter
import com.gahov.encrypted_notes.domain.entities.Note
import kotlinx.serialization.json.Json
import java.io.File

class JsonFileConverter(private val context: Context) : FileConverter<Note> {

    private val json = Json { prettyPrint = true }

    /**
     * Converts a Note object to a JSON file.
     * The JSON representation is saved to a file named "note.json" in the cache directory.
     *
     * @param model the Note object to be converted.
     * @return the File containing the JSON representation.
     */
    override fun toJson(model: Note): File {
        val exportJson = json.encodeToString(model)
        val exportFile = File(context.cacheDir, "note.json")
        exportFile.writeText(exportJson)
        return exportFile
    }

    /**
     * Converts a list of Note objects to a JSON file.
     * The JSON representation is saved to a file named "notes.json" in the cache directory.
     *
     * @param modelList the list of Note objects to be converted.
     * @return the File containing the JSON representation.
     */
    override fun toJson(modelList: List<Note>?): File {
        val list = modelList ?: emptyList()
        val exportJson = json.encodeToString(list)
        val exportFile = File(context.cacheDir, "notes.json")
        exportFile.writeText(exportJson)
        return exportFile
    }

    /**
     * Reads a JSON file and converts its content into a Note object.
     *
     * @param file the File containing the JSON representation.
     * @return the Note object parsed from the JSON content.
     */
    override fun fromJson(file: File): Note {
        val jsonString = file.readText()
        return json.decodeFromString(jsonString)
    }

    /**
     * Reads a JSON file and converts its content into a list of Note objects.
     *
     * @param file the File containing the JSON representation.
     * @return the list of Note objects parsed from the JSON content.
     */
    override fun fromJsonList(file: File): List<Note> {
        val jsonString = file.readText()
        return json.decodeFromString(jsonString)
    }
}