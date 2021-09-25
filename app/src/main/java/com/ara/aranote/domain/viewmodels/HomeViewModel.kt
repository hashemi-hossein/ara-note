package com.ara.aranote.domain.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ara.aranote.domain.entity.Note
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
class HomeViewModel
@Inject
constructor(
    private val repository: NoteRepository,
) : ViewModel() {

    private val _notes = MutableStateFlow(listOf<Note>())
    val notes = _notes.asStateFlow()

    private val _notebooks = MutableStateFlow(listOf<Notebook>())
    val notebooks = _notebooks.asStateFlow()

    init {
        viewModelScope.launch {
            repository.observeNotes().collect { notes ->
                _notes.update { notes }
            }
        }
        viewModelScope.launch {
            repository.observeNotebooks().collect { notebooks ->
                _notebooks.update { notebooks }
            }
        }
    }
}
