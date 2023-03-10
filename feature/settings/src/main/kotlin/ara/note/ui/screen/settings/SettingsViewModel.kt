package ara.note.ui.screen.settings

import ara.note.backup.HDataBackup
import ara.note.domain.usecase.userpreferences.ObserveUserPreferencesUseCase
import ara.note.domain.usecase.userpreferences.WriteUserPreferencesUseCase
import ara.note.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel
@Inject constructor(
    private val observeUserPreferencesUseCase: ObserveUserPreferencesUseCase,
    private val writeUserPreferencesUseCase: WriteUserPreferencesUseCase,
    private val hDataBackup: HDataBackup,
) : BaseViewModel<SettingsState, SettingsIntent, SettingsSingleEvent>() {

    override fun initialState(): SettingsState = SettingsState()
    override val reducer: Reducer<SettingsState, SettingsIntent> = SettingsReducer()

    init {
        sendIntent(SettingsIntent.ObserveUserPreferences)
    }

    override suspend fun handleIntent(intent: SettingsIntent, state: SettingsState) {
        when (intent) {
            is SettingsIntent.ObserveUserPreferences ->
                observeFlow("Settings_observeUserPreferences") {
                    observeUserPreferencesUseCase().collect {
                        sendIntent(SettingsIntent.ShowUserPreferences(it))
                    }
                }
            is SettingsIntent.ShowUserPreferences -> Unit

            is SettingsIntent.WriteUserPreferences<*> ->
                writeUserPreferencesUseCase(intent.kProperty, intent.value)

            is SettingsIntent.ExportData -> hDataBackup.exportData(intent.uri, intent.onComplete)
            is SettingsIntent.ImportData -> hDataBackup.importData(intent.uri, intent.onComplete)
        }
    }
}

internal class SettingsReducer : BaseViewModel.Reducer<SettingsState, SettingsIntent> {

    override fun reduce(state: SettingsState, intent: SettingsIntent): SettingsState =
        when (intent) {
            is SettingsIntent.ObserveUserPreferences -> state
            is SettingsIntent.ShowUserPreferences -> state.copy(userPreferences = intent.userPreferences)

            is SettingsIntent.WriteUserPreferences<*> -> state

            is SettingsIntent.ExportData -> state
            is SettingsIntent.ImportData -> state
        }
}
