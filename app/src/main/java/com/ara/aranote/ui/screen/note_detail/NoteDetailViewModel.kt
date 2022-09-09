package com.ara.aranote.ui.screen.note_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ara.aranote.data.datastore.AppDataStore
import com.ara.aranote.domain.entity.Note
import com.ara.aranote.domain.entity.Notebook
import com.ara.aranote.domain.usecase.home.ObserveNotebooksUseCase
import com.ara.aranote.domain.usecase.note_detail.CreateNoteUseCase
import com.ara.aranote.domain.usecase.note_detail.DeleteNoteUseCase
import com.ara.aranote.domain.usecase.note_detail.GetLastNoteIdUseCase
import com.ara.aranote.domain.usecase.note_detail.GetNoteByIdUseCase
import com.ara.aranote.domain.usecase.note_detail.UpdateNoteUseCase
import com.ara.aranote.util.DEFAULT_NOTEBOOK_ID
import com.ara.aranote.util.HDateTime
import com.ara.aranote.util.INVALID_NOTE_ID
import com.ara.aranote.util.NAV_ARGUMENT_NOTEBOOK_ID
import com.ara.aranote.util.NAV_ARGUMENT_NOTE_ID
import com.ara.aranote.util.TAG
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class NoteDetailViewModel
@Inject constructor(
    savedStateHandle: SavedStateHandle,
    observeNotebooksUseCase: ObserveNotebooksUseCase,
    private val createNoteUseCase: CreateNoteUseCase,
    private val updateNoteUseCase: UpdateNoteUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase,
    private val getLastNoteIdUseCase: GetLastNoteIdUseCase,
    private val getNoteByIdUseCase: GetNoteByIdUseCase,
    val appDataStore: AppDataStore,
) : ViewModel() {

    private val _note = MutableStateFlow(
        Note(
            id = 0,
            notebookId = DEFAULT_NOTEBOOK_ID,
            text = "",
            addedDateTime = HDateTime.getCurrentDateTime()
        )
    )
    val note = _note.asStateFlow()

    private val _notebooks = MutableStateFlow(listOf<Notebook>())
    val notebooks = _notebooks.asStateFlow()

    private lateinit var originalNote: Note
    private val _isModified = MutableStateFlow(false)
    val isModified = _isModified.asStateFlow()

    val isNewNote: Boolean

    init {
        val noteId = savedStateHandle.get<Int>(NAV_ARGUMENT_NOTE_ID) ?: INVALID_NOTE_ID
        isNewNote = noteId < 0
        val notebookId = savedStateHandle.get<Int>(NAV_ARGUMENT_NOTEBOOK_ID) ?: DEFAULT_NOTEBOOK_ID

        viewModelScope.launch {
            prepareNote(noteId = noteId, notebookId = notebookId)
            _notebooks.update { observeNotebooksUseCase().first() }
        }
    }

    suspend fun prepareNote(noteId: Int, notebookId: Int = DEFAULT_NOTEBOOK_ID) {
        Timber.tag(TAG).d("loading note - noteId=$noteId")
        _note.value = if (noteId >= 0) {
            getNoteByIdUseCase(noteId)
        } else {
            val lastId = getLastNoteIdUseCase()
            _note.value.copy(id = lastId + 1, notebookId = notebookId)
        }
        originalNote = _note.value
    }

    fun modifyNote(note: Note) {
        _note.update { note }
        _isModified.value =
            if (::originalNote.isInitialized) _note.value != originalNote else false
    }

    fun restoreNote() {
        modifyNote(originalNote)
    }

    private suspend fun updateNote(): Boolean {
        val oldNote = getNoteByIdUseCase(_note.value.id)
        val result =
            if (oldNote != _note.value) {
                Timber.tag(TAG).d("updating note")
                Timber.tag(TAG).d("note = %s", _note.value.toString())
                if (oldNote.text != _note.value.text)
                    _note.value = _note.value.copy(addedDateTime = HDateTime.getCurrentDateTime())
                updateNoteUseCase(note.value)
            } else
                true
        return result
    }

    enum class TheOperation {
        SAVE, DISCARD, DELETE
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
                    createNoteUseCase(note.value)
                else
                    true
            } else {
                if (!doesDelete)
                    updateNote()
                else {
                    _note.value.alarmDateTime?.let {
                        disableAlarm(_note.value.id)
                    }
                    deleteNoteUseCase(note.value)
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
