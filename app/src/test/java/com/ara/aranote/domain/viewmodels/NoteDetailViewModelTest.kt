package com.ara.aranote.domain.viewmodels

import com.ara.aranote.domain.repository.FakeNoteRepository
import com.ara.aranote.domain.repository.NoteRepository
import com.ara.aranote.test_util.TestCoroutineRule
import com.ara.aranote.test_util.TestUtil
import com.ara.aranote.util.INVALID_NOTE_ID
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class NoteDetailViewModelTest {

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private val textModifiedNote = TestUtil.tEntity.copy(text = "modified")
    private val navigateUp = { println("navigateUp") }
    private val disableAlarm: (Int) -> Unit = { println("disableAlarm id=$it") }
    private val onOperationError = { println("onOperationError") }

    private lateinit var repository: NoteRepository
    private lateinit var systemUnderTest: NoteDetailViewModel

    @Before
    fun setUp() {
        repository = FakeNoteRepository()
        systemUnderTest = NoteDetailViewModel(repository)
    }

    @Test
    fun `prepareNote-invalidId and fresh`() = runBlockingTest {
        // act
        systemUnderTest.prepareNote(INVALID_NOTE_ID)

        // assert
        assertThat(systemUnderTest.note.value.id).isEqualTo(1)
    }

    @Test
    fun `prepareNote-invalidId and notFresh`() = runBlockingTest {
        // arrange
        repository.insertNote(TestUtil.tEntity)

        // act
        systemUnderTest.prepareNote(TestUtil.tEntity.id)

        // assert
        assertThat(systemUnderTest.note.value.id).isEqualTo(1)
    }

    @Test
    fun `prepareNote-invalidId and notFresh and idNotFound`() = runBlockingTest {
        // arrange
        repository.insertNote(TestUtil.tEntity)

        // act
        systemUnderTest.prepareNote(100)

        // assert
        assertThat(systemUnderTest.note.value.id).isEqualTo(0)
        assertThat(systemUnderTest.note.value.text).isEqualTo("ERROR")
    }

    @Test
    fun modifyNote() = runBlockingTest {
        // arrange
        repository.insertNote(TestUtil.tEntity)

        // act
        systemUnderTest.prepareNote(TestUtil.tEntity.id)
        systemUnderTest.modifyNote(textModifiedNote)

        // assert
        assertThat(systemUnderTest.note.value).isEqualTo(textModifiedNote)
    }

    @Test
    fun `backPressed-insert when isNewNote and doNotDelete`() = runBlockingTest {
        // arrange
        systemUnderTest.modifyNote(TestUtil.tEntity)

        // act
        systemUnderTest.backPressed(
            isNewNote = true,
            doesDelete = false,
            navigateUp = navigateUp,
            disableAlarm = disableAlarm,
            onOperationError = onOperationError,
        )

        // assert
        assertThat(repository.getLastId()).isEqualTo(TestUtil.tEntity.id)
    }

    @Test
    fun `backPressed-ignore when isNewNote and doDelete`() = runBlockingTest {
        // arrange
        systemUnderTest.modifyNote(TestUtil.tEntity)

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
        repository.insertNote(TestUtil.tEntity)
        systemUnderTest.prepareNote(TestUtil.tEntity.id)

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
        repository.insertNote(TestUtil.tEntity)
        systemUnderTest.prepareNote(TestUtil.tEntity.id)
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
        assertThat(repository.getNote(TestUtil.tEntity.id)?.text).isEqualTo(textModifiedNote.text)
    }
}
