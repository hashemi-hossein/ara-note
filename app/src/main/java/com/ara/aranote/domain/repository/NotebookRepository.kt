package com.ara.aranote.domain.repository

import com.ara.aranote.domain.entity.Notebook
import kotlinx.coroutines.flow.Flow

interface NotebookRepository {

    fun observeNotebooks(): Flow<List<Notebook>>

    suspend fun insertNotebook(notebook: Notebook): Int

    suspend fun deleteNotebook(notebook: Notebook): Boolean

    suspend fun updateNotebook(notebook: Notebook): Boolean
}
