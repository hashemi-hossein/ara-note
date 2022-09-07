package com.ara.aranote.data.repository

import com.ara.aranote.data.local_data_source.NotebookDao
import com.ara.aranote.data.model.NotebookModel
import com.ara.aranote.domain.entity.Notebook
import com.ara.aranote.domain.repository.NotebookRepository
import com.ara.aranote.domain.util.DomainMapper
import com.ara.aranote.util.INVALID_NOTEBOOK_ID
import com.ara.aranote.util.TAG
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber

class NotebookRepositoryImpl(
    private val notebookDao: NotebookDao,
    private val notebookDomainMapper: DomainMapper<NotebookModel, Notebook>,
) : NotebookRepository {

    override fun observeNotebooks(): Flow<List<Notebook>> {
        return notebookDao.observeNotebooks().map {
            notebookDomainMapper.toDomainList(it)
        }
    }

    override suspend fun insertNotebook(notebook: Notebook): Int {
        val result = notebookDao.insertNotebook(notebookDomainMapper.mapFromDomainEntity(notebook))
        Timber.tag(TAG).d("insert notebook result = $result")
        return result?.toInt() ?: INVALID_NOTEBOOK_ID
    }

    override suspend fun deleteNotebook(notebook: Notebook): Boolean {
        val result = notebookDao.deleteNotebook(notebookDomainMapper.mapFromDomainEntity(notebook))
        Timber.tag(TAG).d("delete notebook result = $result")
        return result == 1
    }

    override suspend fun updateNotebook(notebook: Notebook): Boolean {
        val result = notebookDao.updateNotebook(notebookDomainMapper.mapFromDomainEntity(notebook))
        Timber.tag(TAG).d("update notebook result = $result")
        return result == 1
    }
}
