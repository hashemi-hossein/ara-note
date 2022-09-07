package com.ara.aranote.data.repository

import com.ara.aranote.data.local_data_source.NotebookDao
import com.ara.aranote.data.model.NotebookModel
import com.ara.aranote.domain.entity.Notebook
import com.ara.aranote.domain.repository.NotebookRepository
import com.ara.aranote.domain.util.Mapper
import com.ara.aranote.util.INVALID_NOTEBOOK_ID
import com.ara.aranote.util.TAG
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber

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

    override fun observeNotebooks(): Flow<List<Notebook>> {
        return notebookDao.observeNotebooks().map {
            notebookDomainMapper.mapList(it)
        }
    }

    override suspend fun insertNotebook(notebook: Notebook): Int {
        val result = notebookDao.insertNotebook(notebookDomainMapper.mapReverse(notebook))
        Timber.tag(TAG).d("insert notebook result = $result")
        return result?.toInt() ?: INVALID_NOTEBOOK_ID
    }

    override suspend fun deleteNotebook(notebook: Notebook): Boolean {
        val result = notebookDao.deleteNotebook(notebookDomainMapper.mapReverse(notebook))
        Timber.tag(TAG).d("delete notebook result = $result")
        return result == 1
    }

    override suspend fun updateNotebook(notebook: Notebook): Boolean {
        val result = notebookDao.updateNotebook(notebookDomainMapper.mapReverse(notebook))
        Timber.tag(TAG).d("update notebook result = $result")
        return result == 1
    }
}
