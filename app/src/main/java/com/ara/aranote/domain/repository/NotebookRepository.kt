package com.ara.aranote.domain.repository

import com.ara.aranote.domain.entity.Notebook
import kotlinx.coroutines.flow.Flow

interface NotebookRepository {

    fun observe(): Flow<List<Notebook>>

    suspend fun insert(notebook: Notebook): Int

    suspend fun delete(notebook: Notebook): Boolean

    suspend fun update(notebook: Notebook): Boolean
}
