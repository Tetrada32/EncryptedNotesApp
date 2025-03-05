package com.gahov.encrypted_notes.feature

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gahov.encrypted_notes.domain.common.getOrNull
import com.gahov.encrypted_notes.domain.entities.NoteEntity
import com.gahov.encrypted_notes.domain.repository.NotesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(
    private val notesRepository: NotesRepository
) : ViewModel() {

    init {
        saveContent()
        fetchContent()
    }

    private fun saveContent() {
        viewModelScope.launch {
            notesRepository.addNote(NoteEntity(title = "First item", content = "This is my content"))
            notesRepository.addNote(NoteEntity(title = "Second item", content = "This is my content 2"))
            notesRepository.addNote(NoteEntity(title = "Third item", content = "This is my content 3"))
        }
    }

    private fun fetchContent() {
        viewModelScope.launch {
            delay(2000)
            val result = notesRepository.fetchAllNotes()
            result.collect {
                Log.d("TAG", "NotesViewModel: fetchedDataFromBD: ${it.getOrNull()}")
            }
        }
    }
}