package com.ara.aranote.domain.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ara.aranote.data.datastore.AppDataStore
import com.ara.aranote.domain.entity.Note
import com.ara.aranote.domain.entity.Notebook
import com.ara.aranote.domain.repository.NoteRepository
import com.ara.aranote.util.DEFAULT_NOTEBOOK_ID
import com.ara.aranote.util.DEFAULT_NOTEBOOK_NAME
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel
@Inject
constructor(
    private val repository: NoteRepository,
    val appDataStore: AppDataStore,
) : ViewModel() {

    private val _notes = MutableStateFlow(listOf<Note>())
    val notes = _notes.asStateFlow()

    private val _notebooks = MutableStateFlow(listOf<Notebook>())
    val notebooks = _notebooks.asStateFlow()

    private val _currentNotebookId = MutableStateFlow(DEFAULT_NOTEBOOK_ID)
    val currentNotebookId = _currentNotebookId.asStateFlow()

    init {
        createDefaultNotebook()
        observeNotes()
        viewModelScope.launch {
            repository.observeNotebooks().collect { notebooks ->
                _notebooks.update { notebooks }
            }
        }
    }

    private var observeNotesJob: Job? = null
    private fun observeNotes() {
        observeNotesJob?.cancel()
        observeNotesJob = viewModelScope.launch {
            repository.observeNotes(_currentNotebookId.value).collect { notes ->
                _notes.update { notes }
            }
        }
    }

    fun setCurrentNotebookId(id: Int) {
        println("setCurrentNotebookId id=$id")
        _currentNotebookId.update { id }
        observeNotes()
    }

    private fun createDefaultNotebook() = viewModelScope.launch {
        if (!appDataStore.readPref(AppDataStore.DEFAULT_NOTEBOOK_EXISTENCE_KEY, false)) {
            appDataStore.writePref(AppDataStore.DEFAULT_NOTEBOOK_EXISTENCE_KEY, true)
            repository.insertNotebook(
                Notebook(
                    id = DEFAULT_NOTEBOOK_ID,
                    name = DEFAULT_NOTEBOOK_NAME
                )
            )
        }
    }
}
