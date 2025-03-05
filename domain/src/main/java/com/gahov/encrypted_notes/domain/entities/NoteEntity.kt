package com.gahov.encrypted_notes.domain.entities

data class NoteEntity(
    val id: Long? = 1L,
    val title: String? = null,
    val content: String? = null,
    val isPinned: Boolean = false
)