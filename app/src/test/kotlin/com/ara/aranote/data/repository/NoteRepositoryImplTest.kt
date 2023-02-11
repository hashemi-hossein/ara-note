package com.ara.aranote.data.repository

import com.ara.aranote.data.localdatasource.NoteDao
import com.ara.aranote.data.model.NoteModel
import com.ara.aranote.domain.entity.Note
import com.ara.aranote.domain.util.Mapper
import com.ara.aranote.util.Result
import com.ara.test.TestUtil
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
    private val noteDomainMapperMock = mockk<Mapper<NoteModel, Note>>() {
        every { map(TestUtil.tNoteModel) } returns TestUtil.tNoteEntity
        every { mapReverse(TestUtil.tNoteEntity) } returns TestUtil.tNoteModel
        every { mapList(TestUtil.tNoteModelList) } returns TestUtil.tNoteEntityList
        every { mapListReverse(TestUtil.tNoteEntityList) } returns TestUtil.tNoteModelList
    }

    private val systemUnderTest = NoteRepositoryImpl(
        noteDao = noteDaoMock,
        noteDomainMapper = noteDomainMapperMock,
    )

    @Test
    fun insertNote_onDbSuccessful() = runTest {
        // arrange
        coEvery { noteDaoMock.insert(TestUtil.tNoteModel) } returns 1

        // act
        val r = systemUnderTest.insert(TestUtil.tNoteEntity)

        // assert
        coVerify { noteDomainMapperMock.mapReverse(TestUtil.tNoteEntity) }
        coVerify { noteDaoMock.insert(TestUtil.tNoteModel) }
        assertThat(r).isInstanceOf(Result.Success::class.java)
        assertThat((r as Result.Success).data).isEqualTo(1)
    }

    @Test
    fun insertNote_onDbError() = runTest {
        // arrange
        coEvery { noteDaoMock.insert(TestUtil.tNoteModel) } returns null

        // act
        val r = systemUnderTest.insert(TestUtil.tNoteEntity)

        // assert
        coVerify { noteDomainMapperMock.mapReverse(TestUtil.tNoteEntity) }
        coVerify { noteDaoMock.insert(TestUtil.tNoteModel) }
        assertThat(r).isInstanceOf(Result.Error::class.java)
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
        verify { noteDomainMapperMock.mapList(TestUtil.tNoteModelList) }
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
        coVerify { noteDomainMapperMock.map(TestUtil.tNoteModel) }
        coVerify { noteDaoMock.getById(1) }
        assertThat(r).isInstanceOf(Result.Success::class.java)
        assertThat((r as Result.Success).data).isEqualTo(TestUtil.tNoteEntity)
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
        assertThat(r).isInstanceOf(Result.Error::class.java)
    }

    @Test
    fun updateNote_onDbSuccessful() = runTest {
        // arrange
        coEvery { noteDaoMock.update(TestUtil.tNoteModel) } returns 1

        // act
        val r = systemUnderTest.update(TestUtil.tNoteEntity)

        // assert
        coVerify { noteDomainMapperMock.mapReverse(TestUtil.tNoteEntity) }
        coVerify { noteDaoMock.update(TestUtil.tNoteModel) }
        assertThat(r).isInstanceOf(Result.Success::class.java)
        assertThat((r as Result.Success).data).isTrue()
    }

    @Test
    fun updateNote_onDbError() = runTest {
        // arrange
        coEvery { noteDaoMock.update(TestUtil.tNoteModel) } returns null

        // act
        val r = systemUnderTest.update(TestUtil.tNoteEntity)

        // assert
        coVerify { noteDomainMapperMock.mapReverse(TestUtil.tNoteEntity) }
        coVerify { noteDaoMock.update(TestUtil.tNoteModel) }
        assertThat(r).isInstanceOf(Result.Error::class.java)
    }

    @Test
    fun deleteNote_onDbSuccessful() = runTest {
        // arrange
        coEvery { noteDaoMock.delete(TestUtil.tNoteModel) } returns 1

        // act
        val r = systemUnderTest.delete(TestUtil.tNoteEntity)

        // assert
        coVerify { noteDomainMapperMock.mapReverse(TestUtil.tNoteEntity) }
        coEvery { noteDaoMock.delete(TestUtil.tNoteModel) }
        assertThat(r).isInstanceOf(Result.Success::class.java)
        assertThat((r as Result.Success).data).isTrue()
    }

    @Test
    fun deleteNote_onDbError() = runTest {
        // arrange
        coEvery { noteDaoMock.delete(TestUtil.tNoteModel) } returns null

        // act
        val r = systemUnderTest.delete(TestUtil.tNoteEntity)

        // assert
        coVerify { noteDomainMapperMock.mapReverse(TestUtil.tNoteEntity) }
        coEvery { noteDaoMock.delete(TestUtil.tNoteModel) }
        assertThat(r).isInstanceOf(Result.Error::class.java)
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
        assertThat(r).isInstanceOf(Result.Success::class.java)
        assertThat((r as Result.Success).data).isEqualTo(1)
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
        assertThat(r).isInstanceOf(Result.Error::class.java)
    }

    @Test
    fun getAllNotesWithAlarm_onDbSuccessful() = runTest {
        // arrange
        coEvery { noteDaoMock.getAllNotesWithAlarm() } returns TestUtil.tNoteModelList

        // act
        val r = systemUnderTest.getAllNotesWithAlarm()

        // assert
        coVerify { noteDomainMapperMock.mapList(TestUtil.tNoteModelList) }
        coVerify { noteDaoMock.getAllNotesWithAlarm() }
        assertThat(r).isInstanceOf(Result.Success::class.java)
        assertThat((r as Result.Success).data).isEqualTo(TestUtil.tNoteEntityList)
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
        assertThat(r).isInstanceOf(Result.Error::class.java)
    }
}
