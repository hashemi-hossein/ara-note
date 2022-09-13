package com.ara.aranote.data.datastore

import kotlinx.serialization.Serializable

@Serializable
data class UserPreferences(
    val isDark: Boolean = false,
    val isAutoSaveMode: Boolean = true,
    val noteColor: Long = -43230,
    val isDoubleBackToExitMode: Boolean = false,
    val doesDefaultNotebookExist: Boolean = false,
)
