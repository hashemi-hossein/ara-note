package com.ara.aranote.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.ara.aranote.data.datastore.AppDataStore
import com.ara.aranote.domain.viewmodels.SettingsViewModel
import com.ara.aranote.ui.components.HAppBar

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    navigateUp: () -> Unit,
) {
    val isDark by viewModel.appDataStore.isDark.collectAsState(initial = false)

    SettingsScreen(
        navigateUp = navigateUp,
        isDark = isDark,
        setIsDark = { viewModel.appDataStore.writePref(AppDataStore.DARK_THEME_KEY, it) },
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun SettingsScreen(
    navigateUp: () -> Unit,
    isDark: Boolean,
    setIsDark: (Boolean) -> Unit,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
) {
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = { HAppBar(onNavButtonClick = navigateUp) },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            ListItem(trailing = {
                Switch(checked = isDark, onCheckedChange = setIsDark)
            }) {
                Text(text = "Dark Theme")
            }
        }
    }
}
