package com.ara.aranote.domain.viewmodels

import androidx.lifecycle.SavedStateHandle
import com.ara.aranote.data.datastore.AppDataStore
import com.ara.aranote.domain.repository.FakeNoteRepository
import com.ara.aranote.domain.repository.NoteRepository
import com.ara.aranote.test_util.TestCoroutineRule
import com.ara.aranote.test_util.TestUtil
import com.ara.aranote.util.DEFAULT_NOTEBOOK_ID
import com.ara.aranote.util.INVALID_NOTE_ID
import com.ara.aranote.util.NAV_ARGUMENT_NOTEBOOK_ID
import com.ara.aranote.util.NAV_ARGUMENT_NOTE_ID
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class NoteDetailViewModelTest {

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private val textModifiedNote = TestUtil.tNoteEntity.copy(text = "modified")

    private val navigateUp = { println("navigateUp") }
    private val disableAlarm: (Int) -> Unit = { println("disableAlarm id=$it") }
    private val onOperationError = { println("onOperationError") }

    private val appDataStoreMock = mockk<AppDataStore>()
    private val savedStateHandleMock = mockk<SavedStateHandle>() {
        every { get<Int>(NAV_ARGUMENT_NOTE_ID) } returns INVALID_NOTE_ID
        every { get<Int>(NAV_ARGUMENT_NOTEBOOK_ID) } returns DEFAULT_NOTEBOOK_ID
    }
    private lateinit var repository: NoteRepository
    private lateinit var systemUnderTest: NoteDetailViewModel

    @Before
    fun setUp() {
        repository = FakeNoteRepository()
        systemUnderTest = NoteDetailViewModel(repository, appDataStoreMock, savedStateHandleMock)
    }

    @Test
    fun `prepareNote-invalidId and fresh`() = runTest {
        // act
        systemUnderTest.prepareNote(noteId = INVALID_NOTE_ID)

        // assert
        assertThat(systemUnderTest.note.value.id).isEqualTo(1)
        assertThat(systemUnderTest.note.value.notebookId).isEqualTo(DEFAULT_NOTEBOOK_ID)
    }

    @Test
    fun `prepareNote-invalidId and notFresh`() = runTest {
        // arrange
        repository.insert(TestUtil.tNoteEntity)

        // act
        systemUnderTest.prepareNote(noteId = TestUtil.tNoteEntity.id)

        // assert
        assertThat(systemUnderTest.note.value.id).isEqualTo(1)
        assertThat(systemUnderTest.note.value.notebookId).isEqualTo(TestUtil.tNoteEntity.notebookId)
    }

    @Test
    fun `prepareNote-invalidId and notFresh and idNotFound`() = runTest {
        // arrange
        repository.insert(TestUtil.tNoteEntity)

        // act
        systemUnderTest.prepareNote(noteId = 100)

        // assert
        assertThat(systemUnderTest.note.value.id).isEqualTo(1)
        assertThat(systemUnderTest.note.value.text).isEqualTo("ERROR")
        assertThat(systemUnderTest.note.value.notebookId).isEqualTo(DEFAULT_NOTEBOOK_ID)
    }

    @Test
    fun modifyNote() = runTest {
        // arrange
        repository.insert(TestUtil.tNoteEntity)
        systemUnderTest.prepareNote(noteId = TestUtil.tNoteEntity.id)

        // act
        systemUnderTest.modifyNote(textModifiedNote)

        // assert
        assertThat(systemUnderTest.note.value).isEqualTo(textModifiedNote)
    }

    @Test
    fun `backPressed-insert when isNewNote and doNotDelete`() = runTest {
        // arrange
        systemUnderTest.prepareNote(noteId = INVALID_NOTE_ID)
        systemUnderTest.modifyNote(TestUtil.tNoteEntity)

        // act
        systemUnderTest.backPressed(
            isNewNote = true,
            doesDelete = false,
            navigateUp = navigateUp,
            disableAlarm = disableAlarm,
            onOperationError = onOperationError,
        )

        // assert
        assertThat(repository.getLastId()).isEqualTo(TestUtil.tNoteEntity.id)
    }

    @Test
    fun `backPressed-ignore when isNewNote and doDelete`() = runTest {
        // arrange
        systemUnderTest.prepareNote(noteId = INVALID_NOTE_ID)
        systemUnderTest.modifyNote(TestUtil.tNoteEntity)

        // act
        systemUnderTest.backPressed(
            isNewNote = true,
            doesDelete = true,
            navigateUp = navigateUp,
            disableAlarm = disableAlarm,
            onOperationError = onOperationError,
        )

        // assert
        assertThat(repository.getLastId()).isEqualTo(0)
    }

    @Test
    fun `backPressed-delete when isNotNewNote and doDelete`() = runTest {
        // arrange
        repository.insert(TestUtil.tNoteEntity)
        systemUnderTest.prepareNote(noteId = TestUtil.tNoteEntity.id)

        // act
        systemUnderTest.backPressed(
            isNewNote = false,
            doesDelete = true,
            navigateUp = navigateUp,
            disableAlarm = disableAlarm,
            onOperationError = onOperationError,
        )

        // assert
        assertThat(repository.getLastId()).isEqualTo(0)
    }

    @Test
    fun `backPressed-update when isNotNewNote and doNotDelete`() = runTest {
        // arrange
        repository.insert(TestUtil.tNoteEntity)
        systemUnderTest.prepareNote(noteId = TestUtil.tNoteEntity.id)
        systemUnderTest.modifyNote(textModifiedNote)

        // act
        systemUnderTest.backPressed(
            isNewNote = false,
            doesDelete = false,
            navigateUp = navigateUp,
            disableAlarm = disableAlarm,
            onOperationError = onOperationError,
        )

        // assert
        assertThat(repository.getLastId()).isEqualTo(textModifiedNote.id)
        assertThat(repository.getById(TestUtil.tNoteEntity.id)?.text).isEqualTo(textModifiedNote.text)
    }
}
