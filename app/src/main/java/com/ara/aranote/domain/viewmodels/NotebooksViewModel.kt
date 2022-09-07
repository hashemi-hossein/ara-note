package com.ara.aranote.domain.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ara.aranote.domain.entity.Notebook
import com.ara.aranote.domain.repository.NoteRepository
import com.ara.aranote.domain.repository.NotebookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotebooksViewModel
@Inject constructor(
    private val noteRepository: NoteRepository,
    private val notebookRepository: NotebookRepository,
) : ViewModel() {

    private val _notebooks = MutableStateFlow(listOf<Notebook>())
    val notebooks = _notebooks.asStateFlow()

    init {
        viewModelScope.launch {
            notebookRepository.observe().collect { notebooks ->
                _notebooks.update { notebooks }
            }
        }
    }

    fun addNotebook(id: Int = 0, name: String) = viewModelScope.launch {
        notebookRepository.insert(Notebook(id = id, name = name))
    }

    fun modifyNotebook(notebook: Notebook) = viewModelScope.launch {
        notebookRepository.update(notebook)
    }

    fun deleteNotebook(notebook: Notebook) = viewModelScope.launch {
        val notesOfNotebook = noteRepository.observe(notebook.id).first()
        for (note in notesOfNotebook) {
            noteRepository.delete(note)
        }
        notebookRepository.delete(notebook)
    }
}
