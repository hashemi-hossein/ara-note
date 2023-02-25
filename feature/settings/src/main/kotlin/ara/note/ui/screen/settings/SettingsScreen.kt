package ara.note.ui.screen.settings

import android.annotation.SuppressLint
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
import androidx.compose.ui.res.stringResource
import ara.note.data.datastore.DarkMode
import ara.note.data.datastore.NoteViewMode
import ara.note.data.datastore.UserPreferences
import ara.note.settings.R.string
import ara.note.ui.component.HAppBar
import ara.note.ui.component.HDropdown
import ara.note.ui.component.HSnackbarHost
import ara.note.ui.component.showSnackbar
import ara.note.ui.screen.settings.SettingsIntent.ExportData
import ara.note.ui.screen.settings.SettingsIntent.ImportData
import ara.note.ui.screen.settings.SettingsIntent.WriteUserPreferences
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
        setDarkMode = {
            viewModel.sendIntent(
                WriteUserPreferences(
                    UserPreferences::darkMode,
                    it,
                ),
            )
        },
        setIsAutoSaveMode = {
            viewModel.sendIntent(
                WriteUserPreferences(
                    UserPreferences::isAutoSaveMode,
                    it,
                ),
            )
        },
        setIsDoubleBackToExitMode = {
            viewModel.sendIntent(
                WriteUserPreferences(
                    UserPreferences::isDoubleBackToExitMode,
                    it,
                ),
            )
        },
        setNoteViewMode = {
            viewModel.sendIntent(
                WriteUserPreferences(
                    UserPreferences::noteViewMode,
                    it,
                ),
            )
        },
        exportData = { uri, onComplete ->
            viewModel.sendIntent(ExportData(uri, onComplete))
        },
        importData = { uri, onComplete ->
            viewModel.sendIntent(ImportData(uri, onComplete))
        },
    )
}

@SuppressLint("CheckResult")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SettingsScreen(
    navigateUp: () -> Unit,
    userPreferences: UserPreferences,
    setDarkMode: (DarkMode) -> Unit,
    setIsAutoSaveMode: (Boolean) -> Unit,
    setIsDoubleBackToExitMode: (Boolean) -> Unit,
    setNoteViewMode: (NoteViewMode) -> Unit,
    exportData: (Uri, (result: String) -> Unit) -> Unit,
    importData: (Uri, (result: String) -> Unit) -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    scope: CoroutineScope = rememberCoroutineScope(),
) {
    Scaffold(
        snackbarHost = { HSnackbarHost(hostState = snackbarHostState) },
        topBar = { HAppBar(title = stringResource(string.settings), onNavButtonClick = navigateUp) },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            ListItem(
                headlineText = { Text(text = stringResource(string.dark_theme)) },
                trailingContent = {
                    HDropdown(
                        items = DarkMode.values().associate { it.ordinal to it.name },
                        selectedKey = userPreferences.darkMode.ordinal,
                        onItemClick = { setDarkMode(DarkMode.values()[it]) },
                    )
                },
            )
            ListItem(
                headlineText = { Text(text = stringResource(string.note_view_mode)) },
                trailingContent = {
                    HDropdown(
                        items = NoteViewMode.values().associate { it.ordinal to it.name },
                        selectedKey = userPreferences.noteViewMode.ordinal,
                        onItemClick = { setNoteViewMode(NoteViewMode.values()[it]) },
                    )
                },
            )
            ListItem(
                headlineText = { Text(text = stringResource(string.auto_save_mode)) },
                trailingContent = {
                    Switch(
                        checked = userPreferences.isAutoSaveMode,
                        onCheckedChange = setIsAutoSaveMode,
                    )
                },
            )
            ListItem(
                headlineText = { Text(text = stringResource(string.double_back_to_exit_mode)) },
                trailingContent = {
                    Switch(
                        checked = userPreferences.isDoubleBackToExitMode,
                        onCheckedChange = setIsDoubleBackToExitMode,
                    )
                },
            )
            Divider()

            val activityResultLauncherCreateDocument =
                rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("text/plain")) {
                    uri->
                    if (uri != null) {
                        exportData(uri) {
                            showSnackbar(
                                scope = scope,
                                snackbarHostState = snackbarHostState,
                                message = it,
                                actionLabel = "OK",
                            )
                        }
                    }
                }
            ListItem(
                headlineText = { Text(text = stringResource(string.export_data)) },
                trailingContent = {
                    Button(onClick = {
                        activityResultLauncherCreateDocument.launch("AraNote.txt")
                    }) {
                        Icon(
                            imageVector = Icons.Default.OpenInNewOff,
                            contentDescription = stringResource(string.export_data),
                        )
                    }
                },
            )

            val activityResultLauncherOpenDocument =
                rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) {uri->
                    if (uri != null) {
                        importData(uri) {
                            showSnackbar(
                                scope = scope,
                                snackbarHostState = snackbarHostState,
                                message = it,
                                actionLabel = "OK",
                            )
                        }
                    }
                }
            ListItem(
                headlineText = { Text(text = stringResource(string.import_data)) },
                trailingContent = {
                    Button(onClick = {
                        activityResultLauncherOpenDocument.launch(arrayOf("text/plain"))
                    }) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = stringResource(string.import_data),
                        )
                    }
                },
            )
        }
    }
}
