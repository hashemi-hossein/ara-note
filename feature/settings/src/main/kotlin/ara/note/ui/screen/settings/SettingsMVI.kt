package ara.note.ui.screen.settings

import android.net.Uri
import ara.note.data.model.UserPreferences
import ara.note.ui.MviIntent
import ara.note.ui.MviSingleEvent
import ara.note.ui.MviState
import kotlin.reflect.KProperty1

data class SettingsState(
    val userPreferences: UserPreferences = UserPreferences(),
) : MviState

sealed interface SettingsIntent : MviIntent {
    object ObserveUserPreferences : SettingsIntent
    data class ShowUserPreferences(val userPreferences: UserPreferences) : SettingsIntent

    data class WriteUserPreferences<T>(
        val kProperty: KProperty1<UserPreferences, T>,
        val value: T,
    ) : SettingsIntent

    data class ImportData(val uri: Uri, val onComplete: (result: String) -> Unit) : SettingsIntent
    data class ExportData(val uri: Uri, val onComplete: (result: String) -> Unit) : SettingsIntent
}

sealed interface SettingsSingleEvent : MviSingleEvent
