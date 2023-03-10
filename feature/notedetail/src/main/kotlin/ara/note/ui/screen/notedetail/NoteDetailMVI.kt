package ara.note.ui.screen.notedetail

import ara.note.data.datastore.UserPreferences
import ara.note.domain.entity.Note
import ara.note.domain.entity.Notebook
import ara.note.util.MviIntent
import ara.note.util.MviSingleEvent
import ara.note.util.MviState

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
    data class OperationError(val message: String = "") : NoteDetailSingleEvent
}
