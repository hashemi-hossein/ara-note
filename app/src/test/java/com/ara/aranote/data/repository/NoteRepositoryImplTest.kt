package com.ara.aranote.data.repository

import com.ara.aranote.data.local_data_source.NoteDao
import com.ara.aranote.data.local_data_source.NotebookDao
import com.ara.aranote.data.model.NoteModel
import com.ara.aranote.data.model.NotebookModel
import com.ara.aranote.domain.entity.Note
import com.ara.aranote.domain.entity.Notebook
import com.ara.aranote.domain.util.DomainMapper
import com.ara.aranote.test_util.TestUtil
import com.ara.aranote.util.INVALID_NOTEBOOK_ID
import com.ara.aranote.util.INVALID_NOTE_ID
import com.google.common.truth.Truth.assertThat
import io.mockk.Called
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Test

@ExperimentalCoroutinesApi
class NoteRepositoryImplTest {

    private val noteDaoMock = mockk<NoteDao>()
    private val noteDomainMapperMock = mockk<DomainMapper<NoteModel, Note>>() {
        every { mapToDomainEntity(TestUtil.tNoteModel) } returns TestUtil.tNoteEntity
        every { mapFromDomainEntity(TestUtil.tNoteEntity) } returns TestUtil.tNoteModel
        every { toDomainList(TestUtil.tNoteModelList) } returns TestUtil.tNoteEntityList
        every { fromDomainList(TestUtil.tNoteEntityList) } returns TestUtil.tNoteModelList
    }
    private val notebookDaoMock = mockk<NotebookDao>()
    private val notebookDomainMapperMock = mockk<DomainMapper<NotebookModel, Notebook>>() {
        every { mapToDomainEntity(TestUtil.tNotebookModel) } returns TestUtil.tNotebookEntity
        every { mapFromDomainEntity(TestUtil.tNotebookEntity) } returns TestUtil.tNotebookModel
        every { toDomainList(TestUtil.tNotebookModelList) } returns TestUtil.tNotebookEntityList
        every { fromDomainList(TestUtil.tNotebookEntityList) } returns TestUtil.tNotebookModelList
    }

    private val systemUnderTest = NoteRepositoryImpl(
        noteDao = noteDaoMock,
        noteDomainMapper = noteDomainMapperMock,
        notebookDao = notebookDaoMock,
        notebookDomainMapper = notebookDomainMapperMock,
    )

    @Test
    fun insertNote_onDbSuccessful() = runTest {
        // arrange
        coEvery { noteDaoMock.insert(TestUtil.tNoteModel) } returns 1

        // act
        val r = systemUnderTest.insert(TestUtil.tNoteEntity)

        // assert
        coVerify { noteDomainMapperMock.mapFromDomainEntity(TestUtil.tNoteEntity) }
        coVerify { noteDaoMock.insert(TestUtil.tNoteModel) }
        assertThat(r).isEqualTo(1)
    }

    @Test
    fun insertNote_onDbError() = runTest {
        // arrange
        coEvery { noteDaoMock.insert(TestUtil.tNoteModel) } returns null

        // act
        val r = systemUnderTest.insert(TestUtil.tNoteEntity)

        // assert
        coVerify { noteDomainMapperMock.mapFromDomainEntity(TestUtil.tNoteEntity) }
        coVerify { noteDaoMock.insert(TestUtil.tNoteModel) }
        assertThat(r).isEqualTo(INVALID_NOTE_ID)
    }

    @Test
    fun observeNotes() = runTest {
        // arrange
        every { noteDaoMock.observe(any()) } returns flowOf(
            TestUtil.tNoteModelList,
            TestUtil.tNoteModelList
        )
        val reorderedList = TestUtil.tNoteEntityList.sortedByDescending { it.addedDateTime }

        // act
        val r = systemUnderTest.observe(0)
        val r2 = r.toList()

        // assert
        verify { noteDomainMapperMock.toDomainList(TestUtil.tNoteModelList) }
        verify { noteDaoMock.observe(any()) }
        assertThat(r2).containsExactly(reorderedList, reorderedList).inOrder()
    }

    @Test
    fun getNote_onDbSuccessful() = runTest {
        // arrange
        coEvery { noteDaoMock.getById(1) } returns TestUtil.tNoteModel

        // act
        val r = systemUnderTest.getById(1)

        // assert
        coVerify { noteDomainMapperMock.mapToDomainEntity(TestUtil.tNoteModel) }
        coVerify { noteDaoMock.getById(1) }
        assertThat(r).isEqualTo(TestUtil.tNoteEntity)
    }

    @Test
    fun getNote_onDbError() = runTest {
        // arrange
        coEvery { noteDaoMock.getById(1) } returns null

        // act
        val r = systemUnderTest.getById(1)

        // assert
        coVerify { noteDomainMapperMock wasNot Called }
        coVerify { noteDaoMock.getById(1) }
        assertThat(r).isEqualTo(null)
    }

    @Test
    fun updateNote_onDbSuccessful() = runTest {
        // arrange
        coEvery { noteDaoMock.update(TestUtil.tNoteModel) } returns 1

        // act
        val r = systemUnderTest.update(TestUtil.tNoteEntity)

        // assert
        coVerify { noteDomainMapperMock.mapFromDomainEntity(TestUtil.tNoteEntity) }
        coVerify { noteDaoMock.update(TestUtil.tNoteModel) }
        assertThat(r).isTrue()
    }

    @Test
    fun updateNote_onDbError() = runTest {
        // arrange
        coEvery { noteDaoMock.update(TestUtil.tNoteModel) } returns null

        // act
        val r = systemUnderTest.update(TestUtil.tNoteEntity)

        // assert
        coVerify { noteDomainMapperMock.mapFromDomainEntity(TestUtil.tNoteEntity) }
        coVerify { noteDaoMock.update(TestUtil.tNoteModel) }
        assertThat(r).isFalse()
    }

    @Test
    fun deleteNote_onDbSuccessful() = runTest {
        // arrange
        coEvery { noteDaoMock.delete(TestUtil.tNoteModel) } returns 1

        // act
        val r = systemUnderTest.delete(TestUtil.tNoteEntity)

        // assert
        coVerify { noteDomainMapperMock.mapFromDomainEntity(TestUtil.tNoteEntity) }
        coEvery { noteDaoMock.delete(TestUtil.tNoteModel) }
        assertThat(r).isTrue()
    }

    @Test
    fun deleteNote_onDbError() = runTest {
        // arrange
        coEvery { noteDaoMock.delete(TestUtil.tNoteModel) } returns null

        // act
        val r = systemUnderTest.delete(TestUtil.tNoteEntity)

        // assert
        coVerify { noteDomainMapperMock.mapFromDomainEntity(TestUtil.tNoteEntity) }
        coEvery { noteDaoMock.delete(TestUtil.tNoteModel) }
        assertThat(r).isFalse()
    }

    @Test
    fun getLastId_onDbSuccessful() = runTest {
        // arrange
        coEvery { noteDaoMock.getLastId() } returns 1

        // act
        val r = systemUnderTest.getLastId()

        // assert
        coVerify { noteDomainMapperMock wasNot Called }
        coVerify { noteDaoMock.getLastId() }
        assertThat(r).isEqualTo(1)
    }

    @Test
    fun getLastId_onEmptyDb_or_onDbError() = runTest {
        // arrange
        coEvery { noteDaoMock.getLastId() } returns null

        // act
        val r = systemUnderTest.getLastId()

        // assert
        coVerify { noteDomainMapperMock wasNot Called }
        coVerify { noteDaoMock.getLastId() }
        assertThat(r).isEqualTo(0)
    }

    @Test
    fun getAllNotesWithAlarm_onDbSuccessful() = runTest {
        // arrange
        coEvery { noteDaoMock.getAllNotesWithAlarm() } returns TestUtil.tNoteModelList

        // act
        val r = systemUnderTest.getAllNotesWithAlarm()

        // assert
        coVerify { noteDomainMapperMock.toDomainList(TestUtil.tNoteModelList) }
        coVerify { noteDaoMock.getAllNotesWithAlarm() }
        assertThat(r).isEqualTo(TestUtil.tNoteEntityList)
    }

    @Test
    fun getAllNotesWithAlarm_onDbError() = runTest {
        // arrange
        coEvery { noteDaoMock.getAllNotesWithAlarm() } returns null

        // act
        val r = systemUnderTest.getAllNotesWithAlarm()

        // assert
        coVerify { noteDomainMapperMock wasNot Called }
        coVerify { noteDaoMock.getAllNotesWithAlarm() }
        assertThat(r).isEmpty()
    }

    @Test
    fun observeNotebooks() = runTest {
        // arrange
        every { notebookDaoMock.observe() } returns flowOf(
            TestUtil.tNotebookModelList,
            TestUtil.tNotebookModelList
        )

        // act
        val r = systemUnderTest.observeNotebooks()
        val r2 = r.toList()

        // assert
        verify { notebookDomainMapperMock.toDomainList(TestUtil.tNotebookModelList) }
        verify { notebookDaoMock.observe() }
        assertThat(r2).containsExactly(TestUtil.tNotebookEntityList, TestUtil.tNotebookEntityList)
            .inOrder()
    }

    @Test
    fun insertNotebook_onDbSuccessful() = runTest {
        // arrange
        coEvery { notebookDaoMock.insert(TestUtil.tNotebookModel) } returns 1

        // act
        val r = systemUnderTest.insertNotebook(TestUtil.tNotebookEntity)

        // assert
        coVerify { notebookDomainMapperMock.mapFromDomainEntity(TestUtil.tNotebookEntity) }
        coVerify { notebookDaoMock.insert(TestUtil.tNotebookModel) }
        assertThat(r).isEqualTo(1)
    }

    @Test
    fun insertNotebook_onDbError() = runTest {
        // arrange
        coEvery { notebookDaoMock.insert(TestUtil.tNotebookModel) } returns null

        // act
        val r = systemUnderTest.insertNotebook(TestUtil.tNotebookEntity)

        // assert
        coVerify { notebookDomainMapperMock.mapFromDomainEntity(TestUtil.tNotebookEntity) }
        coVerify { notebookDaoMock.insert(TestUtil.tNotebookModel) }
        assertThat(r).isEqualTo(INVALID_NOTEBOOK_ID)
    }
}
