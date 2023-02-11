package com.ara.aranote.domain.repository

import com.ara.aranote.domain.entity.Notebook
import com.ara.aranote.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update

class FakeNotebookRepository : NotebookRepository {

    private val notebooks = mutableMapOf<Int, Notebook>()
    private val notebooksFlow = MutableStateFlow(notebooks.values.toList())

    override fun observe(): Flow<List<Notebook>> {
        return flow { notebooksFlow.collect { emit(it) } }
    }

    override suspend fun insert(notebook: Notebook): Result<Int> {
        val r = if (notebooks.put(notebook.id, notebook) == null) {
            notebook.id
        } else {
            return Result.Error()
        }
        notebooksFlow.update { notebooks.values.toList() }
        return Result.Success(r)
    }

    override suspend fun delete(notebook: Notebook): Result<Boolean> {
        val r = notebooks.remove(notebook.id) != null
        notebooksFlow.update { notebooks.values.toList() }
        return Result.Success(r)
    }

    override suspend fun update(notebook: Notebook): Result<Boolean> {
        val r = notebooks.put(notebook.id, notebook) == null
        notebooksFlow.update { notebooks.values.toList() }
        return Result.Success(r)
    }
}
