package ara.note.ui.screen.notedetail

import androidx.lifecycle.SavedStateHandle
import ara.note.domain.entity.Note
import ara.note.domain.usecase.note.CreateNoteUseCase
import ara.note.domain.usecase.note.DeleteNoteUseCase
import ara.note.domain.usecase.note.GetLastNoteIdUseCase
import ara.note.domain.usecase.note.GetNoteByIdUseCase
import ara.note.domain.usecase.note.UpdateNoteUseCase
import ara.note.domain.usecase.notebook.ObserveNotebooksUseCase
import ara.note.domain.usecase.userpreferences.ObserveUserPreferencesUseCase
import ara.note.util.BaseViewModel
import ara.note.util.DEFAULT_NOTEBOOK_ID
import ara.note.util.INVALID_NOTE_ID
import ara.note.util.NAV_ARGUMENT_NOTEBOOK_ID
import ara.note.util.NAV_ARGUMENT_NOTE_ID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
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
        sendIntent(NoteDetailIntent.LoadNotebooks)
        sendIntent(NoteDetailIntent.LoadUserPreferences)
    }

    override suspend fun handleIntent(intent: NoteDetailIntent, state: NoteDetailState) {
        when (intent) {
            is NoteDetailIntent.PrepareNote -> {
                val noteId = savedStateHandle.get<Int>(NAV_ARGUMENT_NOTE_ID) ?: INVALID_NOTE_ID
                val isNewNote = noteId < 0
                val notebookId =
                    savedStateHandle.get<Int>(NAV_ARGUMENT_NOTEBOOK_ID) ?: DEFAULT_NOTEBOOK_ID

//                Timber.tag(TAG).d("loading note - noteId=$noteId")

                val note = if (noteId >= 0) {
                    getNoteByIdUseCase(noteId)
                } else {
                    val lastId = getLastNoteIdUseCase()
                    Note(id = lastId + 1, notebookId = notebookId)
                }

                sendIntent(NoteDetailIntent.SetIsNewNote(isNewNote))
                sendIntent(NoteDetailIntent.ModifyNote(note))
            }
            is NoteDetailIntent.ModifyNote -> Unit
            is NoteDetailIntent.SetIsNewNote -> Unit

            is NoteDetailIntent.LoadNotebooks -> observeNotebooksUseCase().first()
                .let { sendIntent(NoteDetailIntent.ShowNotebooks(it)) }
            is NoteDetailIntent.ShowNotebooks -> Unit

            is NoteDetailIntent.UpdateNote -> updateNoteUseCase(state.note)

            is NoteDetailIntent.BackPressed -> {
                val result =
                    if (state.isNewNote) {
//              if(text.isNotBlank())
                        if (!intent.shouldDelete && (state.note.text.isNotEmpty() /*|| state.note.alarmDateTime != null*/)) {
                            createNoteUseCase(state.note)
                        } else {
                            true
                        }
                    } else {
                        if (!intent.shouldDelete) {
                            sendIntent(NoteDetailIntent.UpdateNote)
                            true
                        } else {
//                            if (state.note.alarmDateTime != null) {
//                                triggerSingleEvent(NoteDetailSingleEvent.DisableAlarm(state.note.id))
//                            }

                            deleteNoteUseCase(state.note)
                        }
                    }
                if (result) {
                    triggerSingleEvent(NoteDetailSingleEvent.NavigateUp)
                } else {
//                    Timber.tag(TAG).d("error in operation occurred")
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
}

internal class NoteDetailReducer : BaseViewModel.Reducer<NoteDetailState, NoteDetailIntent> {

    override fun reduce(state: NoteDetailState, intent: NoteDetailIntent): NoteDetailState =
        when (intent) {
            is NoteDetailIntent.PrepareNote -> state
            is NoteDetailIntent.SetIsNewNote -> state.copy(isNewNote = intent.isNewNote)
            is NoteDetailIntent.ModifyNote -> state.copy(note = intent.note)

            is NoteDetailIntent.LoadNotebooks -> state
            is NoteDetailIntent.ShowNotebooks -> state.copy(notebooks = intent.notebooks)

            is NoteDetailIntent.UpdateNote -> state
            is NoteDetailIntent.BackPressed -> state

            is NoteDetailIntent.LoadUserPreferences -> state
            is NoteDetailIntent.ShowUserPreferences -> state.copy(userPreferences = intent.userPreferences)
        }
}
