package ara.note.util

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

open class CoroutineDispatcherProvider
@Inject constructor() {
    open val main: CoroutineDispatcher = Dispatchers.Main
    open val default: CoroutineDispatcher = Dispatchers.Default
    open val io: CoroutineDispatcher = Dispatchers.IO
    open val unconfined: CoroutineDispatcher = Dispatchers.Unconfined
}
