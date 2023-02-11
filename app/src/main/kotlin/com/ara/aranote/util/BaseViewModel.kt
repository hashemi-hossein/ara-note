package com.ara.aranote.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

interface MviState

interface MviIntent

interface MviSingleEvent

abstract class BaseViewModel<S : MviState, I : MviIntent, E : MviSingleEvent> : ViewModel() {

    private val initialState: S by lazy { initialState() }

    protected abstract fun initialState(): S

    private val _uiState: MutableStateFlow<S> by lazy { MutableStateFlow(initialState) }
    val uiState: StateFlow<S> by lazy { _uiState }

    private val _intent: MutableSharedFlow<I> = MutableSharedFlow()

    init {
        subscribeToIntents()
    }

    private fun subscribeToIntents() {
        viewModelScope.launch {
            _intent.collect {
                reduceInternal(_uiState.value, it)
                launch {
                    handleIntent(it, _uiState.value)
                }
            }
        }
    }

    private fun reduceInternal(prevState: S, intent: I) {
        val newState = reducer.reduce(prevState, intent)
        setState { newState }
    }

    private fun setState(newState: S.() -> S) {
        _uiState.value = uiState.value.newState()
    }

    protected abstract suspend fun handleIntent(intent: I, state: S)

    fun sendIntent(intent: I) {
        viewModelScope.launch {
            _intent.emit(intent)
        }
    }

    private val _singleEvent: MutableSharedFlow<E> = MutableSharedFlow()
    val singleEvent: Flow<E> = _singleEvent

    fun triggerSingleEvent(singleEvent: E) {
        viewModelScope.launch { _singleEvent.emit(singleEvent) }
    }

    private val longRunningJobs: HashMap<String, Job> = hashMapOf()

    fun observeFlow(
        taskId: String,
        isUnique: Boolean = true,
        taskStartedByIntent: suspend () -> Unit,
    ) {
        when {
            taskId in longRunningJobs.keys &&
                !(longRunningJobs[taskId]?.isCompleted ?: true) &&
                isUnique -> {
//                Log.d("CoroutinesViewModel", "Job for intent already working.. Skip execution")
                return
            }
            !isUnique -> {
                if (taskId in longRunningJobs.keys) longRunningJobs[taskId]?.cancel()
            }
        }
        val task = viewModelScope.launch {
            taskStartedByIntent()
        }
        longRunningJobs[taskId] = task
    }

    fun cancelFlow(taskId: String) {
        if (taskId in longRunningJobs.keys && longRunningJobs[taskId]?.isActive == true) {
            longRunningJobs[taskId]?.cancel()
        }
    }

    protected abstract val reducer: Reducer<S, I>

    interface Reducer<S, I> {
        fun reduce(state: S, intent: I): S
    }
}
