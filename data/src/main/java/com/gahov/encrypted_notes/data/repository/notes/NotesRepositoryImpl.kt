package com.gahov.encrypted_notes.data.repository.notes

import com.gahov.encrypted_notes.data.mapper.NotesLocalMapper
import com.gahov.encrypted_notes.data.source.notes.NotesLocalSource
import com.gahov.encrypted_notes.domain.repository.NotesRepository

class NotesRepositoryImpl(
    private val localSource: NotesLocalSource,
    private val localMapper: NotesLocalMapper
) : NotesRepository {

}