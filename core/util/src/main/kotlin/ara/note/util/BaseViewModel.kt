package ara.note.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

interface MviState

interface MviIntent

interface MviSingleEvent

abstract class BaseViewModel<S : MviState, I : MviIntent, E : MviSingleEvent> : ViewModel() {

    private val initialState: S by lazy { initialState() }

    protected abstract fun initialState(): S

    private val _uiState by lazy { MutableStateFlow(initialState) }
    val uiState by lazy { _uiState.asStateFlow() }

    private val _intent = MutableSharedFlow<I>()

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
        _uiState.update { newState }
    }

    protected abstract suspend fun handleIntent(intent: I, state: S)

    fun sendIntent(intent: I) {
        viewModelScope.launch {
            _intent.emit(intent)
        }
    }

    private val _singleEvent = MutableSharedFlow<E>()
    val singleEvent = _singleEvent.asSharedFlow()

    fun triggerSingleEvent(singleEvent: E) {
        viewModelScope.launch { _singleEvent.emit(singleEvent) }
    }

    private val longRunningJobs = hashMapOf<String, Job>()

    fun observeFlow(
        id: String,
        block: suspend () -> Unit,
    ) {
        cancelFlow(id)
        longRunningJobs[id] = viewModelScope.launch {
            block()
        }
    }

    fun cancelFlow(id: String) {
        if (id in longRunningJobs.keys && longRunningJobs[id]?.isActive == true) {
            longRunningJobs[id]?.cancel()
        }
    }

    protected abstract val reducer: Reducer<S, I>

    interface Reducer<S, I> {
        fun reduce(state: S, intent: I): S
    }
}
