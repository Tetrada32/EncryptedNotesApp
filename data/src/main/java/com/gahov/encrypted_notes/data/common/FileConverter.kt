package com.gahov.encrypted_notes.data.common

import java.io.File

/**
 * An interface for converting a data model to and from JSON files.
 *
 * @param T the type of the data model.
 */
interface FileConverter<T> {


    /**
     * Converts a list of data models into their JSON representation and writes it to a file.
     *
     * @param modelList the list of data models to be converted.
     * @return a File containing the JSON representation of the list of models.
     */
    fun toJson(modelList: List<T>?): File

    /**
     * Reads a JSON file and converts its content into a list of data models.
     *
     * @param file the File containing the JSON representation.
     * @return a list of data models represented by the JSON content.
     */
    fun fromJson(file: File): List<T>
}
