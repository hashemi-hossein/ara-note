package com.ara.aranote.ui.screen.notebookslist

import com.ara.aranote.domain.entity.Notebook
import com.ara.aranote.util.MviIntent
import com.ara.aranote.util.MviSingleEvent
import com.ara.aranote.util.MviState

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
