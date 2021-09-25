package com.ara.aranote.domain.viewmodels

import com.ara.aranote.domain.repository.FakeNoteRepository
import com.ara.aranote.domain.repository.NoteRepository
import com.ara.aranote.test_util.TestCoroutineRule
import com.ara.aranote.test_util.TestUtil
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class HomeViewModelTest {

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private lateinit var repository: NoteRepository
    private lateinit var systemUnderTest: HomeViewModel

    @Before
    fun setUp() {
        repository = FakeNoteRepository()
        systemUnderTest = HomeViewModel(repository)
    }

    @Test
    fun observeNotes() = runBlockingTest {
        // arrange
        repository.insertNote(TestUtil.tNoteEntity)

        // act
        val r = systemUnderTest.notes.value

        // assert
        assertThat(r).containsExactly(TestUtil.tNoteEntity).inOrder()
    }
}
