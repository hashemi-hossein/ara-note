package com.ara.aranote.data.repository

import ara.note.data.localdatasource.NotebookDao
import ara.note.data.model.NotebookModel
import ara.note.domain.entity.Notebook
import com.ara.aranote.domain.repository.NotebookRepository
import com.ara.aranote.domain.util.Mapper
import ara.note.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Based on SINGLE-SOURCE-OF-TRUTH strategy:
 *
 * Every operations on tblNoteBook such as CRUD or observation on the table's data
 * will be done via this repository
 *
 * Every function notify the caller about the result of the operation
 * by returning true or positive value in case of successful operation
 * and false or negative value in case of error
 */
class NotebookRepositoryImpl(
    private val notebookDao: NotebookDao,
    private val notebookDomainMapper: Mapper<NotebookModel, Notebook>,
) : NotebookRepository {

    override fun observe(): Flow<List<Notebook>> {
        return notebookDao.observe().map {
            notebookDomainMapper.mapList(it)
        }
    }

    override suspend fun insert(notebook: Notebook): Result<Int> {
        val result = notebookDao.insert(notebookDomainMapper.mapReverse(notebook))
        return if (result != null) Result.Success(result.toInt()) else Result.Error()
    }

    override suspend fun delete(notebook: Notebook): Result<Boolean> {
        val result = notebookDao.delete(notebookDomainMapper.mapReverse(notebook))
        return if (result != null) Result.Success(result == 1) else Result.Error()
    }

    override suspend fun update(notebook: Notebook): Result<Boolean> {
        val result = notebookDao.update(notebookDomainMapper.mapReverse(notebook))
        return if (result != null) Result.Success(result == 1) else Result.Error()
    }
}
