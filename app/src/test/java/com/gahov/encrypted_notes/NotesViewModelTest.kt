package com.gahov.encrypted_notes

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.gahov.encrypted_notes.domain.common.Either
import com.gahov.encrypted_notes.domain.entities.Note
import com.gahov.encrypted_notes.domain.repository.NotesRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NotesViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: NotesViewModel
    private var notesRepository: NotesRepository = mockk()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = NotesViewModel(notesRepository)
        viewModel.exportImportCallback
    }

    @Test
    fun `onStart fetches notes successfully`() = runTest {
        // Arrange
        val note = mockk<Note> {
            every { deletedAt } returns null
            every { createdAt } returns 1000L
        }
        val notes = listOf(note)

        coEvery { notesRepository.fetchAllNotes() } returns flowOf(Either.Right(notes))

        // Act
        viewModel.onStart()
        advanceUntilIdle()

        // Assert
        coVerify(exactly = 1) { notesRepository.fetchAllNotes() }
        assertEquals(notes, viewModel.notesListAsStateFlow.value)
    }

    @Test
    fun `onSearchInputChanged returns full list when input is blank`() = runTest {
        // Arrange
        val note = mockk<Note> {
            every { message } returns "title"
            every { deletedAt } returns null
            every { createdAt } returns 1000L
            every { isPinned } returns false
        }
        val note2 = mockk<Note> {
            every { message } returns "message"
            every { deletedAt } returns null
            every { createdAt } returns 1100L
            every { isPinned } returns false
        }
        val notes = listOf(note, note2)

        coEvery { notesRepository.fetchAllNotes() } returns flowOf(Either.Right(notes))

        // Act
        viewModel.onStart()
        advanceUntilIdle()

        viewModel.onSearchInputChanged("")

        assertEquals(notes, viewModel.notesListAsStateFlow.value)
    }

    @Test
    fun `onSearchInputChanged filters notes by message`() = runTest {
        // Prepare test notes
        val note1 = Note(id = 1, message = "Kotlin is awesome", isPinned = false, createdAt = 1000L)
        val note2 = Note(id = 2, message = "Android development", isPinned = true, createdAt = 2000L)
        val note3 = Note(id = 3, message = "Kotlin coroutines", isPinned = false, createdAt = 3000L)
        val notes = listOf(note1, note2, note3)

        coEvery { notesRepository.fetchAllNotes() } returns flowOf(Either.Right(notes))

        viewModel.onStart()
        advanceUntilIdle()

        // Filter by the word "kotlin" (case insensitive)
        viewModel.onSearchInputChanged("kOtLiN")

        // Expect note1 and note3 to be found
        val expected = listOf(note1, note3).sortedWith(
            compareByDescending<Note> { it.isPinned }
                .thenBy { it.createdAt }
        )
        assertEquals(expected, viewModel.notesListAsStateFlow.value)
    }

    @Test
    fun `addNote calls repository addNote`() = runTest {
        // Assert
        val newNote = mockk<Note>()
        coEvery { notesRepository.addNote(newNote) } returns Either.Right(Unit)

        // Act
        viewModel.addNote(newNote)
        advanceUntilIdle()

        // Verify
        coVerify(exactly = 1) { notesRepository.addNote(newNote) }
    }

    @Test
    fun `updateNote calls repository updateNote`() = runTest {
        // Arrange
        val noteToUpdate = mockk<Note> {
            every { id } returns 121L
            every { message } returns "title"
            every { deletedAt } returns Long.MAX_VALUE
            every { createdAt } returns 1000L
            every { isPinned } returns true
        }
        // Use matchers to ensure the stub matches exactly.
        coEvery {
            notesRepository.updateNote(
                eq(121L),
                eq("title"),
                eq(true),
                eq(Long.MAX_VALUE)
            )
        } returns Either.Right(Unit)

        // Act
        viewModel.updateNote(noteToUpdate)
        advanceUntilIdle()

        // Verify the call occurred once.
        coVerify(exactly = 1) {
            notesRepository.updateNote(
                eq(121L),
                eq("title"),
                eq(true),
                eq(Long.MAX_VALUE)
            )
        }
    }

    @Test
    fun `deleteNote calls repository deleteNote`() = runTest {
        // Assert
        val noteId = 1L
        val noteToDelete = mockk<Note> {
            every { id } returns 1L
        }
        coEvery { notesRepository.deleteNote(eq(noteId)) } returns Either.Right(Unit)

        // Act
        viewModel.deleteNote(noteToDelete)
        advanceUntilIdle()

        // Verify
        coVerify(exactly = 1) { notesRepository.deleteNote(eq(noteId)) }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}
