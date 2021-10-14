package com.ara.aranote.domain.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ara.aranote.domain.entity.Notebook
import com.ara.aranote.domain.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotebooksViewModel
@Inject
constructor(
    private val repository: NoteRepository,
) : ViewModel() {

    private val _notebooks = MutableStateFlow(listOf<Notebook>())
    val notebooks = _notebooks.asStateFlow()

    init {
        viewModelScope.launch {
            repository.observeNotebooks().collect { notebooks ->
                _notebooks.update { notebooks }
            }
        }
    }

    fun addNotebook(id: Int = 0, name: String) = viewModelScope.launch {
        repository.insertNotebook(Notebook(id = id, name = name))
    }

    fun modifyNotebook(notebook: Notebook) = viewModelScope.launch {
        repository.updateNotebook(notebook)
    }

    fun deleteNotebook(notebook: Notebook) = viewModelScope.launch {
        repository.deleteNotebook(notebook)
    }
}
