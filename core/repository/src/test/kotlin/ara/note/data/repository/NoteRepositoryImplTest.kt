package ara.note.data.repository

import ara.note.data.localdatasource.NoteDao
import ara.note.data.model.toDomainEntity
import ara.note.domain.entity.Note
import ara.note.domain.entity.toDataModel
import ara.note.test.TestUtil
import ara.note.util.Result
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
import kotlin.test.assertEquals
import kotlin.test.assertIs

@ExperimentalCoroutinesApi
class NoteRepositoryImplTest {

    private val noteDaoMock = mockk<NoteDao>()

    private val systemUnderTest = NoteRepositoryImpl(
        noteDao = noteDaoMock,
    )

    @Test
    fun insertNote_onDbSuccessful() = runTest {
        // given
        coEvery { noteDaoMock.insert(TestUtil.tNoteEntity.toDataModel()) } returns 1

        // when
        val r = systemUnderTest.insert(TestUtil.tNoteEntity)

        // then
        coVerify { noteDaoMock.insert(TestUtil.tNoteEntity.toDataModel()) }
        assertIs<Result.Success<Int>>(r)
        assertEquals(r.data, 1)
    }

    @Test
    fun insertNote_onDbError() = runTest {
        // given
        coEvery { noteDaoMock.insert(TestUtil.tNoteEntity.toDataModel()) } returns null

        // when
        val r = systemUnderTest.insert(TestUtil.tNoteEntity)

        // then
        coVerify { noteDaoMock.insert(TestUtil.tNoteEntity.toDataModel()) }
        assertIs<Result.Error>(r)
    }

    @Test
    fun observeNotes() = runTest {
        // given
        every { noteDaoMock.observe(any<Int>()) } returns flowOf(
            TestUtil.tNoteModelList,
            TestUtil.tNoteModelList,
        )

        // when
        val r = systemUnderTest.observe(notebookId = 0)
        val r2 = r.toList()

        // then
        verify { noteDaoMock.observe(any<Int>()) }
        val reorderedList = TestUtil.tNoteModelList.toDomainEntity().sortedByDescending { it.modifiedDateTime }
        assertEquals(r2, listOf(reorderedList, reorderedList))
    }

    @Test
    fun getNote_onDbSuccessful() = runTest {
        // given
        coEvery { noteDaoMock.getById(1) } returns TestUtil.tNoteModel

        // when
        val r = systemUnderTest.getById(1)

        // then
        coVerify { noteDaoMock.getById(1) }
        assertIs<Result.Success<Note>>(r)
        assertEquals(r.data, TestUtil.tNoteModel.toDomainEntity())
    }

    @Test
    fun getNote_onDbError() = runTest {
        // given
        coEvery { noteDaoMock.getById(1) } returns null

        // when
        val r = systemUnderTest.getById(1)

        // then
        coVerify { noteDaoMock.getById(1) }
        assertIs<Result.Error>(r)
    }

    @Test
    fun updateNote_onDbSuccessful() = runTest {
        // given
        coEvery { noteDaoMock.update(TestUtil.tNoteEntity.toDataModel()) } returns 1

        // when
        val r = systemUnderTest.update(TestUtil.tNoteEntity)

        // then
        coVerify { noteDaoMock.update(TestUtil.tNoteEntity.toDataModel()) }
        assertIs<Result.Success<Boolean>>(r)
        assert(r.data)
    }

    @Test
    fun updateNote_onDbError() = runTest {
        // given
        coEvery { noteDaoMock.update(TestUtil.tNoteEntity.toDataModel()) } returns null

        // when
        val r = systemUnderTest.update(TestUtil.tNoteEntity)

        // then
        coVerify { noteDaoMock.update(TestUtil.tNoteEntity.toDataModel()) }
        assertIs<Result.Error>(r)
    }

    @Test
    fun deleteNote_onDbSuccessful() = runTest {
        // given
        coEvery { noteDaoMock.delete(TestUtil.tNoteEntity.toDataModel()) } returns 1

        // when
        val r = systemUnderTest.delete(TestUtil.tNoteEntity)

        // then
        coEvery { noteDaoMock.delete(TestUtil.tNoteEntity.toDataModel()) }
        assertIs<Result.Success<Boolean>>(r)
        assert(r.data)
    }

    @Test
    fun deleteNote_onDbError() = runTest {
        // given
        coEvery { noteDaoMock.delete(TestUtil.tNoteEntity.toDataModel()) } returns null

        // when
        val r = systemUnderTest.delete(TestUtil.tNoteEntity)

        // then
        coEvery { noteDaoMock.delete(TestUtil.tNoteEntity.toDataModel()) }
        assertIs<Result.Error>(r)
    }

    @Test
    fun getLastId_onDbSuccessful() = runTest {
        // given
        coEvery { noteDaoMock.getLastId() } returns 1

        // when
        val r = systemUnderTest.getLastId()

        // then
        coVerify { noteDaoMock.getLastId() }
        assertIs<Result.Success<Int>>(r)
        assertEquals(r.data, 1)
    }

    @Test
    fun getLastId_onEmptyDb_or_onDbError() = runTest {
        // given
        coEvery { noteDaoMock.getLastId() } returns null

        // when
        val r = systemUnderTest.getLastId()

        // then
        coVerify { noteDaoMock.getLastId() }
        assertIs<Result.Error>(r)
    }
}
