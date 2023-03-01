package ara.note.ui.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ara.note.data.datastore.UserPreferences
import ara.note.domain.usecase.userpreferences.ObserveUserPreferencesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel
@Inject constructor(
    private val observeUserPreferencesUseCase: ObserveUserPreferencesUseCase,
) : ViewModel() {

    var darkMode by mutableStateOf(UserPreferences().darkMode)

    init {
        viewModelScope.launch {
            observeUserPreferencesUseCase().collect {
                darkMode = it.darkMode
            }
        }
    }
}
