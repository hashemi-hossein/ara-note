package com.ara.aranote.domain.viewmodels

import androidx.lifecycle.ViewModel
import com.ara.aranote.data.datastore.AppDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel
@Inject
constructor(
    val appDataStore: AppDataStore,
) : ViewModel()
