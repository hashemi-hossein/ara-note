package com.ara.aranote.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import kotlinx.serialization.Serializable

@Serializable
data class UserPreferences(
    val isDark: Boolean = false,
    val isAutoSaveMode: Boolean = true,
    val noteColor: Long = -43230,
    val isDoubleBackToExitMode: Boolean = false,
    val doesDefaultNotebookExist: Boolean = false,
)

val Context.userPreferencesStore: DataStore<UserPreferences> by dataStore(
    fileName = "UserPreferences.json",
    serializer = UserPreferencesSerializer
)
