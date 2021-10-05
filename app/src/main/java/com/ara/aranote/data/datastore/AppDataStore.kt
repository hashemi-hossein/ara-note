package com.ara.aranote.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
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

    suspend fun <T> readPref(key: Preferences.Key<T>, defaultValue: T): T {
        return context.dataStore.data.first()[key] ?: defaultValue
    }

    companion object {
        val DEFAULT_NOTEBOOK_EXISTENCE_KEY =
            booleanPreferencesKey("default_notebook_existence_key")
        val DARK_THEME_KEY = booleanPreferencesKey("dark_theme_key")
    }

    val isDark: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[DARK_THEME_KEY] ?: false
        }
}
