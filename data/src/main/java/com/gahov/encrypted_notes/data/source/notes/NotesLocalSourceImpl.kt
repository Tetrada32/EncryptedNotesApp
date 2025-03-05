package com.gahov.encrypted_notes.data.source.notes

import com.gahov.encrypted_notes.data.storage.storage.NotesDao

class NotesLocalSourceImpl(private val notesDao: NotesDao) : NotesLocalSource {
}