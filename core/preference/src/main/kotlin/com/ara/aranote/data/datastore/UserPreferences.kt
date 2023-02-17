package com.ara.aranote.data.datastore

import kotlinx.serialization.Serializable

@Serializable
data class UserPreferences(
    val darkMode: DarkMode = DarkMode.SYSTEM,
    val noteViewMode: NoteViewMode = NoteViewMode.GRID,
    val isAutoSaveMode: Boolean = true,
    val isDoubleBackToExitMode: Boolean = false,
)

enum class NoteViewMode { LIST, GRID }
enum class DarkMode { LIGHT, DARK, SYSTEM }
