package com.ara.aranote.domain.viewmodels

import com.ara.aranote.data.datastore.AppDataStore
import com.ara.aranote.domain.repository.FakeNoteRepository
import com.ara.aranote.domain.repository.NoteRepository
import com.ara.aranote.test_util.TestCoroutineRule
import com.ara.aranote.test_util.TestUtil
import com.ara.aranote.util.DEFAULT_NOTEBOOK_ID
import com.ara.aranote.util.INVALID_NOTE_ID
import com.google.common.truth.Truth.assertThat
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
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
    private lateinit var repository: NoteRepository
    private lateinit var systemUnderTest: NoteDetailViewModel

    @Before
    fun setUp() {
        repository = FakeNoteRepository()
        systemUnderTest = NoteDetailViewModel(repository, appDataStoreMock)
    }

    @Test
    fun `prepareNote-invalidId and fresh`() = runBlockingTest {
        // act
        systemUnderTest.prepareNote(noteId = INVALID_NOTE_ID, notebookId = DEFAULT_NOTEBOOK_ID)

        // assert
        assertThat(systemUnderTest.note.value.id).isEqualTo(1)
        assertThat(systemUnderTest.note.value.notebookId).isEqualTo(DEFAULT_NOTEBOOK_ID)
    }

    @Test
    fun `prepareNote-invalidId and notFresh`() = runBlockingTest {
        // arrange
        repository.insertNote(TestUtil.tNoteEntity)

        // act
        systemUnderTest.prepareNote(
            noteId = TestUtil.tNoteEntity.id,
            notebookId = TestUtil.tNoteEntity.notebookId + 1
        )

        // assert
        assertThat(systemUnderTest.note.value.id).isEqualTo(1)
        assertThat(systemUnderTest.note.value.notebookId).isEqualTo(TestUtil.tNoteEntity.notebookId)
    }

    @Test
    fun `prepareNote-invalidId and notFresh and idNotFound`() = runBlockingTest {
        // arrange
        repository.insertNote(TestUtil.tNoteEntity)

        // act
        systemUnderTest.prepareNote(noteId = 100, notebookId = DEFAULT_NOTEBOOK_ID)

        // assert
        assertThat(systemUnderTest.note.value.id).isEqualTo(0)
        assertThat(systemUnderTest.note.value.text).isEqualTo("ERROR")
        assertThat(systemUnderTest.note.value.notebookId).isEqualTo(DEFAULT_NOTEBOOK_ID)
    }

    @Test
    fun modifyNote() = runBlockingTest {
        // arrange
        repository.insertNote(TestUtil.tNoteEntity)
        systemUnderTest.prepareNote(
            noteId = TestUtil.tNoteEntity.id,
            notebookId = DEFAULT_NOTEBOOK_ID
        )

        // act
        systemUnderTest.modifyNote(textModifiedNote)

        // assert
        assertThat(systemUnderTest.note.value).isEqualTo(textModifiedNote)
    }

    @Test
    fun `backPressed-insert when isNewNote and doNotDelete`() = runBlockingTest {
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
    fun `backPressed-ignore when isNewNote and doDelete`() = runBlockingTest {
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
    fun `backPressed-delete when isNotNewNote and doDelete`() = runBlockingTest {
        // arrange
        repository.insertNote(TestUtil.tNoteEntity)
        systemUnderTest.prepareNote(
            noteId = TestUtil.tNoteEntity.id,
            notebookId = DEFAULT_NOTEBOOK_ID
        )

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
    fun `backPressed-update when isNotNewNote and doNotDelete`() = runBlockingTest {
        // arrange
        repository.insertNote(TestUtil.tNoteEntity)
        systemUnderTest.prepareNote(
            noteId = TestUtil.tNoteEntity.id,
            notebookId = DEFAULT_NOTEBOOK_ID
        )
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
        assertThat(repository.getNote(TestUtil.tNoteEntity.id)?.text).isEqualTo(textModifiedNote.text)
    }
}
