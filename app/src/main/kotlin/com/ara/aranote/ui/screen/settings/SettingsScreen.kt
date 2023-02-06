package com.ara.aranote.ui.screen.settings

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.OpenInNewOff
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.ara.aranote.data.datastore.NoteViewMode
import com.ara.aranote.data.datastore.UserPreferences
import com.ara.aranote.ui.component.HAppBar
import com.ara.aranote.ui.component.HDropdown
import com.ara.aranote.ui.component.HSnackbarHost
import com.ara.aranote.ui.component.showSnackbar
import kotlinx.coroutines.CoroutineScope

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    navigateUp: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    
    SettingsScreen(
        navigateUp = navigateUp,
        userPreferences = uiState.userPreferences,
        setIsDark = { viewModel.sendIntent(SettingsIntent.WriteUserPreferences(UserPreferences::isDark, it)) },
        setIsAutoSaveMode = {
            viewModel.sendIntent(SettingsIntent.WriteUserPreferences(UserPreferences::isAutoSaveMode, it))
        },
        setIsDoubleBackToExitMode = {
            viewModel.sendIntent(SettingsIntent.WriteUserPreferences(UserPreferences::isDoubleBackToExitMode, it))
        },
        setNoteViewMode = {
            viewModel.sendIntent(SettingsIntent.WriteUserPreferences(UserPreferences::noteViewMode, it))
        },
        exportData = { uri, onComplete ->
            viewModel.sendIntent(SettingsIntent.ExportData(uri, onComplete))
        },
        importData = { uri, onComplete ->
            viewModel.sendIntent(SettingsIntent.ImportData(uri, onComplete))
        },
    )
}

@SuppressLint("CheckResult")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SettingsScreen(
    navigateUp: () -> Unit,
    userPreferences: UserPreferences,
    setIsDark: (Boolean) -> Unit,
    setIsAutoSaveMode: (Boolean) -> Unit,
    setIsDoubleBackToExitMode: (Boolean) -> Unit,
    setNoteViewMode: (NoteViewMode) -> Unit,
    exportData: (Uri, () -> Unit) -> Unit,
    importData: (Uri, () -> Unit) -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    scope: CoroutineScope = rememberCoroutineScope(),
    context: Context = LocalContext.current,
) {
    Scaffold(
        snackbarHost = { HSnackbarHost(hostState = snackbarHostState) },
        topBar = { HAppBar(title = "Settings", onNavButtonClick = navigateUp) },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            ListItem(
                headlineText = { Text(text = "Dark Theme") },
                trailingContent = {
                    Switch(checked = userPreferences.isDark, onCheckedChange = setIsDark)
                },
            )
            ListItem(
                headlineText = { Text(text = "Auto save Mode") },
                trailingContent = {
                    Switch(
                        checked = userPreferences.isAutoSaveMode,
                        onCheckedChange = setIsAutoSaveMode,
                    )
                },
            )
            ListItem(
                headlineText = { Text(text = "Double back to exit Mode") },
                trailingContent = {
                    Switch(
                        checked = userPreferences.isDoubleBackToExitMode,
                        onCheckedChange = setIsDoubleBackToExitMode
                    )
                },
            )
            ListItem(
                headlineText = { Text(text = "Note View Mode") },
                trailingContent = {
                    HDropdown(
                        items = NoteViewMode.values().associate { it.ordinal to it.name },
                        selectedKey = userPreferences.noteViewMode.ordinal,
                        onItemClick = { setNoteViewMode(NoteViewMode.values()[it]) },
                    )
                }
            )
            Divider()
            
            val activityResultLauncherCreateDocument =
                rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument()) {
                    if (it != null)
                        exportData(it) {
                            showSnackbar(
                                scope = scope,
                                snackbarHostState = snackbarHostState,
                                message = "Operation was done",
                                actionLabel = "OK"
                            )
                        }
                }
            ListItem(
                headlineText = { Text(text = "Export Data") },
                trailingContent = {
                    Button(onClick = {
                        activityResultLauncherCreateDocument.launch("AraNote.txt")
                    }) {
                        Icon(
                            imageVector = Icons.Default.OpenInNewOff,
                            contentDescription = "Export Data"
                        )
                    }
                },
            )
            
            val activityResultLauncherOpenDocument =
                rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) {
                    if (it != null)
                        importData(it) {
                            showSnackbar(
                                scope = scope,
                                snackbarHostState = snackbarHostState,
                                message = "Operation was done",
                                actionLabel = "OK"
                            )
                        }
                }
            ListItem(
                headlineText = { Text(text = "Import Data") },
                trailingContent = {
                    Button(onClick = {
                        activityResultLauncherOpenDocument.launch(arrayOf("text/plain"))
                    }) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Import Data"
                        )
                    }
                },
            )
        }
    }
}
