package com.gahov.encrypted_notes.domain.entities

data class NoteEntity(
    val title: String? = null,
    val content: String? = null,
    val isPinned: Boolean = false
)