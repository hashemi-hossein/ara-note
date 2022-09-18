package com.ara.aranote.ui.screen.notebooks_list

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ara.aranote.R
import com.ara.aranote.domain.entity.Notebook
import com.ara.aranote.ui.component.HAppBar
import com.ara.aranote.ui.component.HSnackbarHost
import com.ara.aranote.ui.component.showSnackbar
import com.ara.aranote.util.DEFAULT_NOTEBOOK_ID
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun NotebooksListScreen(
    viewModel: NotebooksListViewModel,
    navigateUp: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    NotebooksListScreen(
        navigateUp = navigateUp,
        uiState = uiState,
        addNotebook = { viewModel.sendIntent(NotebooksListIntent.AddNotebook(name = it)) },
        modifyNotebook = { viewModel.sendIntent(NotebooksListIntent.ModifyNotebook(it)) },
        deleteNotebook = { viewModel.sendIntent(NotebooksListIntent.DeleteNotebook(it)) },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotebooksListScreen(
    navigateUp: () -> Unit,
    uiState: NotebooksListState,
    addNotebook: (String) -> Unit = {},
    modifyNotebook: (Notebook) -> Unit = {},
    deleteNotebook: (Notebook) -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    scope: CoroutineScope = rememberCoroutineScope(),
    context: Context = LocalContext.current,
) {
    var selectedNotebook by remember { mutableStateOf<Notebook?>(null) }
    var dialogType by remember { mutableStateOf(DialogType.HIDE) }
    val setDialogType: (DialogType) -> Unit = { dialogType = it }

    Scaffold(
        snackbarHost = { HSnackbarHost(hostState = snackbarHostState) },
        topBar = {
            HAppBar(title = "Notebooks", onNavButtonClick = navigateUp, actions = {
                IconButton(onClick = { setDialogType(DialogType.ADD_NOTEBOOK) }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.cd_add_notebook)
                    )
                }
            })
        },
    ) { innerPadding ->
        LazyColumn(modifier = Modifier.padding(innerPadding)) {
            items(uiState.notebooks) { notebook ->
                ListItem(
                    headlineText = {
                        Text(text = notebook.name, style = MaterialTheme.typography.bodyLarge)
                    },
                    trailingContent = {
                        Row {
                            IconButton(onClick = {
                                selectedNotebook = notebook
                                setDialogType(DialogType.EDIT_NOTEBOOK)
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = stringResource(R.string.edit_notebook),
                                    modifier = Modifier.alpha(0.3f)
                                )
                            }
                            if (notebook.id != DEFAULT_NOTEBOOK_ID)
                                IconButton(onClick = {
                                    showSnackbar(
                                        scope,
                                        snackbarHostState,
                                        message = "Do you confirm deleting ${notebook.name} notebook and all its notes?",
                                        actionLabel = context.getString(R.string.delete)
                                    ) {
                                        deleteNotebook(notebook)
                                    }
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = stringResource(R.string.cd_delete_notebook),
                                        modifier = Modifier.alpha(0.3f)
                                    )
                                }
                        }
                    },
                )
            }
        }
        HDialog(
            dialogType = dialogType,
            setDialogType = setDialogType,
            addNotebook = addNotebook,
            selectedNotebook = selectedNotebook,
            modifyNotebook = modifyNotebook,
        )
    }
}

private enum class DialogType {
    HIDE, ADD_NOTEBOOK, EDIT_NOTEBOOK
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HDialog(
    dialogType: DialogType,
    setDialogType: (DialogType) -> Unit,
    addNotebook: (String) -> Unit,
    selectedNotebook: Notebook?,
    modifyNotebook: (Notebook) -> Unit,
) {
    if (dialogType != DialogType.HIDE) {
        var text by rememberSaveable {
            mutableStateOf(
                if (dialogType == DialogType.ADD_NOTEBOOK || selectedNotebook == null) ""
                else selectedNotebook.name
            )
        }
        AlertDialog(
            onDismissRequest = { setDialogType(DialogType.HIDE) },
            confirmButton = {
                IconButton(onClick = {
                    setDialogType(DialogType.HIDE)
                    if (dialogType == DialogType.ADD_NOTEBOOK) addNotebook(text)
                    else if (selectedNotebook != null) modifyNotebook(selectedNotebook.copy(name = text))
                }) {
                    Icon(
                        imageVector = Icons.Default.Done,
                        contentDescription = stringResource(R.string.cd_dialog_confirm)
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = if (dialogType == DialogType.ADD_NOTEBOOK) stringResource(R.string.add_notebook)
                        else stringResource(R.string.edit_notebook),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                    )
                    TextField(
                        value = text,
                        onValueChange = { text = it },
                        singleLine = true,
                    )
                }
            },
        )
    }
}
