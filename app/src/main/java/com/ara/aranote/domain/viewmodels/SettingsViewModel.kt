package com.ara.aranote.domain.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ara.aranote.data.datastore.AppDataStore
import com.ara.aranote.util.HDataBackup
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel
@Inject
constructor(
    val appDataStore: AppDataStore,
    private val hDataBackup: HDataBackup,
) : ViewModel() {

    fun importData(uri: Uri, onComplete: () -> Unit) = viewModelScope.launch {
        hDataBackup.importData(uri, onComplete)
    }

    fun exportData(uri: Uri, onComplete: () -> Unit) = viewModelScope.launch {
        hDataBackup.exportData(uri, onComplete)
    }
}
