package com.ara.aranote.ui.screen.home

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ara.aranote.R
import com.ara.aranote.domain.entity.Note
import com.ara.aranote.domain.entity.Notebook
import com.ara.aranote.ui.component.AppBarNavButtonType
import com.ara.aranote.ui.component.HAppBar
import com.ara.aranote.ui.component.NoteCard
import com.ara.aranote.ui.component.showSnackbar
import com.ara.aranote.util.DEFAULT_NOTEBOOK_ID
import com.ara.aranote.util.INVALID_NOTE_ID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    navigateToSettingsScreen: () -> Unit,
    navigateToNotebooksScreen: () -> Unit,
    navigateToNoteDetailScreen: (Int, Int) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HomeScreen(
        uiState = uiState,
        navigateToSettingsScreen = navigateToSettingsScreen,
        navigateToNotebooksScreen = navigateToNotebooksScreen,
        navigateToNoteDetailScreen = { navigateToNoteDetailScreen(it, uiState.currentNotebookId) },
        setCurrentNotebookId = { viewModel.sendIntent(HomeIntent.ChangeNotebook(it)) },
    )
}

@Composable
internal fun HomeScreen(
    uiState: HomeState,
    navigateToNoteDetailScreen: (Int) -> Unit,
    navigateToSettingsScreen: () -> Unit,
    navigateToNotebooksScreen: () -> Unit,
    setCurrentNotebookId: (Int) -> Unit = {},
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    scope: CoroutineScope = rememberCoroutineScope(),
    context: Context = LocalContext.current,
) {
    var lastTimeMillis by remember { mutableStateOf(0L) }
    val listState: LazyListState = rememberLazyListState()

    BackHandler(
        onBack = {
            when {
                scaffoldState.drawerState.isOpen && !scaffoldState.drawerState.isAnimationRunning ->
                    scope.launch { scaffoldState.drawerState.close() }
                uiState.currentNotebookId != DEFAULT_NOTEBOOK_ID -> {
                    setCurrentNotebookId(DEFAULT_NOTEBOOK_ID)
                    scope.launch {
                        listState.animateScrollToItem(0)
                    }
                }
                !uiState.userPreferences.isDoubleBackToExitMode || System.currentTimeMillis() - lastTimeMillis < 2000 ->
                    (context as AppCompatActivity).finish()
                else -> {
                    lastTimeMillis = System.currentTimeMillis()
                    showSnackbar(
                        scope = scope,
                        snackbarHostState = scaffoldState.snackbarHostState,
                        message = context.getString(R.string.press_back_again_to_exit),
                        actionLabel = context.getString(R.string.exit),
                        timeout = 2000,
                    ) {
                        (context as AppCompatActivity).finish()
                    }
                }
            }
        }
    )
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            HAppBar(
                title = uiState.notebooks.find { it.id == uiState.currentNotebookId }?.name
                    ?: stringResource(id = R.string.app_name),
                appBarNavButtonType = AppBarNavButtonType.MENU,
                actions = {
                    IconButton(onClick = { /*todo*/ }) {
                        Icon(imageVector = Icons.Default.Search, contentDescription = null)
                    }
                }
            ) {
                scope.launch { scaffoldState.drawerState.open() }
            }
        },
        drawerContent = {
            HDrawer(
                notebooks = uiState.notebooks,
                currentNotebookId = uiState.currentNotebookId,
                setCurrentNotebookId = {
                    if (it != uiState.currentNotebookId) {
                        setCurrentNotebookId(it)
                        scope.launch {
                            scaffoldState.drawerState.close()
                            listState.animateScrollToItem(0)
                        }
                    }
                },
                navigateToSettingsScreen = {
                    navigateToSettingsScreen()
                    scope.launch { scaffoldState.drawerState.close() }
                },
                navigateToNotebooksScreen = navigateToNotebooksScreen,
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
            notes = uiState.notes,
            navigateToNoteDetailScreen = navigateToNoteDetailScreen,
            noteColor = uiState.userPreferences.noteColor,
            listState = listState,
        )
    }
}

@Composable
private fun HBody(
    innerPadding: PaddingValues,
    notes: List<Note>,
    navigateToNoteDetailScreen: (Int) -> Unit,
    noteColor: Long,
    listState: LazyListState,
) {
    Surface(
        modifier = Modifier
            .padding(innerPadding)
    ) {
        LazyColumn(state = listState) {
            items(notes) { item: Note ->
                NoteCard(
                    note = item,
                    noteColor = noteColor,
                ) {
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
    currentNotebookId: Int,
    setCurrentNotebookId: (Int) -> Unit,
    navigateToSettingsScreen: () -> Unit,
    navigateToNotebooksScreen: () -> Unit,
) {
    HDrawerColumn {
        Column {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
                    .padding(top = 15.dp)
            ) {
                Text(
                    text = stringResource(R.string.notebooks),
                    style = MaterialTheme.typography.h6
                )
                IconButton(onClick = { navigateToNotebooksScreen() }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = stringResource(R.string.cd_goto_notebooks_screen)
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
private fun HDrawerColumn(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Layout(content = content, modifier = modifier) { measurables, constraints ->
        check(measurables.size == 2) { "This component must have tow item" }
        val secondItemPlaceable = measurables[1].measure(constraints)
        val remainedSpace = constraints.maxHeight - secondItemPlaceable.height
        val secondItemConstraints =
            constraints.copy(minHeight = remainedSpace, maxHeight = remainedSpace)
        val placeables = listOf(
            measurables[0].measure(secondItemConstraints),
            secondItemPlaceable,
        )
        var yPosition = 0
        layout(width = constraints.maxWidth, height = constraints.maxHeight) {
            placeables.forEach { placeable ->
                placeable.placeRelative(x = 0, y = yPosition)
                yPosition += placeable.height
            }
        }
    }
}

// @OptIn(ExperimentalTime::class)
// @Preview(
// //    showBackground = true,
// //    backgroundColor = 0xff2ff2f2,
// //    widthDp = 200,
// //    heightDp = 300,
// //    showSystemUi = true,
// )
// @Composable
// private fun HPreview() {
//    val lstNotes = mutableListOf<Note>()
//    val currentDateTime = HDateTime.getCurrentDateTime()
//    for (i in 1..10) {
//        lstNotes.add(
//            Note(
//                id = i,
//                notebookId = DEFAULT_NOTEBOOK_ID,
//                text = "item $i",
//                addedDateTime = currentDateTime.minus((i * i * i * i * i).seconds),
//                alarmDateTime = if (i % 3 == 1) currentDateTime else null,
//            )
//        )
//    }
//    val lstNotebooks = mutableListOf<Notebook>()
//    for (i in 1..3) {
//        lstNotebooks.add(
//            Notebook(id = i, name = "notebook$i")
//        )
//    }
//    HomeScreen(
//        notes = lstNotes,
//        notebooks = lstNotebooks,
//        navigateToNoteDetailScreen = { },
//        navigateToSettingsScreen = {},
//        navigateToNotebooksScreen = {},
//    )
// }
