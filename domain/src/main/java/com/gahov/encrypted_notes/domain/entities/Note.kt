package com.gahov.encrypted_notes.domain.entities

import kotlinx.serialization.Serializable

/**
 * Data class representing a note.
 *
 * @property id A unique identifier for the note. It is nullable until assigned.
 * @property message The content of the note.
 * @property isPinned A boolean, which represents if user pinned the note.
 * @property createdAt The timestamp (in epoch milliseconds) when the note was created.
 * @property deletedAt The timestamp (in epoch milliseconds) when the note should be deleted.
 */

@Serializable
data class Note(
    var id: Long? = 1L,
    var message: String? = null,
    var isPinned: Boolean = false,
    var createdAt: Long? = null,
    var deletedAt: Long? = null
)