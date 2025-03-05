package com.gahov.encrypted_notes.data.common

import java.io.File

/**
 * An interface for converting a data model to and from JSON files.
 *
 * @param T the type of the data model.
 */
interface FileConverter<T> {

    /**
     * Converts a data model into its JSON representation and writes it to a file.
     *
     * @param model the data model to be converted.
     * @return a File containing the JSON representation of the model.
     */
    fun toJson(model: T): File

    /**
     * Converts a list of data models into their JSON representation and writes it to a file.
     *
     * @param modelList the list of data models to be converted.
     * @return a File containing the JSON representation of the list of models.
     */
    fun toJson(modelList: List<T>?): File

    /**
     * Reads a JSON file and converts its content into the corresponding data model.
     *
     * @param file the File containing the JSON representation.
     * @return the data model represented by the JSON content.
     */
    fun fromJson(file: File): T

    /**
     * Reads a JSON file and converts its content into a list of data models.
     *
     * @param file the File containing the JSON representation.
     * @return a list of data models represented by the JSON content.
     */
    fun fromJsonList(file: File): List<T>
}
