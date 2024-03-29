package ara.note.ui.screen.notedetail

import ara.note.data.model.UserPreferences
import ara.note.domain.entity.Note
import ara.note.domain.entity.Notebook
import ara.note.ui.MviIntent
import ara.note.ui.MviSingleEvent
import ara.note.ui.MviState

data class NoteDetailState(
    val note: Note = Note(),
    val notebooks: List<Notebook> = emptyList(),
    val userPreferences: UserPreferences = UserPreferences(),
    val isNewNote: Boolean = false,
) : MviState

sealed interface NoteDetailIntent : MviIntent {
    object PrepareNote : NoteDetailIntent
    data class SetIsNewNote(val isNewNote: Boolean) : NoteDetailIntent

    data class ModifyNote(val note: Note) : NoteDetailIntent
    object CreateOrUpdateNote : NoteDetailIntent
    object DeleteNote : NoteDetailIntent

    object LoadNotebooks : NoteDetailIntent
    data class ShowNotebooks(val notebooks: List<Notebook>) : NoteDetailIntent

    object LoadUserPreferences : NoteDetailIntent
    data class ShowUserPreferences(val userPreferences: UserPreferences) : NoteDetailIntent
}

sealed interface NoteDetailSingleEvent : MviSingleEvent {
    object NavigateUp : NoteDetailSingleEvent

//    data class DisableAlarm(val noteId: Int) : NoteDetailSingleEvent
    data class ShowSnackbar(val message: Int, val actionLabel: Int, val onClick: (() -> Unit)? = null) : NoteDetailSingleEvent
}
