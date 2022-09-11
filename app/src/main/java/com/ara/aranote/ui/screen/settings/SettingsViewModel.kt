package com.ara.aranote.ui.screen.settings

import com.ara.aranote.domain.usecase.user_preferences.ObserveUserPreferencesUseCase
import com.ara.aranote.domain.usecase.user_preferences.WriteUserPreferencesUseCase
import com.ara.aranote.util.BaseViewModel
import com.ara.aranote.util.HDataBackup
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

            is SettingsIntent.ExportData -> hDataBackup.importData(intent.uri, intent.onComplete)
            is SettingsIntent.ImportData -> hDataBackup.exportData(intent.uri, intent.onComplete)
        }
    }

    override val reducer: Reducer<SettingsState, SettingsIntent>
        get() = SettingsReducer()
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
