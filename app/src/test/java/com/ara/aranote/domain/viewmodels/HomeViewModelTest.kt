package com.ara.aranote.domain.viewmodels

import com.ara.aranote.data.datastore.AppDataStore
import com.ara.aranote.domain.repository.FakeNoteRepository
import com.ara.aranote.domain.repository.NoteRepository
import com.ara.aranote.test_util.TestCoroutineRule
import com.ara.aranote.test_util.TestUtil
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class HomeViewModelTest {

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private val appDataStoreMock = mockk<AppDataStore>() {
        coEvery { readPref(AppDataStore.DEFAULT_NOTEBOOK_EXISTENCE_KEY, any()) } returns true
    }
    private lateinit var repository: NoteRepository
    private lateinit var systemUnderTest: HomeViewModel

    @Before
    fun setUp() {
        repository = FakeNoteRepository()
        systemUnderTest = HomeViewModel(repository, appDataStoreMock)
    }

    @Test
    fun observeNotes() = runTest {
        // arrange
        repository.insertNote(TestUtil.tNoteEntity)

        // act
        val r = systemUnderTest.notes.value

        // assert
        assertThat(r).containsExactly(TestUtil.tNoteEntity).inOrder()
    }

//    @Test
//    fun addNotebook() = runTest {
//        // act
//        systemUnderTest.addNotebook(
//            id = TestUtil.tNotebookEntity.id,
//            name = TestUtil.tNotebookEntity.name
//        )
//        val r = repository.observeNotebooks().first()
//
//        // assert
//        assertThat(r).containsExactly(TestUtil.tNotebookEntity).inOrder()
//    }

    @Test
    fun setCurrentNotebookId() = runTest {
        // arrange
        repository.insertNote(TestUtil.tNoteEntity)
        repository.insertNote(TestUtil.tNoteEntity2)

        // act
        systemUnderTest.setCurrentNotebookId(TestUtil.tNoteEntity2.notebookId)
        val r = systemUnderTest.notes.value

        // assert
        assertThat(r).containsExactly(TestUtil.tNoteEntity2).inOrder()
    }
}
