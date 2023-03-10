package ara.note.ui.screen.home

import ara.note.data.datastore.UserPreferences
import ara.note.domain.entity.Note
import ara.note.domain.entity.Notebook
import ara.note.ui.MviIntent
import ara.note.ui.MviSingleEvent
import ara.note.ui.MviState
import ara.note.util.DEFAULT_NOTEBOOK_ID

data class HomeState(
    val notes: List<Note> = emptyList(),
    val notebooks: List<Notebook> = emptyList(),
    val currentNotebookId: Int = DEFAULT_NOTEBOOK_ID,
    val userPreferences: UserPreferences = UserPreferences(),
    val searchText: String? = null,
) : MviState

sealed interface HomeIntent : MviIntent {
    object ObserveNotes : HomeIntent
    data class ShowNotes(val notes: List<Note>) : HomeIntent

    object ObserveNotebooks : HomeIntent
    data class ShowNotebooks(val notebooks: List<Notebook>) : HomeIntent

    data class ChangeNotebook(val notebookId: Int) : HomeIntent

    object ObserveUserPreferences : HomeIntent
    data class ShowUserPreferences(val userPreferences: UserPreferences) : HomeIntent

    data class ModifySearchText(val searchText: String?) : HomeIntent
}

sealed interface HomeSingleEvent : MviSingleEvent
