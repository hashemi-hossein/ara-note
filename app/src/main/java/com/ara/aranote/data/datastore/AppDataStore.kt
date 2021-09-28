package com.ara.aranote.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AppDataStore
@Inject constructor(@ApplicationContext private val context: Context) {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app")

    suspend fun readDefaultNotebookExistence() =
        context.dataStore.data.map { preferences ->
            preferences[DEFAULT_NOTEBOOK_EXISTENCE_KEY] ?: false
        }.first()

    suspend fun writeDefaultNotebookExistence() = context.dataStore.edit { settings ->
        settings[DEFAULT_NOTEBOOK_EXISTENCE_KEY] = true
    }

    companion object {
        private val DEFAULT_NOTEBOOK_EXISTENCE_KEY =
            booleanPreferencesKey("default_notebook_existence_key")
    }
}
