package com.ara.aranote.domain.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ara.aranote.domain.entity.Notebook
import com.ara.aranote.domain.usecase.home.ObserveNotebooksUseCase
import com.ara.aranote.domain.usecase.notebooks.CreateNotebookUseCase
import com.ara.aranote.domain.usecase.notebooks.DeleteNotebookUseCase
import com.ara.aranote.domain.usecase.notebooks.UpdateNotebookUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotebooksViewModel
@Inject constructor(
    observeNotebooksUseCase: ObserveNotebooksUseCase,
    private val createNotebookUseCase: CreateNotebookUseCase,
    private val updateNotebookUseCase: UpdateNotebookUseCase,
    private val deleteNotebookUseCase: DeleteNotebookUseCase,
) : ViewModel() {

    private val _notebooks = MutableStateFlow(listOf<Notebook>())
    val notebooks = _notebooks.asStateFlow()

    init {
        viewModelScope.launch {
            observeNotebooksUseCase().collect { notebooks ->
                _notebooks.update { notebooks }
            }
        }
    }

    fun addNotebook(id: Int = 0, name: String) = viewModelScope.launch {
        createNotebookUseCase(Notebook(id = id, name = name))
    }

    fun modifyNotebook(notebook: Notebook) = viewModelScope.launch {
        updateNotebookUseCase(notebook)
    }

    fun deleteNotebook(notebook: Notebook) = viewModelScope.launch {
        deleteNotebookUseCase(notebook)
    }
}
