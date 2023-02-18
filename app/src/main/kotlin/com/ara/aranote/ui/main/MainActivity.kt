package com.ara.aranote.ui.main

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import ara.note.data.datastore.DarkMode
import ara.note.data.datastore.UserPreferences
import ara.note.data.repository.UserPreferencesRepository
import com.ara.aranote.ui.navigation.NavigationGraph
import com.ara.aranote.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var userPreferencesRepository: UserPreferencesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val userPreferences by userPreferencesRepository.observe()
                .collectAsState(initial = UserPreferences())
            val isDark = when (userPreferences.darkMode) {
                DarkMode.LIGHT -> false
                DarkMode.DARK -> true
                DarkMode.SYSTEM -> isSystemInDarkTheme()
            }
            AppTheme(darkTheme = isDark) {
                NavigationGraph()
            }
        }
    }
}
