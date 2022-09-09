package com.ara.aranote.domain.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ara.aranote.data.datastore.AppDataStore
import com.ara.aranote.domain.entity.Note
import com.ara.aranote.domain.entity.Notebook
import com.ara.aranote.domain.usecase.home.CreateDefaultNotebookUseCase
import com.ara.aranote.domain.usecase.home.ObserveNotebooksUseCase
import com.ara.aranote.domain.usecase.home.ObserveNotesUseCase
import com.ara.aranote.util.DEFAULT_NOTEBOOK_ID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel
@Inject constructor(
    createDefaultNotebookUseCase: CreateDefaultNotebookUseCase,
    observeNotebooksUseCase: ObserveNotebooksUseCase,
    private val observeNotesUseCase: ObserveNotesUseCase,
    val appDataStore: AppDataStore,
) : ViewModel() {

    private val _notes = MutableStateFlow(listOf<Note>())
    val notes = _notes.asStateFlow()

    private val _notebooks = MutableStateFlow(listOf<Notebook>())
    val notebooks = _notebooks.asStateFlow()

    private val _currentNotebookId = MutableStateFlow(DEFAULT_NOTEBOOK_ID)
    val currentNotebookId = _currentNotebookId.asStateFlow()

    init {
        observeNotes()
        viewModelScope.launch {
            createDefaultNotebookUseCase()
            observeNotebooksUseCase().collect { notebooks ->
                _notebooks.update { notebooks }
            }
        }
    }

    private var observeNotesJob: Job? = null
    private fun observeNotes() {
        observeNotesJob?.cancel()
        observeNotesJob = viewModelScope.launch {
            observeNotesUseCase(_currentNotebookId.value).collect { notes ->
                _notes.update { notes }
            }
        }
    }

    fun setCurrentNotebookId(id: Int) {
        println("setCurrentNotebookId id=$id")
        _currentNotebookId.update { id }
        observeNotes()
    }
}
