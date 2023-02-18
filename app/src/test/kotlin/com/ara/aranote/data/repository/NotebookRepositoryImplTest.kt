package com.ara.aranote.data.repository

import ara.note.data.localdatasource.NotebookDao
import ara.note.data.model.NotebookModel
import ara.note.domain.entity.Notebook
import com.ara.aranote.domain.util.Mapper
import ara.note.util.Result
import ara.note.test.TestUtil
import com.google.common.truth.Truth.assertThat
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
class NotebookRepositoryImplTest {

    private val notebookDaoMock = mockk<NotebookDao>()
    private val notebookDomainMapperMock = mockk<Mapper<NotebookModel, Notebook>>() {
        every { map(TestUtil.tNotebookModel) } returns TestUtil.tNotebookEntity
        every { mapReverse(TestUtil.tNotebookEntity) } returns TestUtil.tNotebookModel
        every { mapList(TestUtil.tNotebookModelList) } returns TestUtil.tNotebookEntityList
        every { mapListReverse(TestUtil.tNotebookEntityList) } returns TestUtil.tNotebookModelList
    }

    private val systemUnderTest = NotebookRepositoryImpl(
        notebookDao = notebookDaoMock,
        notebookDomainMapper = notebookDomainMapperMock,
    )

    @Test
    fun observeNotebooks() = runTest {
        // arrange
        every { notebookDaoMock.observe() } returns flowOf(
            TestUtil.tNotebookModelList,
            TestUtil.tNotebookModelList,
        )

        // act
        val r = systemUnderTest.observe()
        val r2 = r.toList()

        // assert
        verify { notebookDomainMapperMock.mapList(TestUtil.tNotebookModelList) }
        verify { notebookDaoMock.observe() }
        assertThat(r2).containsExactly(TestUtil.tNotebookEntityList, TestUtil.tNotebookEntityList)
            .inOrder()
    }

    @Test
    fun insertNotebook_onDbSuccessful() = runTest {
        // arrange
        coEvery { notebookDaoMock.insert(TestUtil.tNotebookModel) } returns 1

        // act
        val r = systemUnderTest.insert(TestUtil.tNotebookEntity)

        // assert
        coVerify { notebookDomainMapperMock.mapReverse(TestUtil.tNotebookEntity) }
        coVerify { notebookDaoMock.insert(TestUtil.tNotebookModel) }
        assertThat(r).isInstanceOf(Result.Success::class.java)
        assertThat((r as Result.Success).data).isEqualTo(1)
    }

    @Test
    fun insertNotebook_onDbError() = runTest {
        // arrange
        coEvery { notebookDaoMock.insert(TestUtil.tNotebookModel) } returns null

        // act
        val r = systemUnderTest.insert(TestUtil.tNotebookEntity)

        // assert
        coVerify { notebookDomainMapperMock.mapReverse(TestUtil.tNotebookEntity) }
        coVerify { notebookDaoMock.insert(TestUtil.tNotebookModel) }
        assertThat(r).isInstanceOf(Result.Error::class.java)
    }
}
