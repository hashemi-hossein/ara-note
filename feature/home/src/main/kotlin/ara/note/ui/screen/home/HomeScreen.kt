package ara.note.ui.screen.home

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import ara.note.home.R.string
import ara.note.ui.screen.home.HomeIntent.ChangeNotebook
import ara.note.ui.screen.home.HomeIntent.ModifySearchText
import ara.note.data.datastore.NoteViewMode
import com.ara.aranote.domain.entity.Note
import ara.note.ui.component.AppBarNavButtonType.MENU
import ara.note.ui.component.AppDrawer
import ara.note.ui.component.HAppBar
import ara.note.ui.component.HSnackbarHost
import ara.note.ui.component.NoteCard
import ara.note.ui.component.SearchAppBar
import ara.note.ui.component.showSnackbar
import ara.note.util.DEFAULT_NOTEBOOK_ID
import ara.note.util.INVALID_NOTE_ID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    navigateToSettingsScreen: () -> Unit,
    navigateToNotebooksScreen: () -> Unit,
    navigateToNoteDetailScreen: (Int, Int) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    HomeScreen(
        uiState = uiState,
        navigateToSettingsScreen = navigateToSettingsScreen,
        navigateToNotebooksScreen = navigateToNotebooksScreen,
        navigateToNoteDetailScreen = { navigateToNoteDetailScreen(it, uiState.currentNotebookId) },
        setCurrentNotebookId = { viewModel.sendIntent(ChangeNotebook(it)) },
        modifySearchText = { viewModel.sendIntent(ModifySearchText(it)) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HomeScreen(
    uiState: HomeState,
    navigateToNoteDetailScreen: (Int) -> Unit,
    navigateToSettingsScreen: () -> Unit,
    navigateToNotebooksScreen: () -> Unit,
    setCurrentNotebookId: (Int) -> Unit = {},
    modifySearchText: (String?) -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    drawerState: DrawerState = rememberDrawerState(DrawerValue.Closed),
    scope: CoroutineScope = rememberCoroutineScope(),
    context: Context = LocalContext.current,
) {
    var lastTimeMillis by remember { mutableStateOf(0L) }
    val listState: LazyListState = rememberLazyListState()

    BackHandler(
        onBack = {
            when {
                uiState.searchText != null -> modifySearchText(null)
                drawerState.isOpen && !drawerState.isAnimationRunning ->
                    scope.launch { drawerState.close() }
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
                        snackbarHostState = snackbarHostState,
                        message = context.getString(string.press_back_again_to_exit),
                        actionLabel = context.getString(string.exit),
                        timeout = 2000,
                    ) {
                        (context as AppCompatActivity).finish()
                    }
                }
            }
        },
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = uiState.searchText == null,
        drawerContent = {
            AppDrawer(
                notebooks = uiState.notebooks,
                currentNotebookId = uiState.currentNotebookId,
                setCurrentNotebookId = {
                    if (it != uiState.currentNotebookId) {
                        setCurrentNotebookId(it)
                        scope.launch {
                            drawerState.close()
                            listState.animateScrollToItem(0)
                        }
                    }
                },
                navigateToSettingsScreen = {
                    navigateToSettingsScreen()
                    scope.launch { drawerState.close() }
                },
                navigateToNotebooksScreen = navigateToNotebooksScreen,
            )
        },
    ) {
        Scaffold(
            snackbarHost = { HSnackbarHost(hostState = snackbarHostState) },
            topBar = {
                Crossfade(targetState = uiState.searchText == null) {
                    if (it) {
                        HAppBar(
                            title = uiState.notebooks.find { it.id == uiState.currentNotebookId }?.name
                                ?: stringResource(id = string.app_name),
                            appBarNavButtonType = MENU,
                            actions = {
                                IconButton(onClick = { modifySearchText("") }) {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = null
                                    )
                                }
                            },
                        ) {
                            scope.launch { drawerState.open() }
                        }
                    } else {
                        SearchAppBar(
                            searchText = uiState.searchText ?: "",
                            modifySearchText = modifySearchText,
                        )
                    }
                }
            },
            floatingActionButton = {
                FloatingActionButton(onClick = {
                    navigateToNoteDetailScreen(INVALID_NOTE_ID)
                }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(string.cd_add_note),
                    )
                }
            },
        ) { innerPadding ->
            HBody(
                uiState = uiState,
                innerPadding = innerPadding,
                navigateToNoteDetailScreen = navigateToNoteDetailScreen,
                listState = listState,
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun HBody(
    uiState: HomeState,
    innerPadding: PaddingValues,
    navigateToNoteDetailScreen: (Int) -> Unit,
    listState: LazyListState,
) {
    Surface(
        modifier = Modifier
            .padding(innerPadding),
    ) {
        val noteCard: @Composable (Note) -> Unit = {
            NoteCard(note = it) {
                navigateToNoteDetailScreen(it.id)
            }
        }
        when (uiState.userPreferences.noteViewMode) {
            NoteViewMode.LIST -> {
                LazyColumn(state = listState) {
                    items(uiState.notes) { noteCard(it) }
                }
            }
            NoteViewMode.GRID -> {
                LazyVerticalStaggeredGrid(columns = StaggeredGridCells.Fixed(2)) {
                    items(uiState.notes) { noteCard(it) }
                }
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
