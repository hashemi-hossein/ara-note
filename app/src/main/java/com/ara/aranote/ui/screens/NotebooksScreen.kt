package com.ara.aranote.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.AlertDialog
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import com.ara.aranote.R
import com.ara.aranote.domain.entity.Notebook
import com.ara.aranote.domain.viewmodels.NotebooksViewModel
import com.ara.aranote.ui.components.HAppBar
import com.ara.aranote.ui.components.showSnackbar
import kotlinx.coroutines.CoroutineScope

@Composable
fun NotebooksScreen(
    viewModel: NotebooksViewModel,
    navigateUp: () -> Unit,
) {
    val notebooks by viewModel.notebooks.collectAsState()

    NotebooksScreen(
        navigateUp = navigateUp,
        notebooks = notebooks,
        addNotebook = { viewModel.addNotebook(name = it) },
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NotebooksScreen(
    navigateUp: () -> Unit,
    notebooks: List<Notebook>,
    addNotebook: (String) -> Unit = {},
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    scope: CoroutineScope = rememberCoroutineScope(),
    context: Context = LocalContext.current,
) {
    var isDialogVisible by remember { mutableStateOf(false) }
    val setDialogVisibility: (Boolean) -> Unit = { isDialogVisible = it }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            HAppBar(title = "Notebooks", onNavButtonClick = navigateUp, actions = {
                IconButton(onClick = { setDialogVisibility(true) }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.cd_add_notebook)
                    )
                }
            })
        },
    ) { innerPadding ->
        LazyColumn(modifier = Modifier.padding(innerPadding)) {
            items(notebooks) { notebook ->
                ListItem(trailing = {
                    Row {
                        IconButton(onClick = {
                        }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                modifier = Modifier.alpha(0.3f)
                            )
                        }
                        IconButton(onClick = {
                            showSnackbar(scope, scaffoldState.snackbarHostState) {
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null,
                                modifier = Modifier.alpha(0.3f)
                            )
                        }
                    }
                }) {
                    Text(
                        text = notebook.name,
                        style = MaterialTheme.typography.body1,
                    )
                }
            }
        }
        HDialog(
            isDialogVisible = isDialogVisible,
            setDialogVisibility = setDialogVisibility,
            addNotebook = addNotebook,
        )
    }
}

@Composable
private fun HDialog(
    isDialogVisible: Boolean,
    setDialogVisibility: (Boolean) -> Unit,
    addNotebook: (String) -> Unit,
) {
    if (isDialogVisible) {
        var text by rememberSaveable { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { setDialogVisibility(false) },
            confirmButton = {
                IconButton(onClick = {
                    setDialogVisibility(false)
                    addNotebook(text)
                }) {
                    Icon(
                        imageVector = Icons.Default.Done,
                        contentDescription = stringResource(R.string.cd_confirm_adding_notebook)
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = stringResource(R.string.add_notebook),
                        style = MaterialTheme.typography.body1,
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
