package com.gahov.encrypted_notes.domain.entities

/**
 * Data class representing a note.
 *
 * @property id A unique identifier for the note. It is nullable until assigned.
 * @property message The content of the note.
 * @property isPinned A boolean, which represents if user pinned the note.
 * @property createdAt The timestamp (in epoch milliseconds) when the note was created.
 * @property updatedAt The timestamp (in epoch milliseconds) when the note was last updated.
 */

data class Note(
    var id: Long? = 1L,
    var message: String? = null,
    var isPinned: Boolean = false,
    var createdAt: Long? = null,
    var updatedAt: Long? = null,
    var deletedAt: Long? = null
)