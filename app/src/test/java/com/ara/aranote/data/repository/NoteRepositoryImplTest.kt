package com.ara.aranote.data.repository

import com.ara.aranote.data.local_data_source.NoteDao
import com.ara.aranote.data.local_data_source.NotebookDao
import com.ara.aranote.data.model.NoteModel
import com.ara.aranote.data.model.NotebookModel
import com.ara.aranote.domain.entity.Note
import com.ara.aranote.domain.entity.Notebook
import com.ara.aranote.domain.util.DomainMapper
import com.ara.aranote.test_util.TestUtil
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
import kotlinx.coroutines.test.runBlockingTest
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
    fun insertNote_onDbSuccessful() = runBlockingTest {
        // arrange
        coEvery { noteDaoMock.insertNote(TestUtil.tNoteModel) } returns 1

        // act
        val r = systemUnderTest.insertNote(TestUtil.tNoteEntity)

        // assert
        coVerify { noteDomainMapperMock.mapFromDomainEntity(TestUtil.tNoteEntity) }
        coVerify { noteDaoMock.insertNote(TestUtil.tNoteModel) }
        assertThat(r).isEqualTo(1)
    }

    @Test
    fun insertNote_onDbError() = runBlockingTest {
        // arrange
        coEvery { noteDaoMock.insertNote(TestUtil.tNoteModel) } returns null

        // act
        val r = systemUnderTest.insertNote(TestUtil.tNoteEntity)

        // assert
        coVerify { noteDomainMapperMock.mapFromDomainEntity(TestUtil.tNoteEntity) }
        coVerify { noteDaoMock.insertNote(TestUtil.tNoteModel) }
        assertThat(r).isEqualTo(INVALID_NOTE_ID)
    }

    @Test
    fun observeNotes() = runBlockingTest {
        // arrange
        every { noteDaoMock.observeNotes() } returns flowOf(
            TestUtil.tNoteModelList,
            TestUtil.tNoteModelList
        )

        // act
        val r = systemUnderTest.observeNotes()
        val r2 = r.toList()

        // assert
        verify { noteDomainMapperMock.toDomainList(TestUtil.tNoteModelList) }
        verify { noteDaoMock.observeNotes() }
        assertThat(r2).containsExactly(TestUtil.tNoteEntityList, TestUtil.tNoteEntityList).inOrder()
    }

    @Test
    fun getNote_onDbSuccessful() = runBlockingTest {
        // arrange
        coEvery { noteDaoMock.getNote(1) } returns TestUtil.tNoteModel

        // act
        val r = systemUnderTest.getNote(1)

        // assert
        coVerify { noteDomainMapperMock.mapToDomainEntity(TestUtil.tNoteModel) }
        coVerify { noteDaoMock.getNote(1) }
        assertThat(r).isEqualTo(TestUtil.tNoteEntity)
    }

    @Test
    fun getNote_onDbError() = runBlockingTest {
        // arrange
        coEvery { noteDaoMock.getNote(1) } returns null

        // act
        val r = systemUnderTest.getNote(1)

        // assert
        coVerify { noteDomainMapperMock wasNot Called }
        coVerify { noteDaoMock.getNote(1) }
        assertThat(r).isEqualTo(null)
    }

    @Test
    fun updateNote_onDbSuccessful() = runBlockingTest {
        // arrange
        coEvery { noteDaoMock.updateNote(TestUtil.tNoteModel) } returns 1

        // act
        val r = systemUnderTest.updateNote(TestUtil.tNoteEntity)

        // assert
        coVerify { noteDomainMapperMock.mapFromDomainEntity(TestUtil.tNoteEntity) }
        coVerify { noteDaoMock.updateNote(TestUtil.tNoteModel) }
        assertThat(r).isTrue()
    }

    @Test
    fun updateNote_onDbError() = runBlockingTest {
        // arrange
        coEvery { noteDaoMock.updateNote(TestUtil.tNoteModel) } returns null

        // act
        val r = systemUnderTest.updateNote(TestUtil.tNoteEntity)

        // assert
        coVerify { noteDomainMapperMock.mapFromDomainEntity(TestUtil.tNoteEntity) }
        coVerify { noteDaoMock.updateNote(TestUtil.tNoteModel) }
        assertThat(r).isFalse()
    }

    @Test
    fun deleteNote_onDbSuccessful() = runBlockingTest {
        // arrange
        coEvery { noteDaoMock.deleteNote(TestUtil.tNoteModel) } returns 1

        // act
        val r = systemUnderTest.deleteNote(TestUtil.tNoteEntity)

        // assert
        coVerify { noteDomainMapperMock.mapFromDomainEntity(TestUtil.tNoteEntity) }
        coEvery { noteDaoMock.deleteNote(TestUtil.tNoteModel) }
        assertThat(r).isTrue()
    }

    @Test
    fun deleteNote_onDbError() = runBlockingTest {
        // arrange
        coEvery { noteDaoMock.deleteNote(TestUtil.tNoteModel) } returns null

        // act
        val r = systemUnderTest.deleteNote(TestUtil.tNoteEntity)

        // assert
        coVerify { noteDomainMapperMock.mapFromDomainEntity(TestUtil.tNoteEntity) }
        coEvery { noteDaoMock.deleteNote(TestUtil.tNoteModel) }
        assertThat(r).isFalse()
    }

    @Test
    fun getLastId_onDbSuccessful() = runBlockingTest {
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
    fun getLastId_onDbError() = runBlockingTest {
        // arrange
        coEvery { noteDaoMock.getLastId() } returns null

        // act
        val r = systemUnderTest.getLastId()

        // assert
        coVerify { noteDomainMapperMock wasNot Called }
        coVerify { noteDaoMock.getLastId() }
        assertThat(r).isEqualTo(INVALID_NOTE_ID)
    }

    @Test
    fun getAllNotesWithAlarm_onDbSuccessful() = runBlockingTest {
        // arrange
        coEvery { noteDaoMock.getAllNotesWithAlarm() } returns TestUtil.tNoteModelList

        // act
        val r = systemUnderTest.getAllNotesWithAlarm()

        // assert
        coVerify { noteDomainMapperMock.toDomainList(TestUtil.tNoteModelList) }
        coVerify { noteDaoMock.getAllNotesWithAlarm() }
        assertThat(r).containsExactly(TestUtil.tNoteEntity).inOrder()
    }

    @Test
    fun getAllNotesWithAlarm_onDbError() = runBlockingTest {
        // arrange
        coEvery { noteDaoMock.getAllNotesWithAlarm() } returns null

        // act
        val r = systemUnderTest.getAllNotesWithAlarm()

        // assert
        coVerify { noteDomainMapperMock wasNot Called }
        coVerify { noteDaoMock.getAllNotesWithAlarm() }
        assertThat(r).isEmpty()
    }
}
