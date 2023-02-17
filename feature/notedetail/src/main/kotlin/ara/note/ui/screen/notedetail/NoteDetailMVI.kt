package ara.note.ui.screen.notedetail

import com.ara.aranote.data.datastore.UserPreferences
import com.ara.aranote.domain.entity.Note
import com.ara.aranote.domain.entity.Notebook
import ara.note.ui.screen.notedetail.NoteDetailViewModel.TheOperation
import com.ara.aranote.util.MviIntent
import com.ara.aranote.util.MviSingleEvent
import com.ara.aranote.util.MviState

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

    object LoadNotebooks : NoteDetailIntent
    data class ShowNotebooks(val notebooks: List<Notebook>) : NoteDetailIntent

    object UpdateNote : NoteDetailIntent

    data class BackPressed(val doesDelete: Boolean) : NoteDetailIntent

    object LoadUserPreferences : NoteDetailIntent
    data class ShowUserPreferences(val userPreferences: UserPreferences) : NoteDetailIntent
}

sealed interface NoteDetailSingleEvent : MviSingleEvent {
    object NavigateUp : NoteDetailSingleEvent
    data class DisableAlarm(val noteId: Int) : NoteDetailSingleEvent
    data class OperationError(val message: String = "") : NoteDetailSingleEvent
    data class BackPressed(val theOperation: TheOperation) :
        NoteDetailSingleEvent
}
