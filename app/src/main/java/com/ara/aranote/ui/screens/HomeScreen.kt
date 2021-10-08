package com.ara.aranote.ui.screens

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.AlertDialog
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ara.aranote.R
import com.ara.aranote.domain.entity.Note
import com.ara.aranote.domain.entity.Notebook
import com.ara.aranote.domain.viewmodels.HomeViewModel
import com.ara.aranote.ui.components.AppBarNavButtonType
import com.ara.aranote.ui.components.HAppBar
import com.ara.aranote.ui.components.NoteCard
import com.ara.aranote.ui.components.StaggeredVerticalGrid
import com.ara.aranote.ui.components.showSnackbar
import com.ara.aranote.util.DEFAULT_NOTEBOOK_ID
import com.ara.aranote.util.HDateTime
import com.ara.aranote.util.INVALID_NOTE_ID
import com.ara.aranote.util.minus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    navigateToSettingsScreen: () -> Unit,
    navigateToNoteDetailScreen: (Int, Int) -> Unit,
) {
    val notes: List<Note> by viewModel.notes.collectAsState()
    val notebooks: List<Notebook> by viewModel.notebooks.collectAsState()
    val currentNotebookId by viewModel.currentNotebookId.collectAsState()
    val noteColor by viewModel.appDataStore.noteColor.collectAsState(initial = 0)

    HomeScreen(
        notes = notes,
        notebooks = notebooks,
        navigateToSettingsScreen = navigateToSettingsScreen,
        navigateToNoteDetailScreen = { navigateToNoteDetailScreen(it, currentNotebookId) },
        addNotebook = { viewModel.addNotebook(name = it) },
        currentNotebookId = currentNotebookId,
        setCurrentNotebookId = viewModel::setCurrentNotebookId,
        noteColor = noteColor,
    )
}

@Composable
internal fun HomeScreen(
    notes: List<Note>,
    notebooks: List<Notebook>,
    navigateToSettingsScreen: () -> Unit,
    navigateToNoteDetailScreen: (Int) -> Unit,
    addNotebook: (String) -> Unit = {},
    currentNotebookId: Int = DEFAULT_NOTEBOOK_ID,
    setCurrentNotebookId: (Int) -> Unit = {},
    noteColor: Long = 0,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    scope: CoroutineScope = rememberCoroutineScope(),
    context: Context = LocalContext.current,
) {
    var isDialogVisible by remember { mutableStateOf(false) }
    val setDialogVisibility: (Boolean) -> Unit = { isDialogVisible = it }
    var lastTimeMillis = 0L

    BackHandler(
        onBack = {
            when {
                scaffoldState.drawerState.isOpen && !scaffoldState.drawerState.isAnimationRunning ->
                    scope.launch { scaffoldState.drawerState.close() }
                currentNotebookId != DEFAULT_NOTEBOOK_ID -> setCurrentNotebookId(DEFAULT_NOTEBOOK_ID)
                System.currentTimeMillis() - lastTimeMillis < 2000 -> (context as AppCompatActivity).finish()
                else -> {
                    showSnackbar(
                        scope = scope,
                        snackbarHostState = scaffoldState.snackbarHostState,
                        message = context.getString(R.string.press_back_again_to_exit),
                        actionLabel = context.getString(R.string.exit),
                        timeout = 2000,
                    ) {
                        (context as AppCompatActivity).finish()
                    }
                    lastTimeMillis = System.currentTimeMillis()
                }
            }
        }
    )
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            HAppBar(
                title = stringResource(id = R.string.app_name),
                appBarNavButtonType = AppBarNavButtonType.MENU,
            ) {
                scope.launch { scaffoldState.drawerState.open() }
            }
        },
        drawerContent = {
            HDrawer(
                notebooks = notebooks,
                setDialogVisibility = setDialogVisibility,
                currentNotebookId = currentNotebookId,
                setCurrentNotebookId = {
                    if (it != currentNotebookId) {
                        setCurrentNotebookId(it)
                        scope.launch { scaffoldState.drawerState.close() }
                    }
                },
                navigateToSettingsScreen = {
                    navigateToSettingsScreen()
                    scope.launch { scaffoldState.drawerState.close() }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navigateToNoteDetailScreen(INVALID_NOTE_ID)
            }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.cd_add_note),
                )
            }
        },
    ) { innerPadding ->
        HBody(
            innerPadding = innerPadding,
            notes = notes,
            navigateToNoteDetailScreen = navigateToNoteDetailScreen,
            noteColor = noteColor,
        )
        HDialog(
            isDialogVisible = isDialogVisible,
            setDialogVisibility = setDialogVisibility,
            addNotebook = addNotebook,
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun HBody(
    innerPadding: PaddingValues,
    notes: List<Note>,
    navigateToNoteDetailScreen: (Int) -> Unit,
    noteColor: Long,
) {
    Surface(
        modifier = Modifier
            .padding(innerPadding)
    ) {
        StaggeredVerticalGrid(maxColumnWidth = 220.dp) {
            notes.forEach { item: Note ->
                NoteCard(note = item, noteColor = noteColor) {
                    navigateToNoteDetailScreen(item.id)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun HDrawer(
    notebooks: List<Notebook>,
    setDialogVisibility: (Boolean) -> Unit,
    currentNotebookId: Int,
    setCurrentNotebookId: (Int) -> Unit,
    navigateToSettingsScreen: () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxSize()
    ) {
        Column {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 15.dp)
            ) {
                Text(
                    text = stringResource(R.string.notebooks),
                    style = MaterialTheme.typography.h6
                )
                IconButton(onClick = { setDialogVisibility(true) }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.cd_add_notebook)
                    )
                }
            }
            LazyColumn(modifier = Modifier.selectableGroup()) {
                items(notebooks) { item: Notebook ->
                    Surface(
                        color = if (item.id == currentNotebookId)
                            MaterialTheme.colors.primary.copy(alpha = 0.15f) else MaterialTheme.colors.surface,
                        modifier = Modifier
                            .selectable(
                                selected = item.id == currentNotebookId,
                                role = Role.RadioButton
                            ) { setCurrentNotebookId(item.id) },
                    ) {
                        Text(
                            text = item.name,
                            style = MaterialTheme.typography.body1,
                            color = MaterialTheme.colors.primary,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(15.dp)
                        )
                    }
                }
            }
        }
        Column {
            Divider()
            ListItem(
                icon = { Icon(imageVector = Icons.Default.Settings, contentDescription = null) },
                modifier = Modifier
                    .clickable { navigateToSettingsScreen() }
            ) {
                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.body1,
                )
            }
        }
    }
}

@Composable
fun HDialog(
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

@OptIn(ExperimentalTime::class)
@Preview(
//    showBackground = true,
//    backgroundColor = 0xff2ff2f2,
//    widthDp = 200,
//    heightDp = 300,
//    showSystemUi = true,
)
@Composable
private fun HPreview() {
    val lstNotes = mutableListOf<Note>()
    val currentDateTime = HDateTime.getCurrentDateTime()
    for (i in 1..10) {
        lstNotes.add(
            Note(
                id = i,
                notebookId = 1,
                text = "item $i",
                addedDateTime = currentDateTime.minus(Duration.seconds(i * i * i * i * i)),
                alarmDateTime = if (i % 3 == 1) currentDateTime else null,
            )
        )
    }
    val lstNotebooks = mutableListOf<Notebook>()
    for (i in 1..3) {
        lstNotebooks.add(
            Notebook(id = i, name = "notebook$i")
        )
    }
    HomeScreen(
        notes = lstNotes,
        notebooks = lstNotebooks,
        navigateToNoteDetailScreen = { },
        navigateToSettingsScreen = {},
    )
}
