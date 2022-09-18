package com.ara.aranote.ui.screen.settings

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.color.colorChooser
import com.ara.aranote.R
import com.ara.aranote.data.datastore.UserPreferences
import com.ara.aranote.ui.component.HAppBar
import com.ara.aranote.ui.component.HSnackbarHost
import com.ara.aranote.ui.component.showSnackbar
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    navigateUp: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SettingsScreen(
        navigateUp = navigateUp,
        userPreferences = uiState.userPreferences,
        setIsDark = { viewModel.sendIntent(SettingsIntent.WriteUserPreferences(UserPreferences::isDark, it)) },
        setIsAutoSaveMode = {
            viewModel.sendIntent(SettingsIntent.WriteUserPreferences(UserPreferences::isAutoSaveMode, it))
        },
        setNoteColor = { viewModel.sendIntent(SettingsIntent.WriteUserPreferences(UserPreferences::noteColor, it)) },
        setIsDoubleBackToExitMode = {
            viewModel.sendIntent(SettingsIntent.WriteUserPreferences(UserPreferences::isDoubleBackToExitMode, it))
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
    setNoteColor: (Long) -> Unit,
    setIsDoubleBackToExitMode: (Boolean) -> Unit,
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
                    Switch(checked = userPreferences.isAutoSaveMode,
                        onCheckedChange = setIsAutoSaveMode)
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
                headlineText = { Text(text = "Note Color") },
                trailingContent = {
                    Box(
                        modifier = Modifier
                            .size(35.dp)
                            .clip(CircleShape)
                            .background(color = Color(userPreferences.noteColor))
                            .clickable {
                                showMaterialDialog(context, userPreferences, setNoteColor)
                            }
                    )
                },
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

private fun showMaterialDialog(
    context: Context,
    userPreferences: UserPreferences,
    setNoteColor: (Long) -> Unit,
) {
    val colors = intArrayOf(
        android.graphics.Color.parseColor("#FF5722"),
        android.graphics.Color.parseColor("#d50000"),
        android.graphics.Color.parseColor("#c51162"),
        android.graphics.Color.parseColor("#aa00ff"),
        android.graphics.Color.parseColor("#6200ea"),
        android.graphics.Color.parseColor("#304ffe"),
        android.graphics.Color.parseColor("#2962ff"),
        android.graphics.Color.parseColor("#00796b"),
        android.graphics.Color.parseColor("#2e7d32"),
        android.graphics.Color.parseColor("#33691e"),
        android.graphics.Color.parseColor("#e65100"),
        android.graphics.Color.parseColor("#bf360c"),
        android.graphics.Color.parseColor("#8d6e63"),
        android.graphics.Color.parseColor("#546e7a"),
    )
    MaterialDialog(context).show {
        colorChooser(
            colors,
            initialSelection = userPreferences.noteColor.toInt(),
            allowCustomArgb = true,
        ) { _, color ->
//                                println("color=$color")
            setNoteColor(color.toLong())
        }
        positiveButton(R.string.select)
        negativeButton(R.string.cancel)
    }
}
