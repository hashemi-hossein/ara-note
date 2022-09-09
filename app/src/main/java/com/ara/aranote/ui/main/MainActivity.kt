package com.ara.aranote.ui.main

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.collectAsState
import com.ara.aranote.data.datastore.AppDataStore
import com.ara.aranote.ui.navigation.NavigationGraph
import com.ara.aranote.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var appDataStore: AppDataStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme(darkTheme = appDataStore.isDark.collectAsState(initial = false).value) {
                NavigationGraph()
            }
        }
    }
}
