package ara.note.data.repository

import ara.note.data.localdatasource.NotebookDao
import ara.note.data.model.toDomainEntity
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
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertIs

@ExperimentalCoroutinesApi
class NotebookRepositoryImplTest {

    private val notebookDaoMock = mockk<NotebookDao>()

    private val systemUnderTest = NotebookRepositoryImpl(notebookDao = notebookDaoMock)

    @Test
    fun observeNotebooks() = runTest {
        // given
        every { notebookDaoMock.observeWithCount() } returns flowOf(
            mapOf(TestUtil.tNotebookModel to 5, TestUtil.tNotebookModel2 to 10),
        )

        // when
        val r = systemUnderTest.observe()
        val r2 = r.toList()

        // then
        verify { notebookDaoMock.observeWithCount() }
        assertContains(r2, listOf(TestUtil.tNotebookModel.toDomainEntity(5), TestUtil.tNotebookModel2.toDomainEntity(10)))
    }

    @Test
    fun insertNotebook_onDbSuccessful() = runTest {
        // given
        coEvery { notebookDaoMock.insert(TestUtil.tNotebookEntity.toDataModel()) } returns 1

        // when
        val r = systemUnderTest.insert(TestUtil.tNotebookEntity)

        // then
        coVerify { notebookDaoMock.insert(TestUtil.tNotebookEntity.toDataModel()) }
        assertIs<Result.Success<Int>>(r)
        assertEquals(r.data, 1)
    }

    @Test
    fun insertNotebook_onDbError() = runTest {
        // given
        coEvery { notebookDaoMock.insert(TestUtil.tNotebookEntity.toDataModel()) } returns null

        // when
        val r = systemUnderTest.insert(TestUtil.tNotebookEntity)

        // then
        coVerify { notebookDaoMock.insert(TestUtil.tNotebookEntity.toDataModel()) }
        assertIs<Result.Error>(r)
    }
}
