package com.ara.aranote.ui.screen.notebookslist

import com.ara.aranote.domain.entity.Notebook
import com.ara.aranote.domain.usecase.home.ObserveNotebooksUseCase
import com.ara.aranote.domain.usecase.notebookslist.CreateNotebookUseCase
import com.ara.aranote.domain.usecase.notebookslist.DeleteNotebookUseCase
import com.ara.aranote.domain.usecase.notebookslist.UpdateNotebookUseCase
import com.ara.aranote.util.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NotebooksListViewModel
@Inject constructor(
    private val observeNotebooksUseCase: ObserveNotebooksUseCase,
    private val createNotebookUseCase: CreateNotebookUseCase,
    private val updateNotebookUseCase: UpdateNotebookUseCase,
    private val deleteNotebookUseCase: DeleteNotebookUseCase,
) : BaseViewModel<NotebooksListState, NotebooksListIntent, NotebooksListSingleEvent>() {

    override fun initialState(): NotebooksListState = NotebooksListState()

    init {
        sendIntent(NotebooksListIntent.ObserveNotebooks)
    }

    override suspend fun handleIntent(
        intent: NotebooksListIntent,
        state: NotebooksListState,
    ) {
        when (intent) {
            NotebooksListIntent.ObserveNotebooks -> {
                observeFlow("NotebooksList_observeNotebooks") {
                    observeNotebooksUseCase().collect {
                        sendIntent(NotebooksListIntent.ShowNotebooks(it))
                    }
                }
            }
            is NotebooksListIntent.ShowNotebooks -> Unit

            is NotebooksListIntent.AddNotebook ->
                createNotebookUseCase(Notebook(id = intent.id, name = intent.name))
            is NotebooksListIntent.ModifyNotebook -> updateNotebookUseCase(intent.notebook)
            is NotebooksListIntent.DeleteNotebook -> deleteNotebookUseCase(intent.notebook)
        }
    }

    override val reducer: Reducer<NotebooksListState, NotebooksListIntent>
        get() = NotebooksListReducer()
}

internal class NotebooksListReducer :
    BaseViewModel.Reducer<NotebooksListState, NotebooksListIntent> {

    override fun reduce(
        state: NotebooksListState,
        intent: NotebooksListIntent,
    ): NotebooksListState = when (intent) {
        is NotebooksListIntent.ObserveNotebooks -> state
        is NotebooksListIntent.ShowNotebooks -> state.copy(notebooks = intent.notebooks)
        is NotebooksListIntent.AddNotebook -> state
        is NotebooksListIntent.ModifyNotebook -> state
        is NotebooksListIntent.DeleteNotebook -> state
    }
}
