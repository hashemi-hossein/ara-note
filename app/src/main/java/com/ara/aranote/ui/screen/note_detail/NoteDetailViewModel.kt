package com.ara.aranote.ui.screen.note_detail

import androidx.lifecycle.SavedStateHandle
import com.ara.aranote.domain.entity.Note
import com.ara.aranote.domain.usecase.home.ObserveNotebooksUseCase
import com.ara.aranote.domain.usecase.note_detail.CreateNoteUseCase
import com.ara.aranote.domain.usecase.note_detail.DeleteNoteUseCase
import com.ara.aranote.domain.usecase.note_detail.GetLastNoteIdUseCase
import com.ara.aranote.domain.usecase.note_detail.GetNoteByIdUseCase
import com.ara.aranote.domain.usecase.note_detail.UpdateNoteUseCase
import com.ara.aranote.domain.usecase.user_preferences.ObserveUserPreferencesUseCase
import com.ara.aranote.util.BaseViewModel
import com.ara.aranote.util.DEFAULT_NOTEBOOK_ID
import com.ara.aranote.util.HDateTime
import com.ara.aranote.util.INVALID_NOTE_ID
import com.ara.aranote.util.NAV_ARGUMENT_NOTEBOOK_ID
import com.ara.aranote.util.NAV_ARGUMENT_NOTE_ID
import com.ara.aranote.util.TAG
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class NoteDetailViewModel
@Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val observeNotebooksUseCase: ObserveNotebooksUseCase,
    private val createNoteUseCase: CreateNoteUseCase,
    private val updateNoteUseCase: UpdateNoteUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase,
    private val getLastNoteIdUseCase: GetLastNoteIdUseCase,
    private val getNoteByIdUseCase: GetNoteByIdUseCase,
    private val observeUserPreferencesUseCase: ObserveUserPreferencesUseCase,
) : BaseViewModel<NoteDetailState, NoteDetailIntent, NoteDetailSingleEvent>() {

    override fun initialState(): NoteDetailState = NoteDetailState()

    init {
        sendIntent(NoteDetailIntent.PrepareNote)
    }

    override suspend fun handleIntent(intent: NoteDetailIntent, state: NoteDetailState) {
        when (intent) {
            is NoteDetailIntent.PrepareNote -> {
                val noteId = savedStateHandle.get<Int>(NAV_ARGUMENT_NOTE_ID) ?: INVALID_NOTE_ID
                isNewNote = noteId < 0
                val notebookId =
                    savedStateHandle.get<Int>(NAV_ARGUMENT_NOTEBOOK_ID) ?: DEFAULT_NOTEBOOK_ID

                Timber.tag(TAG).d("loading note - noteId=$noteId")

                val note = if (noteId >= 0) {
                    getNoteByIdUseCase(noteId)
                } else {
                    val lastId = getLastNoteIdUseCase()
                    Note(id = lastId + 1, notebookId = notebookId)
                }

                sendIntent(NoteDetailIntent.ModifyNote(note))
            }

            is NoteDetailIntent.ModifyNote -> Unit

            is NoteDetailIntent.LoadNotebooks -> observeNotebooksUseCase().first()
                .let { sendIntent(NoteDetailIntent.ShowNotebooks(it)) }

            is NoteDetailIntent.ShowNotebooks -> Unit

            is NoteDetailIntent.UpdateNote -> {
                val oldNote = getNoteByIdUseCase(state.note.id)
                if (oldNote.text != state.note.text) {
                    Timber.tag(TAG).d("updating note")
                    Timber.tag(TAG).d("note = %s", state.note.toString())
                    val note = state.note.copy(addedDateTime = HDateTime.getCurrentDateTime())
                    updateNoteUseCase(note)
                    sendIntent(NoteDetailIntent.ModifyNote(note))
                }
            }

            is NoteDetailIntent.BackPressed -> {
                val result =
                    if (isNewNote) {
//              if(text.isNotBlank())
                        if (!intent.doesDelete && (state.note.text.isNotEmpty() || state.note.alarmDateTime != null))
                            createNoteUseCase(state.note)
                        else
                            true
                    } else {
                        if (!intent.doesDelete) {
                            sendIntent(NoteDetailIntent.UpdateNote)
                            true
                        } else {
                            if (state.note.alarmDateTime != null)
                                triggerSingleEvent(NoteDetailSingleEvent.DisableAlarm(state.note.id))

                            deleteNoteUseCase(state.note)
                        }
                    }
                if (result) {
                    triggerSingleEvent(NoteDetailSingleEvent.NavigateUp)
                } else {
                    Timber.tag(TAG).d("error in operation occurred")
                    triggerSingleEvent(NoteDetailSingleEvent.OperationError())
                }
            }

            is NoteDetailIntent.LoadUserPreferences -> observeUserPreferencesUseCase().first()
                .let { sendIntent(NoteDetailIntent.ShowUserPreferences(it)) }
            is NoteDetailIntent.ShowUserPreferences -> Unit
        }
    }

    override val reducer: Reducer<NoteDetailState, NoteDetailIntent>
        get() = NoteDetailReducer()

    var isNewNote: Boolean = true

    enum class TheOperation {
        SAVE, DISCARD, DELETE
    }
}

internal class NoteDetailReducer : BaseViewModel.Reducer<NoteDetailState, NoteDetailIntent> {

    override fun reduce(state: NoteDetailState, intent: NoteDetailIntent): NoteDetailState =
        when (intent) {
            is NoteDetailIntent.PrepareNote -> state
            is NoteDetailIntent.ModifyNote -> state.copy(note = intent.note)

            is NoteDetailIntent.LoadNotebooks -> state
            is NoteDetailIntent.ShowNotebooks -> state.copy(notebooks = intent.notebooks)

            is NoteDetailIntent.UpdateNote -> state
            is NoteDetailIntent.BackPressed -> state

            is NoteDetailIntent.LoadUserPreferences -> state
            is NoteDetailIntent.ShowUserPreferences -> state.copy(userPreferences = intent.userPreferences)
        }
}
