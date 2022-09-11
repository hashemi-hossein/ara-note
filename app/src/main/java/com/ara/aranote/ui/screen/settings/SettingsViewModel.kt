package com.ara.aranote.ui.screen.settings

import com.ara.aranote.data.datastore.AppDataStore
import com.ara.aranote.util.BaseViewModel
import com.ara.aranote.util.HDataBackup
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel
@Inject constructor(
    val appDataStore: AppDataStore,
    private val hDataBackup: HDataBackup,
) : BaseViewModel<SettingsState, SettingsIntent, SettingsSingleEvent>() {

    override fun initialState(): SettingsState = SettingsState

    override suspend fun handleIntent(intent: SettingsIntent, state: SettingsState) {
        when (intent) {
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
            is SettingsIntent.ExportData -> state
            is SettingsIntent.ImportData -> state
        }
}
