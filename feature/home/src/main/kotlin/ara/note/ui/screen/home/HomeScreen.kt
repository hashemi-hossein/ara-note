package ara.note.ui.screen.home

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.tooling.preview.Preview
import ara.note.domain.entity.Note
import ara.note.domain.entity.Notebook
import ara.note.home.R.string
import ara.note.ui.component.AppBarNavButtonType.MENU
import ara.note.ui.component.HAppBar
import ara.note.ui.component.HSnackbarHost
import ara.note.ui.component.showSnackbar
import ara.note.ui.screen.home.HomeIntent.ChangeNotebook
import ara.note.ui.screen.home.HomeIntent.ModifySearchText
import ara.note.ui.screen.home.component.AppDrawer
import ara.note.ui.screen.home.component.HBody
import ara.note.ui.screen.home.component.SearchAppBar
import ara.note.util.DEFAULT_NOTEBOOK_ID
import ara.note.util.HDateTime
import ara.note.util.INVALID_NOTE_ID
import ara.note.util.minus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.pow
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds

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
        modifySearchText = { viewModel.sendIntent(ModifySearchText(it)) },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HomeScreen(
    uiState: HomeState,
    navigateToNoteDetailScreen: (Int) -> Unit,
    navigateToSettingsScreen: () -> Unit,
    navigateToNotebooksScreen: () -> Unit,
    setCurrentNotebookId: (Int) -> Unit,
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
                    (context as ComponentActivity).finish()
                else -> {
                    lastTimeMillis = System.currentTimeMillis()
                    showSnackbar(
                        scope = scope,
                        snackbarHostState = snackbarHostState,
                        message = context.getString(string.press_back_again_to_exit),
                        actionLabel = context.getString(string.exit),
                        timeout = 2000,
                    ) {
                        (context as ComponentActivity).finish()
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
                                        contentDescription = null,
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
                modifier = Modifier.padding(innerPadding),
                uiState = uiState,
                navigateToNoteDetailScreen = navigateToNoteDetailScreen,
                listState = listState,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun HPreview() {
    val currentDateTime = HDateTime.getCurrentDateTime()
    val random = Random(0)
    val lstNotes = List(30) {
        Note(
            id = it,
            notebookId = DEFAULT_NOTEBOOK_ID,
            text = "item $it ".repeat(random.nextInt(1, 15)),
            modifiedDateTime = currentDateTime.minus((it.toDouble().pow(5)).seconds),
        )
    }
    val lstNotebooks = List(10) {
        Notebook(id = it, name = "notebook$it")
    }
    val uiState = HomeState(notes = lstNotes, notebooks = lstNotebooks)
    HomeScreen(
        uiState = uiState,
        navigateToNoteDetailScreen = {},
        navigateToSettingsScreen = {},
        navigateToNotebooksScreen = {},
        setCurrentNotebookId = {},
        modifySearchText = {},
    )
}
