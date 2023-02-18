package com.ara.aranote.domain.repository

import ara.note.domain.entity.Notebook
import ara.note.util.Result
import kotlinx.coroutines.flow.Flow

interface NotebookRepository {

    fun observe(): Flow<List<Notebook>>

    suspend fun insert(notebook: Notebook): Result<Int>

    suspend fun delete(notebook: Notebook): Result<Boolean>

    suspend fun update(notebook: Notebook): Result<Boolean>
}
