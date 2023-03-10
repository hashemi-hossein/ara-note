package ara.note.ui.screen.notebookslist

import ara.note.domain.entity.Notebook
import ara.note.ui.MviIntent
import ara.note.ui.MviSingleEvent
import ara.note.ui.MviState

data class NotebooksListState(
    val notebooks: List<Notebook> = emptyList(),
) : MviState

sealed interface NotebooksListIntent : MviIntent {
    object ObserveNotebooks : NotebooksListIntent
    data class ShowNotebooks(val notebooks: List<Notebook>) : NotebooksListIntent

    data class AddNotebook(val id: Int = 0, val name: String) : NotebooksListIntent
    data class ModifyNotebook(val notebook: Notebook) : NotebooksListIntent
    data class DeleteNotebook(val notebook: Notebook) : NotebooksListIntent
}

sealed interface NotebooksListSingleEvent : MviSingleEvent
