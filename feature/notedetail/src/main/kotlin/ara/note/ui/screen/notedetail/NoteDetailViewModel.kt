package ara.note.ui.screen.notedetail

import androidx.lifecycle.SavedStateHandle
import ara.note.domain.entity.Note
import ara.note.domain.usecase.note.CreateOrUpdateNoteUseCase
import ara.note.domain.usecase.note.DeleteNoteUseCase
import ara.note.domain.usecase.note.GetLastNoteIdUseCase
import ara.note.domain.usecase.note.GetNoteByIdUseCase
import ara.note.domain.usecase.notebook.ObserveNotebooksUseCase
import ara.note.domain.usecase.userpreferences.ObserveUserPreferencesUseCase
import ara.note.ui.BaseViewModel
import ara.note.util.DEFAULT_NOTEBOOK_ID
import ara.note.util.INVALID_NOTE_ID
import ara.note.util.NAV_ARGUMENT_NOTEBOOK_ID
import ara.note.util.NAV_ARGUMENT_NOTE_ID
import ara.note.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@HiltViewModel
class NoteDetailViewModel
@Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val observeNotebooksUseCase: ObserveNotebooksUseCase,
    private val createOrUpdateNoteUseCase: CreateOrUpdateNoteUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase,
    private val getLastNoteIdUseCase: GetLastNoteIdUseCase,
    private val getNoteByIdUseCase: GetNoteByIdUseCase,
    private val observeUserPreferencesUseCase: ObserveUserPreferencesUseCase,
) : BaseViewModel<NoteDetailState, NoteDetailIntent, NoteDetailSingleEvent>() {

    override fun initialState(): NoteDetailState = NoteDetailState()
    override val reducer: Reducer<NoteDetailState, NoteDetailIntent> = NoteDetailReducer()

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

                val note = if (noteId >= 0) {
                    when (val result = getNoteByIdUseCase(noteId)) {
                        is Result.Success -> result.data
                        is Result.Error -> error("Not Found")
                    }
                } else {
                    val lastId = getLastNoteIdUseCase()
                    Note(id = lastId + 1, notebookId = notebookId)
                }

                sendIntent(NoteDetailIntent.SetIsNewNote(isNewNote))
                sendIntent(NoteDetailIntent.ModifyNote(note))
            }
            is NoteDetailIntent.SetIsNewNote -> Unit

            is NoteDetailIntent.ModifyNote -> Unit
            is NoteDetailIntent.CreateOrUpdateNote ->
                if (createOrUpdateNoteUseCase(state.note)) {
                    triggerSingleEvent(NoteDetailSingleEvent.NavigateUp)
                } else {
                    triggerSingleEvent(NoteDetailSingleEvent.OperationError())
                }
            is NoteDetailIntent.DeleteNote ->
                if (deleteNoteUseCase(state.note)) {
                    triggerSingleEvent(NoteDetailSingleEvent.NavigateUp)
                } else {
                    triggerSingleEvent(NoteDetailSingleEvent.OperationError())
                }

            is NoteDetailIntent.LoadNotebooks -> sendIntent(NoteDetailIntent.ShowNotebooks(observeNotebooksUseCase().first()))
            is NoteDetailIntent.ShowNotebooks -> Unit

            is NoteDetailIntent.LoadUserPreferences -> sendIntent(NoteDetailIntent.ShowUserPreferences(observeUserPreferencesUseCase().first()))
            is NoteDetailIntent.ShowUserPreferences -> Unit
        }
    }
}

internal class NoteDetailReducer : BaseViewModel.Reducer<NoteDetailState, NoteDetailIntent> {

    override fun reduce(state: NoteDetailState, intent: NoteDetailIntent): NoteDetailState =
        when (intent) {
            is NoteDetailIntent.PrepareNote -> state
            is NoteDetailIntent.SetIsNewNote -> state.copy(isNewNote = intent.isNewNote)

            is NoteDetailIntent.ModifyNote -> state.copy(note = intent.note)
            is NoteDetailIntent.CreateOrUpdateNote -> state
            is NoteDetailIntent.DeleteNote -> state

            is NoteDetailIntent.LoadNotebooks -> state
            is NoteDetailIntent.ShowNotebooks -> state.copy(notebooks = intent.notebooks)

            is NoteDetailIntent.LoadUserPreferences -> state
            is NoteDetailIntent.ShowUserPreferences -> state.copy(userPreferences = intent.userPreferences)
        }
}
