package com.ara.aranote.ui.screen.settings

import android.net.Uri
import com.ara.aranote.util.MviIntent
import com.ara.aranote.util.MviSingleEvent
import com.ara.aranote.util.MviState

object SettingsState : MviState

sealed interface SettingsIntent : MviIntent {
    data class ImportData(val uri: Uri, val onComplete: () -> Unit) : SettingsIntent
    data class ExportData(val uri: Uri, val onComplete: () -> Unit) : SettingsIntent
}

sealed interface SettingsSingleEvent : MviSingleEvent
