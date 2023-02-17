package ara.note.ui.screen.home

import com.ara.aranote.data.datastore.UserPreferences
import com.ara.aranote.domain.entity.Note
import com.ara.aranote.domain.entity.Notebook
import com.ara.aranote.util.DEFAULT_NOTEBOOK_ID
import com.ara.aranote.util.MviIntent
import com.ara.aranote.util.MviSingleEvent
import com.ara.aranote.util.MviState

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
