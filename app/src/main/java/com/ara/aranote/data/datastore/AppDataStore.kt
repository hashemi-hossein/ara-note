package com.ara.aranote.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppDataStore
@Inject constructor(@ApplicationContext private val context: Context) {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app")

    private val scope = CoroutineScope(IO)

    fun <T> writePref(key: Preferences.Key<T>, value: T) {
        scope.launch {
            context.dataStore.edit { preferences ->
                preferences[key] = value
            }
        }
    }

    suspend fun <T> readPref(key: Preferences.Key<T>, defaultValue: T): T =
        context.dataStore.data.first()[key] ?: defaultValue

    private fun <T> flowOfPref(key: Preferences.Key<T>, defaultValue: T): Flow<T> =
        context.dataStore.data.map { it[key] ?: defaultValue }

    companion object {
        val DEFAULT_NOTEBOOK_EXISTENCE_KEY =
            booleanPreferencesKey("default_notebook_existence_key")
        val DARK_THEME_KEY = booleanPreferencesKey("dark_theme_key")

        val AUTO_SAVE_MODE = booleanPreferencesKey("auto_save_mode")
        val NOTE_COLOR = longPreferencesKey("note_color")
    }

    val isDark: Flow<Boolean> = flowOfPref(DARK_THEME_KEY, false)
    val isAutoSaveMode: Flow<Boolean> = flowOfPref(AUTO_SAVE_MODE, true)
    val noteColor: Flow<Long> = flowOfPref(NOTE_COLOR, -43230)
}
