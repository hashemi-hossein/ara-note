package com.ara.aranote.ui.screen.home

import com.ara.aranote.domain.usecase.home.CreateDefaultNotebookUseCase
import com.ara.aranote.domain.usecase.home.ObserveNotebooksUseCase
import com.ara.aranote.domain.usecase.home.ObserveNotesUseCase
import com.ara.aranote.domain.usecase.userpreferences.ObserveUserPreferencesUseCase
import com.ara.aranote.util.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel
@Inject constructor(
    private val createDefaultNotebookUseCase: CreateDefaultNotebookUseCase,
    private val observeNotebooksUseCase: ObserveNotebooksUseCase,
    private val observeNotesUseCase: ObserveNotesUseCase,
    private val observeUserPreferencesUseCase: ObserveUserPreferencesUseCase
) : BaseViewModel<HomeState, HomeIntent, HomeSingleEvent>() {

    override fun initialState(): HomeState = HomeState()

    init {
        sendIntent(HomeIntent.ObserveNotes)
        sendIntent(HomeIntent.ObserveNotebooks)
        sendIntent(HomeIntent.ObserveUserPreferences)
    }

    override suspend fun handleIntent(intent: HomeIntent, state: HomeState) {
        when (intent) {
            HomeIntent.ObserveNotes -> {
                observeFlow(taskId = "Home_observeNotes", isUnique = false) {
                    observeNotesUseCase(state.currentNotebookId).collect {
                        sendIntent(HomeIntent.ShowNotes(it))
                    }
                }
            }
            is HomeIntent.ShowNotes -> Unit

            HomeIntent.ObserveNotebooks -> {
                createDefaultNotebookUseCase()
                observeFlow("Home_observeNotebooks") {
                    observeNotebooksUseCase().collect {
                        sendIntent(HomeIntent.ShowNotebooks(it))
                    }
                }
            }
            is HomeIntent.ShowNotebooks -> Unit

            is HomeIntent.ChangeNotebook -> sendIntent(HomeIntent.ObserveNotes)

            is HomeIntent.ObserveUserPreferences ->
                observeFlow("Home_observeUserPreferences") {
                    observeUserPreferencesUseCase().collect {
                        sendIntent(HomeIntent.ShowUserPreferences(it))
                    }
                }
            is HomeIntent.ShowUserPreferences -> Unit
        }
    }

    override val reducer: Reducer<HomeState, HomeIntent>
        get() = HomeReducer()
}

internal class HomeReducer : BaseViewModel.Reducer<HomeState, HomeIntent> {

    override fun reduce(state: HomeState, intent: HomeIntent): HomeState =
        when (intent) {
            is HomeIntent.ObserveNotes -> state
            is HomeIntent.ShowNotes -> state.copy(notes = intent.notes)

            is HomeIntent.ObserveNotebooks -> state
            is HomeIntent.ShowNotebooks -> state.copy(notebooks = intent.notebooks)

            is HomeIntent.ChangeNotebook -> state.copy(currentNotebookId = intent.notebookId)

            is HomeIntent.ObserveUserPreferences -> state
            is HomeIntent.ShowUserPreferences -> state.copy(userPreferences = intent.userPreferences)
        }
}
