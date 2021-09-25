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

    private val tEntityList = listOf(TestUtil.tEntity)
    private val tModelList = listOf(TestUtil.tModel)

    private val noteDaoMock = mockk<NoteDao>()
    private val noteDomainMapperMock = mockk<DomainMapper<NoteModel, Note>>() {
        every { mapToDomainEntity(TestUtil.tModel) } returns TestUtil.tEntity
        every { mapFromDomainEntity(TestUtil.tEntity) } returns TestUtil.tModel
        every { toDomainList(tModelList) } returns tEntityList
        every { fromDomainList(tEntityList) } returns tModelList
    }
    private val notebookDaoMock = mockk<NotebookDao>()
    private val notebookDomainMapperMock = mockk<DomainMapper<NotebookModel, Notebook>>() {
//        every { mapToDomainEntity(TestUtil.tModel) } returns TestUtil.tEntity
//        every { mapFromDomainEntity(TestUtil.tEntity) } returns TestUtil.tModel
//        every { toDomainList(tModelList) } returns tEntityList
//        every { fromDomainList(tEntityList) } returns tModelList
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
        coEvery { noteDaoMock.insertNote(TestUtil.tModel) } returns 1

        // act
        val r = systemUnderTest.insertNote(TestUtil.tEntity)

        // assert
        coVerify { noteDomainMapperMock.mapFromDomainEntity(TestUtil.tEntity) }
        coVerify { noteDaoMock.insertNote(TestUtil.tModel) }
        assertThat(r).isEqualTo(1)
    }

    @Test
    fun insertNote_onDbError() = runBlockingTest {
        // arrange
        coEvery { noteDaoMock.insertNote(TestUtil.tModel) } returns null

        // act
        val r = systemUnderTest.insertNote(TestUtil.tEntity)

        // assert
        coVerify { noteDomainMapperMock.mapFromDomainEntity(TestUtil.tEntity) }
        coVerify { noteDaoMock.insertNote(TestUtil.tModel) }
        assertThat(r).isEqualTo(INVALID_NOTE_ID)
    }

    @Test
    fun observeNotes() = runBlockingTest {
        // arrange
        every { noteDaoMock.observeNotes() } returns flowOf(tModelList, tModelList)

        // act
        val r = systemUnderTest.observeNotes()
        val r2 = r.toList()

        // assert
        verify { noteDomainMapperMock.toDomainList(tModelList) }
        verify { noteDaoMock.observeNotes() }
        assertThat(r2).containsExactly(tEntityList, tEntityList).inOrder()
    }

    @Test
    fun getNote_onDbSuccessful() = runBlockingTest {
        // arrange
        coEvery { noteDaoMock.getNote(1) } returns TestUtil.tModel

        // act
        val r = systemUnderTest.getNote(1)

        // assert
        coVerify { noteDomainMapperMock.mapToDomainEntity(TestUtil.tModel) }
        coVerify { noteDaoMock.getNote(1) }
        assertThat(r).isEqualTo(TestUtil.tEntity)
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
        coEvery { noteDaoMock.updateNote(TestUtil.tModel) } returns 1

        // act
        val r = systemUnderTest.updateNote(TestUtil.tEntity)

        // assert
        coVerify { noteDomainMapperMock.mapFromDomainEntity(TestUtil.tEntity) }
        coVerify { noteDaoMock.updateNote(TestUtil.tModel) }
        assertThat(r).isTrue()
    }

    @Test
    fun updateNote_onDbError() = runBlockingTest {
        // arrange
        coEvery { noteDaoMock.updateNote(TestUtil.tModel) } returns null

        // act
        val r = systemUnderTest.updateNote(TestUtil.tEntity)

        // assert
        coVerify { noteDomainMapperMock.mapFromDomainEntity(TestUtil.tEntity) }
        coVerify { noteDaoMock.updateNote(TestUtil.tModel) }
        assertThat(r).isFalse()
    }

    @Test
    fun deleteNote_onDbSuccessful() = runBlockingTest {
        // arrange
        coEvery { noteDaoMock.deleteNote(TestUtil.tModel) } returns 1

        // act
        val r = systemUnderTest.deleteNote(TestUtil.tEntity)

        // assert
        coVerify { noteDomainMapperMock.mapFromDomainEntity(TestUtil.tEntity) }
        coEvery { noteDaoMock.deleteNote(TestUtil.tModel) }
        assertThat(r).isTrue()
    }

    @Test
    fun deleteNote_onDbError() = runBlockingTest {
        // arrange
        coEvery { noteDaoMock.deleteNote(TestUtil.tModel) } returns null

        // act
        val r = systemUnderTest.deleteNote(TestUtil.tEntity)

        // assert
        coVerify { noteDomainMapperMock.mapFromDomainEntity(TestUtil.tEntity) }
        coEvery { noteDaoMock.deleteNote(TestUtil.tModel) }
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
        coEvery { noteDaoMock.getAllNotesWithAlarm() } returns tModelList

        // act
        val r = systemUnderTest.getAllNotesWithAlarm()

        // assert
        coVerify { noteDomainMapperMock.toDomainList(tModelList) }
        coVerify { noteDaoMock.getAllNotesWithAlarm() }
        assertThat(r).containsExactly(TestUtil.tEntity).inOrder()
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
