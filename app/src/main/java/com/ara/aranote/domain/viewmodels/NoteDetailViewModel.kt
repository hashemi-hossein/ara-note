package com.ara.aranote.domain.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ara.aranote.domain.entity.Note
import com.ara.aranote.domain.repository.NoteRepository
import com.ara.aranote.util.HDateTime
import com.ara.aranote.util.TAG
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class NoteDetailViewModel
@Inject
constructor(
    private val repository: NoteRepository,
) : ViewModel() {

    private val _note = MutableStateFlow(Note(0, "", HDateTime.getCurrentDateTime()))
    val note = _note.asStateFlow()

    init {
        Timber.tag(TAG).d(this.javaClass.name)
    }

    suspend fun prepareNote(id: Int) {
        if (id >= 0) {
//            Timber.tag(TAG).d("loading note - id=$id")
            _note.value = repository.getNote(id) ?: _note.value.copy(text = "ERROR")
        } else {
            _note.value = _note.value.copy(id = repository.getLastId() + 1)
        }
    }

    fun modifyNote(note: Note) {
        _note.update { note }
//        _note.value=note
    }

    private suspend fun addNote(): Boolean {
        val result = repository.insertNote(_note.value)
        return result >= 0
    }

    private suspend fun updateNote(): Boolean {
        val oldNote = repository.getNote(_note.value.id)
        val result =
            if (oldNote != _note.value) {
                Timber.tag(TAG).d("updating note")
                Timber.tag(TAG).d("note = %s", _note.value.toString())
                repository.updateNote(_note.value.copy(addedDateTime = HDateTime.getCurrentDateTime()))
            } else
                true
        return result
    }

    private suspend fun deleteNote(): Boolean {
        return repository.deleteNote(_note.value)
    }

    fun backPressed(
        isNewNote: Boolean,
        doesDelete: Boolean,
        navigateUp: () -> Unit,
        disableAlarm: (Int) -> Unit,
        onOperationError: () -> Unit,
    ) = viewModelScope.launch {
        val result =
            if (isNewNote) {
//              if(text.isNotBlank())
                if (!doesDelete && (_note.value.text.isNotEmpty() || _note.value.alarmDateTime != null))
                    addNote()
                else
                    true
            } else {
                if (!doesDelete)
                    updateNote()
                else {
                    _note.value.alarmDateTime?.let {
                        disableAlarm(_note.value.id)
                    }
                    deleteNote()
                }
            }
        if (result) {
//                Timber.tag(TAG).d("operation was successful")
//                Toast.makeText(context, "operation was successful", Toast.LENGTH_SHORT).show()
            navigateUp()
        } else {
            Timber.tag(TAG).d("error in operation occurred")
            onOperationError()
        }
    }
}